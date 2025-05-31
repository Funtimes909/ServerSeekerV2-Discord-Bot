package xyz.funtimes909.serverseekerv2_discord_bot.util;

import xyz.funtimes909.serverseekerv2_discord_bot.Main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class PermissionsManager {
    public static Set<String> blacklist = new HashSet<>();
    public static Set<String> servers = new HashSet<>();
    public static Set<String> trustedUsers = new HashSet<>();

    static {
        try (
                BufferedReader blacklistReader = new BufferedReader(new FileReader("blacklist.txt"));
                BufferedReader usersReader = new BufferedReader(new FileReader("trusted_users.txt"));
                BufferedReader serversReader = new BufferedReader(new FileReader("blacklisted_servers.txt"));
        )   {
                String line;
                // Load the blacklist
                while ((line = blacklistReader.readLine()) != null) {
                    blacklist.add(line);
                }

                // Load blacklisted servers
                while ((line = serversReader.readLine()) != null) {
                    servers.add(line);
                }

                // Load the trusted users
                while ((line = usersReader.readLine()) != null) {
                    trustedUsers.add(line);
                }
        } catch (IOException e) {
            Main.logger.error("Failed to read a required file!", e);
        }
    }

    public static boolean blacklistCheck(String userId) {
        return blacklist.contains(userId);
    }

    public static boolean blacklistServerCheck(String userId) {
        return servers.contains(userId);
    }

    public static boolean trustedUsersCheck(String userId) {
        return trustedUsers.contains(userId);
    }

    public static boolean ownerCheck(String userId) {
        return Main.ownerId.equals(userId);
    }
}