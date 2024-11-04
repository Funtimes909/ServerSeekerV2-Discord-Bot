package xyz.funtimes909.serverseekerv2_discord_bot.util;

import xyz.funtimes909.serverseekerv2_discord_bot.Main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BlacklistCheck {
    public static boolean check(String userId) {
        try {
            String line;
            BufferedReader blacklist = new BufferedReader(new FileReader("blacklist.txt"));
            while ((line = blacklist.readLine()) != null) {
                if (userId.equals(line)) return true;
            }
        } catch (IOException e) {
            Main.logger.error("Failed to read blacklist.txt");
        }
            return false;
    }
}
