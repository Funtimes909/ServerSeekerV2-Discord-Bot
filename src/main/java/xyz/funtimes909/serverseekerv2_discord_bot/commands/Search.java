package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.Records.Server;
import xyz.funtimes909.serverseekerv2_discord_bot.Records.ServerEmbed;
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
    public static HashMap<Integer, ServerEmbed> searchResults = new HashMap<>();
    public static String rescanAddress;
    public static short rescanPort;
    public static int rowCount;
    public static int page = 1;
    private static final Connection conn = DatabaseConnectionPool.getConnection();
    private static SlashCommandInteractionEvent event;
    private static ResultSet resultSet;

    public static void search(SlashCommandInteractionEvent interactionEvent) {
        event = interactionEvent;
        if (BlacklistCheck.check(event.getUser().getId())) event.reply("Sorry! You're not authorized to use this command!").queue();

        if (event.getOptions().isEmpty()) {
            event.reply("You must provide some search queries!").queue();
            return;
        }

        event.deferReply().queue();
        buildQuery(event.getOptions());
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

                searchResults.put(rowCount + 1, new ServerEmbed(address, country, version, timestamp, port));
                rowCount++;
            }

            MessageEmbed embed = SearchEmbedBuilder.parse(searchResults);
            List<ItemComponent> buttons = new ArrayList<>();

            // Add buttons for each returned server
            for (Map.Entry<Integer, ServerEmbed> server : searchResults.entrySet()) {
                buttons.add(Button.success("SearchButton" + server.getKey(), String.valueOf(server.getKey())));
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
            Main.logger.error("Error while executing query!", e);
        }
    }

    private static void buildQuery(List<OptionMapping> options) {
        Map<String, OptionMapping> parameters = new HashMap<>();
        StringBuilder query = new StringBuilder("SELECT address, country, version, lastseen, port FROM servers WHERE ");
        for (OptionMapping option : options) {
            switch (option.getName()) {
                case "description":
                    parameters.put("motd", option);
                    break;
                case "playercount":
                    parameters.put("onlineplayers", option);
                    break;
                case "hostname":
                    parameters.put("reversedns", option);
                    break;
                case "seenbefore", "seenafter":
                    parameters.put("lastseen", option);
                    break;
                case "full":
                    if (option.getAsBoolean()) {
                        query.append("onlinePlayers >= maxPlayers AND ");
                    } else {
                        query.append("onlinePlayers < maxPlayers AND ");
                    }
                    break;
                case "forge":
                    if (option.getAsBoolean()) {
                        query.append("fmlnetworkversion IS NOT NULL AND ");
                    } else {
                        query.append("fmlnetworkversion IS NULL AND ");
                    }
                    break;
                case "icon":
                    if (option.getAsBoolean()) {
                        query.append("icon IS NOT NULL AND ");
                    } else {
                        query.append("icon IS NULL AND ");
                    }
                    break;
                default:
                    parameters.put(option.getName(), option);
                    break;
            }
        }

        parameters.forEach((key, value) -> {
            switch (value.getName()) {
                case "seenbefore" -> query.append("lastseen <= ? AND ");
                case "seenafter" -> query.append("lastseen >= ? AND ");
                case "reversedns" -> query.append("hostname = ? AND ");
                case "description" -> query.append("motd ILIKE '%' || ? || '%' AND ");
                default -> query.append(key).append(" = ? AND ");
            }
        });

        try {
            // Create statement and assign values
            query.replace(query.length() - 4, query.length(), "");
            Connection conn = DatabaseConnectionPool.getConnection();
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

            System.out.println(statement);

            // Execute query and count the rows
            long startTime = System.currentTimeMillis() / 1000L;
            resultSet = statement.executeQuery();
            long endTime = System.currentTimeMillis() / 1000L;
            Main.logger.debug("Search command took {}ms to execute!", (endTime - startTime));
            resultSet.last();
            rowCount = resultSet.getRow();
            if (rowCount == 0) event.getHook().sendMessage("No results!").queue();

            // Set position to the first result
            resultSet.beforeFirst();
            scrollResults(0, true);
        } catch (SQLException e) {
            Main.logger.error("Error while executing query!", e);
        }
    }

    public static void serverSelectedButtonEvent(int row) {
        try (Connection conn = DatabaseConnectionPool.getConnection()) {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM servers LEFT JOIN playerhistory ON servers.address = playerhistory.address AND servers.port = playerhistory.port WHERE servers.address = ? AND servers.port = ?");

            String address = searchResults.get(row).address();
            short port = searchResults.get(row).port();

            statement.setString(1, address);
            statement.setInt(2, port);
            rescanAddress = address;
            rescanPort = port;

            ResultSet resultSet = statement.executeQuery();
            ServerEmbedBuilder embedBuilder = new ServerEmbedBuilder(resultSet, false);
            MessageEmbed embed = embedBuilder.build();

            if (embed == null) {
                event.getHook().sendMessage("Something went wrong executing that command!").queue();
                return;
            }

            event.getHook().sendMessageEmbeds(embed).addActionRow(Button.success("Rescan", "Rescan")).queue();
        } catch (SQLException e) {
            Main.logger.error("Error while executing query!", e);
        }
    }

    public static void rescan() {
        Server server = Rescan.rescan();

        if (server == null) {
            event.getHook().sendMessage("Something went wrong running that command!").queue();
            return;
        }

        ServerEmbedBuilder embedBuilder = new ServerEmbedBuilder(server, true);
        MessageEmbed embed = embedBuilder.build();

        event.getHook().sendMessageEmbeds(embed).queue();
    }
}