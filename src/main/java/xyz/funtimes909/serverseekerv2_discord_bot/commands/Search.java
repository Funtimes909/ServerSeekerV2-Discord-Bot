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
import xyz.funtimes909.serverseekerv2_discord_bot.util.PermissionsCheck;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Search {
    public static HashMap<Integer, ServerEmbed> searchResults = new HashMap<>();
    public static int rowCount;
    public static int page = 1;
    private static SlashCommandInteractionEvent event;
    private static ResultSet resultSet;

    public static void search(SlashCommandInteractionEvent interactionEvent) {
        event = interactionEvent;

        if (PermissionsCheck.blacklistCheck(interactionEvent.getInteraction().getUser().getId())) {
            interactionEvent.getHook().sendMessage("Sorry! You are not authorized to run this command!").setEphemeral(true).queue();
            return;
        }

        if (event.getOptions().isEmpty()) {
            interactionEvent.getHook().sendMessage("You must provide some search queries!").queue();
            return;
        }

        buildQuery(event.getOptions());
    }

    private static void buildQuery(List<OptionMapping> options) {
        Map<String, OptionMapping> parameters = new HashMap<>();
        StringBuilder query = new StringBuilder("SELECT servers.address, servers.country, servers.version, servers.lastseen, servers.port FROM servers");
        Set<String> mods = new HashSet<>();

        // Player and ModId searching
        if (event.getOption("player") != null) query.append(" JOIN playerhistory ON servers.address = playerhistory.address AND servers.port = playerhistory.port");
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
                default -> query.append(entry.getKey()).append(" = ? AND ");
            }
        }

        try {
            // Add modids to the end of the query
            if (!mods.isEmpty()) mods.forEach((mod) -> query.append("modid = ? OR "));

            query.replace(query.length() - 4, query.length(), "");
            query.append(" ORDER BY lastseen DESC");
            if (event.getOption("limit") != null) query.append(" LIMIT ?");
            Connection conn = Database.getConnection();
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

            if (event.getOption("limit") != null) statement.setInt(index, event.getOption("limit").getAsInt());

            // Time query duration
            long startTime = System.currentTimeMillis() / 1000L;
            resultSet = statement.executeQuery();
            Main.logger.debug("Search command took {}ms to execute!", (System.currentTimeMillis() / 1000L - startTime));

            resultSet.last();
            rowCount = resultSet.getRow();
            if (rowCount == 0) event.getHook().sendMessage("No results!").queue();
            resultSet.beforeFirst();
            scrollResults(0, true);
        } catch (SQLException e) {
            Main.logger.error("Error while forming search query!", e);
        }
    }

    public static void scrollResults(int direction, boolean firstRun) {
        try {
            int rowCount = 0;
            searchResults.clear();
            resultSet.relative(direction);
            while (rowCount < 5 && resultSet.next()) {
                searchResults.put(rowCount + 1, new ServerEmbed(resultSet.getString("address"), resultSet.getString("country"), resultSet.getString("version"), resultSet.getLong("lastseen"), resultSet.getShort("port")));
                rowCount++;
            }

            MessageEmbed embed = SearchEmbedBuilder.parse(searchResults);
            List<ItemComponent> buttons = new ArrayList<>();

            // Add buttons for each returned server
            for (Map.Entry<Integer, ServerEmbed> entry : searchResults.entrySet()) {
                buttons.add(Button.success("SearchButton" + entry.getKey(), String.valueOf(entry.getKey())));
            }

            // Send a new message if it's the first interaction, edit the original if it's a new search page
            if (firstRun) {
                if (searchResults.keySet().size() < 5) {
                    event.getHook().sendMessageEmbeds(embed).addActionRow(buttons).queue();
                } else {
                    event.getHook().sendMessageEmbeds(embed).addActionRow(buttons).addActionRow(Button.primary("PagePrevious", Emoji.fromFormatted("U+2B05")), Button.primary("PageNext", Emoji.fromFormatted("U+27A1"))).queue();
                }
            } else {
                event.getHook().editOriginalEmbeds(embed).queue();
            }
        } catch (SQLException e) {
            Main.logger.error("Error while scrolling results!", e);
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