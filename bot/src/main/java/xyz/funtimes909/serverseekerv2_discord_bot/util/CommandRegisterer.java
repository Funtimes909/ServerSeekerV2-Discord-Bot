package xyz.funtimes909.serverseekerv2_discord_bot.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class CommandRegisterer {
    public static final HashMap<String, String> countries = new HashMap<>();

    static {
        try (BufferedReader reader = new BufferedReader(new FileReader("country-codes.json"))) {
            for (JsonElement country : JsonParser.parseReader(reader).getAsJsonArray()) {
                CommandRegisterer.countries.put(
                        country.getAsJsonObject().get("name").getAsString(),
                        country.getAsJsonObject().get("code").getAsString()
                );
            }
        } catch (IOException e) {
            Main.logger.error("Failed to load country-codes.json");
            throw new RuntimeException(e);
        }
    }

    public static void registerCommands(JDA client) {
        client.updateCommands().addCommands(
                Commands.slash("search", "Searches for servers with advanced filters")
                        .addOption(OptionType.STRING, "version", "Search for servers using a specific minecraft version")
                        .addOption(OptionType.STRING, "description", "Search for servers with a specific description")
                        .addOption(OptionType.INTEGER, "playercount", "Search for servers with a specific amount of players online")
                        .addOption(OptionType.INTEGER, "maxplayers", "Search for servers that have a specific player cap")
                        .addOption(OptionType.BOOLEAN, "preventsreports", "Search for servers that prevent chat reports")
                        .addOption(OptionType.BOOLEAN, "enforcesecure", "Search for servers that enforce secure chat")
                        .addOption(OptionType.BOOLEAN, "cracked", "Search for servers that are running in offline mode")
                        .addOption(OptionType.BOOLEAN, "whitelist", "Search for servers that are whitelisted")
                        .addOption(OptionType.INTEGER, "port", "Search for servers running on a specific port")
                        .addOption(OptionType.STRING, "asn", "Search for servers running from a specific ASN")
                        .addOption(OptionType.INTEGER, "seenbefore", "Search for servers scanned before a specific unix timestamp")
                        .addOption(OptionType.INTEGER, "seenafter", "Search for servers scanned after a specific unix timestamp")
                        .addOption(OptionType.INTEGER, "timesseen", "Search for servers that have been scanned a specific time")
                        .addOption(OptionType.BOOLEAN, "icon", "Search for servers that have an icon")
                        .addOption(OptionType.STRING, "mods", "Search for forge servers running a specific ModID")
                        .addOption(OptionType.BOOLEAN, "full", "Search for servers with a full player count")
                        .addOption(OptionType.BOOLEAN, "empty", "Search for servers that have no players online")
                        .addOption(OptionType.INTEGER, "protocol", "Search for servers using a specific protocol")
                        .addOption(OptionType.STRING, "hostname", "Reverse DNS name of the server")
                        .addOption(OptionType.STRING, "country", "Search for servers running in a specific country", false, true)
                        .addOptions(new OptionData(OptionType.STRING, "software", "Searches for servers running a specific server software", false)
                                .addChoice("Java", "JAVA")
                                .addChoice("Neoforge", "NEOFORGE")
                                .addChoice("Forge", "LEXFORGE")
                                .addChoice("Paper", "PAPER")
                                .addChoice("Spigot", "SPIGOT")
                                .addChoice("Bukkit", "BUKKIT")
                                .addChoice("Purpur", "PURPUR")
                                .addChoice("Folia", "FOLIA")
                                .addChoice("Pufferfish", "PUFFERFISH")
                                .addChoice("Velocity", "VELOCITY")
                                .addChoice("Waterfall", "WATERFALL")
                                .addChoice("BungeeCord", "BUNGEECORD")
                                .addChoice("Leaves", "LEAVES")
                                .addChoice("Legacy", "LEGACY")),

                Commands.slash("blacklist", "Blacklist a user from using the bot")
                        .addOption(OptionType.USER, "user", "Which user add/remove from the blacklist", true)
                        .addOptions(new OptionData(OptionType.STRING, "operation", "Whether to add or remove a user from the blacklist", true)
                                .addChoice("Add", "Add")
                                .addChoice("Remove", "Remove")),

                Commands.slash("playerinfo", "Shows information about a player")
                                .addOption(OptionType.STRING, "player", "The player to show", true),

                Commands.slash("track", "Get notified through a webhook of player activity")
                        .addOption(OptionType.STRING, "player", "The player you want to receive activity notifications for", true)
                        .addOption(OptionType.STRING, "webhook", "Webhook URL you want notifications sent to", true),

                Commands.slash("playerhistory", "Search for playerhistory of a specific player")
                        .addOption(OptionType.STRING, "player", "The player you want to search for", false)
                        .addOption(OptionType.STRING, "address", "The server you want to search history for", false),

                Commands.slash("takedown", "Add an ip address to the exclude list, optionally remove it from the database")
                        .addOption(OptionType.STRING, "address", "IP address of the server you want to takedown", true)
                        .addOption(OptionType.BOOLEAN, "remove-entries", "Whether or not to delete existing entries of your server from the database"),

                Commands.slash("random", "Finds a random server"),
                Commands.slash("info", "Displays information about the bot, source code, author and credits"),
                Commands.slash("stats", "Shows stats about the bot"),
                Commands.slash("ping", "Ping a specific server.")
                        .addOption(OptionType.STRING, "address", "IP address of the server", true)
                        .addOption(OptionType.INTEGER, "port", "Port of the server")
        ).queue();
    }
}
