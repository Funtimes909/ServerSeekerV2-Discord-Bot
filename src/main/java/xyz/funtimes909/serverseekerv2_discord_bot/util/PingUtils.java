package xyz.funtimes909.serverseekerv2_discord_bot.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import xyz.funtimes909.serverseekerv2_discord_bot.Records.Mod;
import xyz.funtimes909.serverseekerv2_discord_bot.Records.Player;
import xyz.funtimes909.serverseekerv2_discord_bot.Records.Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PingUtils {
    private final String address;
    private final short port;
    private static final byte[] REQUEST = new byte[] {
            6, // Size: Amount of proceeding bytes [varint]
            0, // ID: Has to be 0
            0, // Protocol Version: Can be anything as long as it's a valid varint
            0, // ServerEmbed Address: As it is indexed with a varint to state it's size, we can just skip sending anything by setting it's size to 0
            0, 0, // Port: Can be anything (Notchian servers don't use this)
            1, // Next State: 1 for status, 2 for login. Therefore, has to be 1
            1, // Size
            0, // ID
    };

    public PingUtils(String address, short port) {
        this.address = address;
        this.port = port;
    }

    public Server parse() {
        try (Socket conn = new Socket()) {
            conn.connect(new  InetSocketAddress(address, port));
            String json = ping(conn);
            if (json == null) return null;

            JsonObject parsedJson = JsonParser.parseString(json).getAsJsonObject();
            String version = null;
            String motd = null;
            String icon = null;
            Boolean preventsChatReports = null;
            Boolean enforcesSecureChat = null;
            Boolean cracked = null;
            Integer protocol = null;
            Integer fmlNetworkVersion = null;
            Integer maxPlayers = null;
            Integer onlinePlayers = null;
            List<Player> playerList = new ArrayList<>();
            List<Mod> modsList  = new ArrayList<>();

            // Minecraft server information
            if (parsedJson.has("version")) {
                version = parsedJson.get("version").getAsJsonObject().get("name").getAsString();
                protocol = parsedJson.get("version").getAsJsonObject().get("protocol").getAsInt();
            }

            // Check for icon
            if (parsedJson.has("favicon")) {
                icon = parsedJson.get("favicon").getAsString();
            }

            // Description can be either an object or a string
            if (parsedJson.has("description")) {
                if (parsedJson.get("description").isJsonPrimitive()) {
                    motd = parsedJson.get("description").getAsString();
                } else if (parsedJson.get("description").isJsonObject()) {
                    if (parsedJson.get("description").getAsJsonObject().has("motd")) {
                        motd = parsedJson.get("description").getAsJsonObject().get("text").getAsString();
                    }
                }
            }

            if (parsedJson.has("enforcesSecureChat")) {
                enforcesSecureChat = parsedJson.get("enforcesSecureChat").getAsBoolean();
            }

            if (parsedJson.has("preventsChatReports")) {
                preventsChatReports = parsedJson.get("preventsChatReports").getAsBoolean();
            }

            // Forge servers send back information about mods
            if (parsedJson.has("forgeData")) {
                fmlNetworkVersion = parsedJson.get("forgeData").getAsJsonObject().get("fmlNetworkVersion").getAsInt();
                if (parsedJson.get("forgeData").getAsJsonObject().has("mods")) {
                    for (JsonElement modJson : parsedJson.get("forgeData").getAsJsonObject().get("mods").getAsJsonArray().asList()) {
                        String modId = modJson.getAsJsonObject().get("modId").getAsString();
                        String modmarker = modJson.getAsJsonObject().get("modmarker").getAsString();

                        Mod mod = new Mod(modId, modmarker);
                        modsList.add(mod);
                    }
                }
            }

            // Check for players
            if (parsedJson.has("players")) {
                maxPlayers = parsedJson.get("players").getAsJsonObject().get("max").getAsInt();
                onlinePlayers = parsedJson.get("players").getAsJsonObject().get("online").getAsInt();
                if (parsedJson.get("players").getAsJsonObject().has("sample")) {
                    for (JsonElement playerJson : parsedJson.get("players").getAsJsonObject().get("sample").getAsJsonArray().asList()) {
                        if (playerJson.getAsJsonObject().has("name") && playerJson.getAsJsonObject().has("id")) {
                            String name = playerJson.getAsJsonObject().get("name").getAsString();
                            String uuid = playerJson.getAsJsonObject().get("id").getAsString();

                            // Offline mode servers use v3 UUID's for players, while regular servers use v4, this is a really easy way to check if a server is offline mode
                            if (UUID.fromString(uuid).version() == 3) cracked = true;
                            Player player = new Player(name, uuid, System.currentTimeMillis() / 1000 );
                            playerList.add(player);
                        }
                    }
                }
            }

            // Build server
            return new Server.Builder()
                    .setAddress(address)
                    .setPort(port)
                    .setLastSeen(System.currentTimeMillis() / 1000)
                    .setVersion(version)
                    .setProtocol(protocol)
                    .setFmlNetworkVersion(fmlNetworkVersion)
                    .setMotd(motd)
                    .setIcon(icon)
                    .setPreventsReports(preventsChatReports)
                    .setEnforceSecure(enforcesSecureChat)
                    .setCracked(cracked)
                    .setMaxPlayers(maxPlayers)
                    .setOnlinePlayers(onlinePlayers)
                    .setPlayers(playerList)
                    .setMods(modsList)
                    .build();

        } catch (IOException ignored) {}
        return null;
    }

    public String ping(Socket connection) {
        try (OutputStream out = connection.getOutputStream()) {
            out.write(REQUEST);
            InputStream in = connection.getInputStream();
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
            // Close all resources
            connection.close();
            in.close();
            return new String(status);
        } catch (Exception e) {
            return null;
        }
    }
}
