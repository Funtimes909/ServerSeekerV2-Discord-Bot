package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.builders.SearchEmbedBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.builders.ServerEmbedBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.records.Mod;
import xyz.funtimes909.serverseekerv2_discord_bot.records.Player;
import xyz.funtimes909.serverseekerv2_discord_bot.records.Server;
import xyz.funtimes909.serverseekerv2_discord_bot.records.ServerEmbed;
import xyz.funtimes909.serverseekerv2_discord_bot.util.Database;

import java.sql.*;
import java.util.*;

public class Search {
    private static final Map<String, OptionMapping> parameters = new HashMap<>();
    private static List<OptionMapping> options = new ArrayList<>();
    private static final Set<String> mods = new HashSet<>();
    private static final HashMap<Integer, ServerEmbed> results = new HashMap<>();
    private static StringBuilder query;
    private static SlashCommandInteractionEvent event;
    public static int pointer = 1;
    public static int offset;
    public static int totalRows;

    public static void search(SlashCommandInteractionEvent interactionEvent) {
        event = interactionEvent;
        options = interactionEvent.getOptions();

        if (interactionEvent.getOptions().isEmpty()) {
            interactionEvent.getHook().sendMessage("You must provide some search queries!").queue();
        }

        query = buildQuery(options, true);
        runQuery(true);

        query = buildQuery(options, false);
        runQuery(false);

    }

    private static StringBuilder buildQuery(List<OptionMapping> options, boolean countQuery) {
        StringBuilder query = new StringBuilder("SELECT servers.address, servers.country, servers.version, servers.lastseen, servers.port FROM servers");
        if (countQuery) query.replace(7, 88, "COUNT(*)");

        // Player and ModId searching
        if (event.getOption("players") != null) query.append(" JOIN playerhistory ON servers.address = playerhistory.address AND servers.port = playerhistory.port");
        if (event.getOption("mods") != null) query.append(" JOIN mods ON servers.address = mods.address AND servers.port = mods.port");
        query.append(" WHERE ");

        for (OptionMapping option : options) {
            switch (option.getName()) {
                case "description" -> parameters.put("motd", option);
                case "playercount" -> parameters.put("onlineplayers", option);
                case "hostname" -> parameters.put("reversedns", option);
                case "seenbefore", "seenafter" -> parameters.put("lastseen", option);
                case "full" -> query.append(option.getAsBoolean() ? "onlinePlayers >= maxPlayers AND " : "onlinePlayers < maxPlayers AND ");
                case "empty" -> query.append(option.getAsBoolean() ? "onlinePlayers = 0 AND " : "onlinePlayers != 0 AND ");
                case "forge" -> query.append(option.getAsBoolean() ? "fmlnetworkversion IS NOT NULL AND " : "fmlnetworkversion IS NULL AND ");
                case "icon" -> query.append(option.getAsBoolean() ? "icon IS NOT NULL AND " : "icon IS NULL AND ");
                case "mods" -> {
                    if (option.getAsString().contains(",")) { mods.addAll(Arrays.asList(option.getAsString().split(", "))); }
                }
                case "limit" -> {}
                default -> parameters.put(option.getName(), option);
            }
        }

        for (Map.Entry<String, OptionMapping> entry : parameters.entrySet()) {
            switch (entry.getValue().getName()) {
                case "seenbefore" -> query.append("lastseen <= ? AND ");
                case "seenafter" -> query.append("lastseen >= ? AND ");
                case "reversedns" -> query.append("hostname = ? AND ");
                case "forgeversion" -> query.append("fmlnetworkversion = ? AND ");
                case "description" -> query.append("motd ILIKE '%' || ? || '%' AND ");
                case "player" -> query.append("playername ILIKE '%' || ? || '%' AND ");
                case "port" -> {
                    if (event.getOption("players") != null || event.getOption("mods") != null) { query.append("servers.port = ? AND "); }
                }
                default -> query.append(entry.getKey()).append(" = ? AND ");
            }
        }

        // Add modids to the end of the query
        if (!mods.isEmpty()) mods.forEach((mod) -> query.append("modid = ? OR "));
        query.replace(query.length() - 4, query.length(), "");
        if (!countQuery) query.append(" ORDER BY lastseen DESC OFFSET ").append(offset);
        return query;
    }

    private static void runQuery(boolean countQuery) {
        try (Connection conn = Database.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(query.toString(), ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            int index = 1;
            for (OptionMapping option : parameters.values()) {
                switch (option.getType()) {
                    case STRING -> statement.setString(index, option.getAsString());
                    case INTEGER -> statement.setInt(index, option.getAsInt());
                    case BOOLEAN -> statement.setBoolean(index, option.getAsBoolean());
                }
                index++;
            }

            if (!mods.isEmpty()) {
                for (String mod : mods) {
                    statement.setString(index, mod);
                    index++;
                }
            }
            System.out.println(statement);

            long startTime = System.currentTimeMillis() / 1000L;
            ResultSet results = statement.executeQuery();
            Main.logger.debug("Search command took {}ms to execute!", (System.currentTimeMillis() / 1000L - startTime));

            if (!countQuery) {
                results.last();
                int rowCount = results.getRow();
                if (rowCount == 0) {
                    event.getHook().sendMessage("No results!").queue();
                    return;
                }

                Search.results.clear();
                int count = 1;
                results.beforeFirst();
                while (results.next()) {
                    Search.results.put(count, new ServerEmbed(results.getString("address"), results.getString("country"), results.getString("version"), results.getLong("lastseen"), results.getShort("port")));
                    count++;
                }

                scrollResults(true, true);
            } else {
                while (results.next()) {
                    totalRows = results.getInt(1);
                }
            }
        } catch (SQLException e) {
            Main.logger.error("Error while running search query!", e);
            event.getHook().sendMessage("Error while running search query!").queue();
        }
    }

    public static void scrollResults(boolean firstRun, boolean forward) {
        HashMap<Integer, ServerEmbed> page = new HashMap<>();
        List<ItemComponent> buttons = new ArrayList<>();

        if (forward && firstRun) {
            int index = 1;
            for (int i = 0; 5 > i; i++) {
                System.out.println("At index: " + (i + 1) + " Page value: " + results.get(i + 1));
                page.put(index, results.get(i + 1));
                pointer++;
                index++;
            }
        } else {
            if (forward) {
                int index = 1;
                int count = pointer;
                for (int i = pointer; count + 5 > i; i++) {
                    System.out.println("At index: " + i + " Page value: " + results.get(i));
                    page.put(index, results.get(i));
                    pointer++;
                    index++;
                }
            } else {
                int index = 1;
                int count = pointer;
                for (int i = pointer; count - 5 < i; i--) {
                    System.out.println("At index: " + i + " Page value: " + results.get(i));
                    page.put(index, results.get(i));
                    pointer--;
                    index++;
                }
            }
        }

        for (int entry : page.keySet()) {
            buttons.add(Button.success("SearchButton" + entry, String.valueOf(entry)));
        }

        MessageEmbed embed = SearchEmbedBuilder.parse(page);
        if (firstRun) {
            if (page.size() < 5) {
                event.getHook().sendMessageEmbeds(embed).addActionRow(buttons).queue();
            } else {
                event.getHook().sendMessageEmbeds(embed).addActionRow(buttons).addActionRow(Button.primary("PagePrevious", Emoji.fromFormatted("U+2B05")), Button.primary("PageNext", Emoji.fromFormatted("U+27A1"))).queue();
            }
        } else {
            event.getHook().editOriginalEmbeds(embed).queue();
        }
    }


    public static void serverSelectedButtonEvent(String address, short port, ButtonInteractionEvent event) {
        try (Connection conn = Database.getConnection()) {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM servers LEFT JOIN playerhistory ON servers.address = playerhistory.address AND servers.port = playerhistory.port LEFT JOIN mods ON servers.address = mods.address AND servers.port = mods.port WHERE servers.address = ? AND servers.port = ?");
            statement.setString(1, address);
            statement.setShort(2, port);

            ResultSet results = statement.executeQuery();
            Server.Builder server = new Server.Builder();
            List<Player> players = new ArrayList<>();
            List<Mod> mods = new ArrayList<>();

            while (results.next()) {
                server.setAddress(results.getString("address"));
                server.setPort(results.getShort("port"));
                server.setMotd(results.getString("motd"));
                server.setVersion(results.getString("version"));
                server.setFirstSeen(results.getLong("firstseen"));
                server.setLastSeen(results.getLong("lastseen"));
                server.setProtocol(results.getInt("protocol"));
                server.setCountry(results.getString("country"));
                server.setAsn(results.getString("asn"));
                server.setReverseDns(results.getString("reversedns"));
                server.setOrganization(results.getString("organization"));
                server.setWhitelist((Boolean) results.getObject("whitelist"));
                server.setEnforceSecure((Boolean) results.getObject("enforceSecure"));
                server.setCracked((Boolean) results.getObject("cracked"));
                server.setPreventsReports((Boolean) results.getObject("preventsReports"));
                server.setMaxPlayers(results.getInt("maxPlayers"));
                server.setTimesSeen(results.getInt("timesSeen"));
                server.setFmlNetworkVersion(results.getInt("fmlnetworkversion"));

                if (results.getString("playername") != null) players.add(new Player(results.getString("playername"), results.getString("playeruuid"), results.getLong("lastseen")));
                if (results.getString("modid") != null) mods.add(new Mod(results.getString("modid"), results.getString("modmarker")));
            }

            server.setPlayers(players);
            server.setMods(mods);
            statement.close();
            results.close();

            ServerEmbedBuilder embedBuilder = new ServerEmbedBuilder(server.build());
            MessageEmbed embed = embedBuilder.build(false);

            if (embed == null) {
                event.getInteraction().getHook().sendMessage("Something went wrong executing that command!").queue();
                return;
            }

            event.getInteraction().getHook().sendMessageEmbeds(embed).queue();
        } catch (SQLException e) {
            Main.logger.error("Error while executing query!", e);
        }
    }
}