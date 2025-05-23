package xyz.funtimes909.serverseekerv2_discord_bot.util;

import xyz.funtimes909.serverseekerv2_discord_bot.util.Utils.AnsiCodes;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class PingUtils {
    private static final byte[] REQUEST = new byte[] {
            8, // Size: Amount of proceeding bytes [varint]
            0, // ID: Has to be 0
            0, // Protocol Version: Can be anything as long as it's a valid varint
            2, 0x3A, 0x33, // Address: As it is indexed with a varint to state it's size, we can just skip sending anything by setting it's size to 0
            0, 0, // Port: Can be anything (Notchian servers don't use this)
            1, // Next State: 1 for status, 2 for login. Therefore, has to be 1
            1, // Size
            0, // ID
    };

    public static String ping(Socket connection) {
        try (
                OutputStream out = connection.getOutputStream();
                InputStream in = connection.getInputStream();
                connection;
        ) {
            out.write(REQUEST);
            // Skip the first varint which indicates the total size of the packet.
            // Later we properly read a varint that contains the length of the json, so we use that
            for (byte i = 0; i < 5; i ++)
                if ((((byte) in.read()) & 0b10000000) == 0)
                    break;
            // Read the packet id, which should always be 0
            in.read();
            // Properly read the varint. This one contains the length of the following string
            int json_length = VarInt.decode_varint(in);
            // Finally read the bytes
            byte[] status = in.readNBytes(json_length);
            return new String(status);
        } catch (Exception e) {
            return null;
        }
    }

    public static String parseMOTD(String description) {
        StringBuilder motd = new StringBuilder();

        for (String line : description.split("§")) {
            if (line.isBlank() || !AnsiCodes.colors.containsKey(line.charAt(0))) {
                motd.append(line);
                continue;
            }

            int code = AnsiCodes.colors.get(line.charAt(0)).ansi;

            // Use 50 as a reset code
            if (code == 50) {
                motd.append("\u001B[0m").append(line.substring(1));
                continue;
            }

            motd.append(code < 5 ?
                    "\u001B[" + code + ";00m" + line.substring(1) :
                    "\u001B[0;" + code + "m" + line.substring(1)
            );
        }
        return motd.toString();
    }
}