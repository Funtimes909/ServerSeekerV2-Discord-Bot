package xyz.funtimes909.serverseekerv2_discord_bot.util;

import java.io.IOException;
import java.io.InputStream;

public class VarInt {
    public static int decode_varint(InputStream is) throws IOException {
        int val = 0;
        for (int i = 0; i < (7 /* size */ * 5 /* max size */); i += 7) {
            byte byte_read = (byte) is.read();
            // NOTE: 0x7f is 7 ones
            val |= (byte_read & 0x7f) << i;

            if ((byte_read >> 7) == 0)
                break;
        }
        return val;
    }
}
