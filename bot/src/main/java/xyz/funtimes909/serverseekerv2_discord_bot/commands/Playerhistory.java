package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.builders.PlayerhistoryEmbedBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.util.Database;
import xyz.funtimes909.serverseekerv2_discord_bot.util.GenericErrorEmbed;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Playerhistory {
    public static void playerhistory(SlashCommandInteractionEvent event) {
        if (event.getOption("player") != null && event.getOption("address") != null) {
            event.getHook().sendMessage("Please select only one option!").queue();
            return;
        }

        try (Connection conn = Database.getConnection()) {
            if (conn == null) {
                GenericErrorEmbed.errorEmbed(event.getMessageChannel(), "Failed to connect to database!");
                return;
            }

            PreparedStatement statement = null;

            if (event.getOption("player") != null) {
                statement = conn.prepareStatement("SELECT address, playername, playeruuid, lastseen FROM playerhistory WHERE playername = ? ORDER BY lastseen DESC", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                statement.setString(1, event.getOption("player").getAsString());
            }

            if (event.getOption("address") != null) {
                statement = conn.prepareStatement("SELECT address, playername, playeruuid, lastseen FROM playerhistory WHERE playerhistory.address = ? ORDER BY lastseen DESC", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                statement.setString(1, event.getOption("address").getAsString());
            }

            ResultSet results = statement.executeQuery();
            results.last();
            int rowCount = results.getRow();
            if (rowCount == 0) {
                event.getHook().sendMessage("No playerhistory found!").queue();
                return;
            }

            results.beforeFirst();
            MessageEmbed embed = PlayerhistoryEmbedBuilder.build(results, event.getOption("player") != null ?
                    event.getOption("player").getAsString() :
                    event.getOption("address").getAsString()
            );

            if (embed == null) {
                GenericErrorEmbed.errorEmbed(event.getMessageChannel(), "Something went wrong running this command!");
                return;
            }

            List<ItemComponent> buttons = new ArrayList<>();

            for (int i = 0; i < rowCount; i++) {
                buttons.add(Button.success(String.valueOf(i + 1), String.valueOf(i + 1)));
            }

            if (buttons.size() > 5) {
                event.getHook().sendMessageEmbeds(embed).addActionRow(buttons.stream().limit(5).toList()).queue();
            } else {
                event.getHook().sendMessageEmbeds(embed).addActionRow(buttons).queue();
            }

            statement.close();
            results.close();
        } catch (SQLException e) {
            Main.logger.warn("Exception when running the playerhistory command!", e);
            GenericErrorEmbed.errorEmbed(event.getMessageChannel(), e.getMessage());
        }
    }
}