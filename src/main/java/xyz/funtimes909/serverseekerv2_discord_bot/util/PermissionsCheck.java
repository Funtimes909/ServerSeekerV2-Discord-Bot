package xyz.funtimes909.serverseekerv2_discord_bot.util;

import xyz.funtimes909.serverseekerv2_discord_bot.Main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class PermissionsCheck {
    public static Set<String> blacklist = new HashSet<>();
    public static Set<String> trustedUsers = new HashSet<>();

    public static void initPermissions() {
        try (
                BufferedReader blacklistReader = new BufferedReader(new FileReader("blacklist.txt"));
                BufferedReader trustedUsersReader = new BufferedReader(new FileReader("trusted_users.txt"));
        )   {
                String line;
                while ((line = blacklistReader.readLine()) != null) {
                    blacklist.add(line);
                }

                while ((line = trustedUsersReader.readLine()) != null) {
                    trustedUsers.add(line);
                }
        } catch (IOException e) {
            Main.logger.error("Failed to read blacklist or trusted users file!");
        }
    }

    public static boolean blacklistCheck(String userId) {
        return blacklist.contains(userId);
    }

    public static boolean trustedUsersCheck(String userId) {
        return trustedUsers.contains(userId);
    }

    public static boolean ownerCheck(String userId) {
        return Main.ownerId.equals(userId);
    }
}