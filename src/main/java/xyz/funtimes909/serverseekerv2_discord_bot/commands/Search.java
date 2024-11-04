package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.util.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Search {
    private static final Connection conn = DatabaseConnectionPool.getConnection();
    private static SlashCommandInteractionEvent event;
    private static ResultSet resultSet;
    public static HashMap<Integer, Server> searchResults = new HashMap<>();
    public static int rowCount;

    public static void search(SlashCommandInteractionEvent interactionEvent) {
        event = interactionEvent;
        if (BlacklistCheck.check(event.getUser().getId())) {
            event.reply("Sorry! You're not authorized to use this command!").queue();
        }
        event.deferReply().queue();

        runQuery("SELECT * FROM servers WHERE port = 25565");
    }

    private static void runQuery(String query) {
        try {
            PreparedStatement statement = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            resultSet = statement.executeQuery();

            // Count rows
            resultSet.last();
            rowCount = resultSet.getRow();

            if (rowCount == 0) {
                event.getHook().sendMessage("No results!").queue();
            }

            // Set position to the first result
            resultSet.beforeFirst();
            scrollResults(0, true);
        } catch (SQLException e) {
            Main.logger.error("Error while executing query: {}", query, e);
        }
    }

    public static void scrollResults(int direction, boolean firstRun) {
        try {
            int rowCount = 0;
            searchResults.clear();
            resultSet.relative(direction);
            while (rowCount < 5 && resultSet.next()) {
                String address = resultSet.getString("address");
                String country = resultSet.getString("country");
                String version = resultSet.getString("version");
                long timestamp = resultSet.getLong("lastseen");
                short port = resultSet.getShort("port");

                searchResults.put(rowCount + 1, new Server(address, country, version, timestamp, port));
                rowCount++;
            }

            MessageEmbed embed = SearchCommandBuilder.parse(searchResults);
            List<ItemComponent> buttons = new ArrayList<>();

            // Add buttons for each returned server
            for (Map.Entry<Integer, Server> server : searchResults.entrySet()) {
                buttons.add(Button.success("SearchButton" + server.getKey(), String.valueOf(server.getKey())));
            }

            if (firstRun) {
                if (searchResults.keySet().size() < 5) {
                    event.getHook().sendMessageEmbeds(embed).addActionRow(buttons).queue();
                } else {
                    event.getHook().sendMessageEmbeds(embed).addActionRow(buttons).addActionRow(Button.primary("PagePrevious", Emoji.fromFormatted("U+2B05")), Button.primary("PageNext", Emoji.fromFormatted("U+27A1"))).queue();
                }
            } else {
                if (searchResults.keySet().size() < 5) {
                    event.getHook().editOriginalEmbeds(embed).queue();
                } else {
                    event.getHook().editOriginalEmbeds(embed).queue();
                }
            }

        } catch (SQLException e) {
            Main.logger.error("Error while executing query!", e);
        }
    }

    private static String buildQuery(List<OptionMapping> options) {
        Map<String, String> optionsMap = new HashMap<>();
        StringBuilder query = new StringBuilder("SELECT * FROM servers WHERE ");

        for (OptionMapping option : options) {
            switch (option.getName()) {
                case "description":
                    optionsMap.put("description", "motd = ? AND ");
                    break;
                case "playercount":
                    optionsMap.put("playercount", "onlinePlayers = ? AND ");
                    break;
                case "hostname":
                    optionsMap.put("hostname", "reverseDns = ? AND ");
                    break;
                case "seenbefore":
                    optionsMap.put("seenbefore", "lastseen <= ? AND ");
                    break;
                case "seenafter":
                    optionsMap.put("seenafter", "lastseen >= ? AND ");
                    break;
                case "forge":
                    if (option.getAsBoolean()) optionsMap.put("forge", "fmlnetworkversion IS NOT NULL AND ");
                    else optionsMap.put("forge", "fmlnetworkversion IS NULL AND ");
                    break;
                case "icon":
                    if (option.getAsBoolean()) optionsMap.put("icon", "icon IS NOT NULL AND ");
                    else optionsMap.put("icon", "icon IS NULL AND ");
                    break;
                case "full":
                    if (option.getAsBoolean()) optionsMap.put("full", "onlinePlayers >= maxPlayers AND ");
                    else optionsMap.put("full", "onlinePlayers =< maxPlayers AND ");
                    break;
                default:
                    optionsMap.put(option.getName(), " = ? AND ");
                    break;
            }
        }

        for (String key : optionsMap.keySet()) {
            query.append(key).append(" = ? AND ");
        }

        return query.toString();
    }

    public static void buttonEvent(int row) {
        System.out.println("Called!");
        try (Connection conn = DatabaseConnectionPool.getConnection()) {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM servers WHERE address = ? AND port = ?");

            statement.setString(1, searchResults.get(row).address());
            statement.setInt(2, searchResults.get(row).port());

            System.out.println(searchResults.get(row).address());
            System.out.println(searchResults.get(row).port());

            ResultSet resultSet = statement.executeQuery();

            MessageEmbed embed = ServerEmbedBuilder.build(resultSet);

            if (embed == null) {
                event.getHook().sendMessage("Something went wrong executing that command!").queue();
                return;
            }

            event.getHook().sendMessageEmbeds(embed).queue();
        } catch (SQLException e) {
            Main.logger.error("Error while executing query!", e);
        }

    }
}

