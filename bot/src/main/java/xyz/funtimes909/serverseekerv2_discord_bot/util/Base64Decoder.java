package xyz.funtimes909.serverseekerv2_discord_bot.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class Base64Decoder {
    public static File decode(String base64, String name) {
        byte[] image = Base64.getDecoder().decode(base64);

        try (FileOutputStream stream = new FileOutputStream(name)) {
            stream.write(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new File(name);
    }
}