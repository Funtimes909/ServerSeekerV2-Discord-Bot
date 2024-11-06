package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class Register {
    public static void registerCommands(JDA client) {

        List<OptionData> searchOptions = new ArrayList<>();
        searchOptions.add(new OptionData(OptionType.STRING, "player", "Search for servers that a player is currently playing on"));
        searchOptions.add(new OptionData(OptionType.STRING, "version", "Search for servers using a specific minecraft version"));
        searchOptions.add(new OptionData(OptionType.STRING, "description", "Search for servers with a specific description"));
        searchOptions.add(new OptionData(OptionType.INTEGER, "playercount", "Search for servers with a specific amount of players online"));
        searchOptions.add(new OptionData(OptionType.INTEGER, "maxplayers", "Search for servers that have a specific player cap"));
        searchOptions.add(new OptionData(OptionType.BOOLEAN, "preventsreports", "Search for servers that prevent chat reports"));
        searchOptions.add(new OptionData(OptionType.BOOLEAN, "enforcesecure", "Search for servers that enforce secure chat"));
        searchOptions.add(new OptionData(OptionType.BOOLEAN, "cracked", "Search for servers that are running in offline mode"));
        searchOptions.add(new OptionData(OptionType.STRING, "country", "Search for servers running in a specific country"));
        searchOptions.add(new OptionData(OptionType.STRING, "iprange", "Search for servers running in this specific IP address range"));
        searchOptions.add(new OptionData(OptionType.INTEGER, "port", "Search for servers running on a specific port"));
        searchOptions.add(new OptionData(OptionType.STRING, "asn", "Search for servers running from a specific ASN"));
        searchOptions.add(new OptionData(OptionType.INTEGER, "seenbefore", "Search for servers scanned before a specific unix timestamp"));
        searchOptions.add(new OptionData(OptionType.INTEGER, "seenafter", "Search for servers scanned after a specific unix timestamp"));
        searchOptions.add(new OptionData(OptionType.INTEGER, "timesseen", "Search for servers that have been scanned a specific time"));
        searchOptions.add(new OptionData(OptionType.BOOLEAN, "icon", "Search for servers that have an icon"));
        searchOptions.add(new OptionData(OptionType.STRING, "mods", "Search for forge servers running a specific ModID"));
        searchOptions.add(new OptionData(OptionType.BOOLEAN, "full", "Search for servers with a full player count"));
        searchOptions.add(new OptionData(OptionType.INTEGER, "protocol", "Search for servers using a specific protocol"));
        searchOptions.add(new OptionData(OptionType.BOOLEAN, "forge", "Search for servers running Forge"));
        searchOptions.add(new OptionData(OptionType.INTEGER, "forgeversion", "Search for servers running a specific FML Network version"));
        searchOptions.add(new OptionData(OptionType.STRING, "hostname", "Reverse DNS name of the server"));
        searchOptions.add(new OptionData(OptionType.BOOLEAN, "compact", "Show results in a compact or bold style (default: compact)"));

        List<OptionData> blacklistOptions = new ArrayList<>();
        blacklistOptions.add(new OptionData(OptionType.STRING, "blacklist", "Blacklist a user").addChoice("add", "Add").addChoice("remove", "Remove"));
        blacklistOptions.add(new OptionData(OptionType.USER, "user", "Which user add/remove from the blacklist"));

        client.updateCommands().addCommands(
                Commands.slash("search", "Searches for servers with advanced filters")
                                .addOptions(searchOptions),

                Commands.slash("random", "Finds a random server"),
                Commands.slash("blacklist", "Blacklist a user from using the bot")
                        .addOptions(blacklistOptions),

                Commands.slash("notify", "Get notified through a webhook of player activity")
                        .addOption(OptionType.STRING, "player", "The player you want to recieve activity notifications for")
                        .addOption(OptionType.STRING, "url", "Webhook URL you want notifications sent to"),

                Commands.slash("playerhistory", "Search for playerhistory of a specific player")
                                .addOption(OptionType.STRING, "player", "The player you want to search for"),

                Commands.slash("takedown", "Request for your server to be excluded from all future scans")
                        .addOption(OptionType.STRING, "address", "IP address of the server you want to takedown", true)
                        .addOption(OptionType.BOOLEAN, "remove-entries", "Whether or not to delete existing entries of your server from the database"),

                Commands.slash("info", "Displays information about the bot, source code, author and credits"),
                Commands.slash("stats", "Shows stats about the bot"),
                Commands.slash("ping", "Ping a specific server.")
                        .addOption(OptionType.STRING, "address", "IP address of the server", true)
                        .addOption(OptionType.INTEGER, "port", "Port of the server", true)
        ).queue();
    }
}
