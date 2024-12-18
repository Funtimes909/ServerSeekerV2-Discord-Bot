package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import xyz.funtimes909.serverseekerv2_core.records.Server;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.builders.SearchEmbedBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.builders.ServerEmbedBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.records.ServerEmbed;
import xyz.funtimes909.serverseekerv2_discord_bot.util.ConnectionPool;
import xyz.funtimes909.serverseekerv2_discord_bot.util.GenericErrorEmbed;
import xyz.funtimes909.serverseekerv2_discord_bot.util.PingUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Search {
    private final Map<String, OptionMapping> parameters = new HashMap<>();
    private final HashMap<Integer, ServerEmbed> results = new HashMap<>();
    private final Set<String> mods = new HashSet<>();
    private final SlashCommandInteractionEvent interaction;
    private StringBuilder query;
    public int totalRows;
    public int pointer = 1;
    public int offset = 0;

    public Search(SlashCommandInteractionEvent event) {
        this.interaction = event;
    }

    public void search() {
        if (interaction.getOptions().isEmpty()) {
            interaction.getHook().sendMessage("You must provide some search queries!").queue();
            return;
        }

        buildQuery(interaction.getOptions());
        runQuery(true);
        scrollResults(true, true);
    }

    private void buildQuery(List<OptionMapping> options) {
        query = new StringBuilder("SELECT servers.address, servers.port, servers.country, servers.version, servers.lastseen FROM servers ");

        // If either player or mods are requested, join the tables
        if (interaction.getOption("player") != null) query.append("JOIN playerhistory ON servers.address = playerhistory.address AND servers.port = playerhistory.port ");
        if (interaction.getOption("mods") != null) query.append("JOIN mods ON servers.address = mods.address AND servers.port = mods.port ");
        query.append("WHERE ");

        // For simple boolean queries, add them directly to the query, for everything else add it to the parameters Map
        for (OptionMapping option : options) {
            switch (option.getName()) {
                case "description" -> parameters.put("motd", option);
                case "playercount" -> parameters.put("onlineplayers", option);
                case "hostname" -> parameters.put("reversedns", option);
                case "seenbefore", "seenafter" -> parameters.put("lastseen", option);
                case "full" -> query.append(option.getAsBoolean() ? "onlinePlayers >= maxPlayers AND " : "onlinePlayers < maxPlayers AND ");
                case "empty" -> query.append(option.getAsBoolean() ? "onlinePlayers = 0 AND " : "onlinePlayers != 0 AND ");
                case "forge" -> query.append(option.getAsBoolean() ? "fmlnetworkversion IS NOT NULL AND " : "fmlnetworkversion IS NULL OR fmlnetworkversion = 0 AND ");
                case "icon" -> query.append(option.getAsBoolean() ? "icon IS NOT NULL AND " : "icon IS NULL AND ");
                case "mods" -> { if (option.getAsString().contains(",")) { mods.addAll(Arrays.asList(option.getAsString().split(", "))); } }
                default -> parameters.put(option.getName(), option);
            }
        }

        // Append corresponding query parameter to end of query
        for (Map.Entry<String, OptionMapping> entry : parameters.entrySet()) {
            switch (entry.getValue().getName()) {
                case "seenbefore" -> query.append("lastseen < ? AND ");
                case "seenafter" -> query.append("lastseen > ? AND ");
                case "reversedns" -> query.append("hostname = ? AND ");
                case "forgeversion" -> query.append("FmlNetworkVersion = ? AND ");
                case "description" -> query.append("motd ILIKE '%' || ? || '%' AND ");
                case "player" -> query.append("playername ILIKE '%' || ? || '%' AND ");
                case "port" -> query.append("servers.port = ? AND ");
                default -> query.append(entry.getKey()).append(" = ? AND ");
            }
        }

        // Add modids to the end of the query
        if (!mods.isEmpty()) mods.forEach((mod) -> query.append("modid = ? OR "));
        query.replace(query.length() - 4, query.length(), "");
        query.append(" ORDER BY lastseen DESC");
    }

    public void runQuery(boolean firstRun) {
        try (Connection conn = ConnectionPool.getConnection()) {
            if (conn == null) return;

            // Append offset
            if (firstRun) query.append(" OFFSET 0");
            else query.replace(query.lastIndexOf(" OFFSET"), query.length(), " OFFSET " + offset);
            PreparedStatement statement = conn.prepareStatement(query.toString(), ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            System.out.println(statement);

            // Add every query value based on its type to the statement
            int index = 1;
            for (OptionMapping option : parameters.values()) {
                switch (option.getType()) {
                    case STRING -> statement.setString(index, option.getAsString());
                    case INTEGER -> statement.setInt(index, option.getAsInt());
                    case BOOLEAN -> statement.setBoolean(index, option.getAsBoolean());
                }
                index++;
            }

            // Add modID's to the statement last as they will always be at the end
            if (!mods.isEmpty()) {
                for (String mod : mods) {
                    statement.setString(index, mod);
                    index++;
                }
            }

            long startTime = System.currentTimeMillis() / 1000L;
            ResultSet results = statement.executeQuery();
            Main.logger.debug("Search command took {}ms to execute!", (System.currentTimeMillis() / 1000L - startTime));

            // Check the row count for the first run
            if (firstRun) {
                results.last();
                totalRows = results.getRow();
                if (totalRows == 0) {
                    interaction.getHook().sendMessage("No results!").queue();
                    return;
                }
            }

            // Add the first 50 results to a Map, where the key is the results number between 1 - 50
            int count = 1;
            results.beforeFirst();
            while (results.next() && count < 50) {
                this.results.put(count, new ServerEmbed(results.getString("address"), results.getString("country"), results.getString("version"), results.getLong("lastseen"), results.getShort("port")));
                count++;
            }
        } catch (SQLException e) {
            Main.logger.error("Error while running search query!", e);
            GenericErrorEmbed.errorEmbed(interaction.getMessageChannel(), e.getMessage());
        }
    }

    public void scrollResults(boolean firstRun, boolean forward) {
        List<ServerEmbed> page = new ArrayList<>();
        List<Button> buttons = new ArrayList<>();
        List<LayoutComponent> pageButtons = new ArrayList<>();

        // Iterate through results by 5 and add them to the page results, set pointer back 10 results for scrolling backwards
        if (!forward) pointer -= 10;
        int count = pointer;
        for (int i = count; (count + 5) > i; i++) {
            if (results.get(i) == null) continue;
            page.add(results.get(i));
            pointer++;
        }

        for (int index = 1; index < page.size() + 1; index++) {
            buttons.add(Button.of(ButtonStyle.SUCCESS, "SearchButton" + index, String.valueOf(index)));
        }

        if (page.isEmpty()) return;

        if (totalRows <= 5) {
            // Less than 6 total results, only one page, don't scroll
            pageButtons.add(ActionRow.of(buttons));
        } else if (pointer >= (totalRows - 6)) {
            // Last page, don't let the user scroll forwards anymore
            pageButtons.add(ActionRow.of(buttons));
            pageButtons.add(ActionRow.of(Button.primary("PagePrevious", Emoji.fromFormatted("U+2B05")), Button.primary("PageNext", Emoji.fromFormatted("U+27A1")).asDisabled()));
        } else if (pointer <= 6) {
            // First page, don't let the user scroll back
            pageButtons.add(ActionRow.of(buttons));
            pageButtons.add(ActionRow.of(Button.primary("PagePrevious", Emoji.fromFormatted("U+2B05")).asDisabled(), Button.primary("PageNext", Emoji.fromFormatted("U+27A1"))));
        } else {
            // Normal page, let the user scroll both directions
            pageButtons.add(ActionRow.of(buttons));
            pageButtons.add(ActionRow.of(Button.primary("PagePrevious", Emoji.fromFormatted("U+2B05")), Button.primary("PageNext", Emoji.fromFormatted("U+27A1"))));
        }

        // Show accurate page count if pointer is over 5, otherwise display only one page
        MessageEmbed embed = SearchEmbedBuilder.parse(page, totalRows, totalRows <= 5 ? 1 : (pointer / 5));

        // Send embed, update if already sent
        if (firstRun) {
            interaction.getHook().sendMessageEmbeds(embed).setComponents(pageButtons).queue();
        } else {
            interaction.getHook().editOriginalEmbeds(embed).setComponents(pageButtons).queue();
        }
    }

    public void serverSelectedButtonEvent(String address, short port, ButtonInteractionEvent event) {
        try (Connection conn = ConnectionPool.getConnection()) {
            if (conn == null) {
                GenericErrorEmbed.errorEmbed(interaction.getMessageChannel(), "Could not connect to database!");
                return;
            }

            PreparedStatement statement = conn.prepareStatement("SELECT * FROM servers LEFT JOIN playerhistory ON servers.address = playerhistory.address AND servers.port = playerhistory.port LEFT JOIN mods ON servers.address = mods.address AND servers.port = mods.port WHERE servers.address = ? AND servers.port = ?");
            statement.setString(1, address);
            statement.setShort(2, port);

            Server server = PingUtils.buildResultsToObject(statement.executeQuery());

            if (server == null) {
                GenericErrorEmbed.errorEmbed(interaction.getMessageChannel(), "Server could not be built!");
                return;
            }

            ServerEmbedBuilder embedBuilder = new ServerEmbedBuilder(server);
            MessageEmbed embed = embedBuilder.build(event.getMessageChannel(), true).get();
            if (embed == null) {
                event.getHook().sendMessage("Server did not connect!").queue();
                return;
            }

            event.getHook().sendMessageEmbeds(embed).queue();
        } catch (SQLException | ExecutionException | InterruptedException e) {
            Main.logger.error("Error while executing query!", e);
            GenericErrorEmbed.errorEmbed(interaction.getMessageChannel(), e.getMessage());
        }
    }
}