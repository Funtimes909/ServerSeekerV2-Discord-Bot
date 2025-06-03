package xyz.funtimes909.serverseekerv2_discord_bot.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
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
                        // Allows the command to be used everywhere
                        .setContexts(InteractionContextType.ALL)
                        .setIntegrationTypes(IntegrationType.ALL)
                        .addOption(OptionType.STRING, "address", "Search for servers running on a specific server")
                        .addOption(OptionType.INTEGER, "port", "Search for servers running on a specific port")
                        .addOption(OptionType.STRING, "version", "Search for servers using a specific minecraft version")
                        .addOption(OptionType.INTEGER, "protocol", "Search for servers using a specific protocol")
                        .addOption(OptionType.STRING, "description", "Search for servers with a specific description")
                        .addOption(OptionType.INTEGER, "online_players", "Search for servers with a specific amount of players online")
                        .addOption(OptionType.INTEGER, "max_online_players", "Search for servers with a maximum amount of players online")
                        .addOption(OptionType.INTEGER, "min_online_players", "Search for servers with a minimum amount of players online")
                        .addOption(OptionType.INTEGER, "max_players", "Search for servers that have a specific player cap")
                        .addOption(OptionType.BOOLEAN, "prevents_chat_reports", "Search for servers that prevent chat reports")
                        .addOption(OptionType.BOOLEAN, "enforces_secure_chat", "Search for servers that enforce secure chat")
                        .addOption(OptionType.STRING, "country", "Search for servers running in a specific country", false, true)
                        .addOption(OptionType.STRING, "asn", "Search for servers running from a specific ASN")
                        .addOption(OptionType.INTEGER, "first_seen_before", "Search for servers first scanned before a specific unix timestamp")
                        .addOption(OptionType.INTEGER, "first_seen_after", "Search for servers first scanned after a specific unix timestamp")
                        .addOption(OptionType.INTEGER, "last_seen_before", "Search for servers last scanned after a specific unix timestamp")
                        .addOption(OptionType.INTEGER, "last_seen_after", "Search for servers last scanned after a specific unix timestamp")
                        .addOption(OptionType.BOOLEAN, "icon", "Search for servers that have an icon")
                        .addOption(OptionType.BOOLEAN, "full", "Search for servers with a full player count")
                        .addOption(OptionType.BOOLEAN, "empty", "Search for servers that have no players online")
                        .addOption(OptionType.STRING, "hostname", "Hostname of the server")
                        .addOptions(new OptionData(OptionType.STRING, "software", "Searches for servers running a specific server software", false)
                                .addChoice("Java", "Java")
                                .addChoice("Neoforge", "Neoforge")
                                .addChoice("Forge", "Lexforge")
                                .addChoice("Paper", "Paper")
                                .addChoice("Spigot", "Spigot")
                                .addChoice("Bukkit", "Bukkit")
                                .addChoice("Purpur", "Purpur")
                                .addChoice("Folia", "Folia")
                                .addChoice("Pufferfish", "Pufferfish")
                                .addChoice("Velocity", "Velocity")
                                .addChoice("Leaves", "Leaves")
                                .addChoice("Waterfall", "Waterfall")
                                .addChoice("BungeeCord", "Bungeecord")),

                Commands.slash("blacklist", "Blacklist a user from using the bot")
                        .setContexts(InteractionContextType.ALL)
                        .setIntegrationTypes(IntegrationType.ALL)
                        .addOption(OptionType.USER, "user", "Which user add/remove from the blacklist", true)
                        .addOptions(new OptionData(OptionType.STRING, "operation", "Whether to add or remove a user from the blacklist", true)
                                .addChoice("Add", "Add")
                                .addChoice("Remove", "Remove")),

                Commands.slash("playerhistory", "Search for player history of a specific player")
                        .setContexts(InteractionContextType.ALL)
                        .setIntegrationTypes(IntegrationType.ALL)
                        .addOption(OptionType.STRING, "player", "The player you want to search for", false)
                        .addOption(OptionType.STRING, "address", "The server you want to search history for", false),

                Commands.slash("ping", "Ping a specific server.")
                        .setContexts(InteractionContextType.ALL)
                        .setIntegrationTypes(IntegrationType.ALL)
                        .addOption(OptionType.STRING, "address", "Address of the server", true)
                        .addOption(OptionType.INTEGER, "port", "Port of the server"),

                Commands.slash("takedown", "Add an ip address to the exclude list, optionally remove it from the database")
                        .setContexts(InteractionContextType.ALL)
                        .setIntegrationTypes(IntegrationType.ALL)
                        .addOption(OptionType.STRING, "address", "IP address of the server you want to takedown", true),

                Commands.slash("random", "Finds a random server")
                        .setContexts(InteractionContextType.ALL)
                        .setIntegrationTypes(IntegrationType.ALL),

                Commands.slash("info", "Displays information about the bot, source code, author and credits")
                        .setContexts(InteractionContextType.ALL)
                        .setIntegrationTypes(IntegrationType.ALL),

                Commands.slash("stats", "Shows stats about the bot")
                        .setContexts(InteractionContextType.ALL)
                        .setIntegrationTypes(IntegrationType.ALL)
        ).queue();
    }
}
