package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.builders.PlayerhistoryEmbedBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.util.DatabaseConnectionPool;
import xyz.funtimes909.serverseekerv2_discord_bot.util.PermissionsCheck;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Playerhistory {
    public static void playerhistory(SlashCommandInteractionEvent event) {
        if (PermissionsCheck.blacklistCheck(event.getUser().getId())) {
            event.reply("Sorry! You're not authorized to use this command!").queue();
            return;
        }

        if (event.getOption("player") != null && event.getOption("address") != null) {
            event.reply("Please select only one option!").queue();
            return;
        }
        event.deferReply().queue();

        try (Connection conn = DatabaseConnectionPool.getConnection()) {
            PreparedStatement statement = null;

            if (event.getOption("player") != null) {
                statement = conn.prepareStatement("SELECT address, playername, playeruuid, lastseen FROM playerhistory WHERE playername = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                statement.setString(1, event.getOption("player").getAsString());
            }

            if (event.getOption("address") != null) {
                statement = conn.prepareStatement("SELECT address, playername, playeruuid, lastseen FROM playerhistory WHERE playerhistory.address = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                statement.setString(1, event.getOption("address").getAsString());
            }

            ResultSet results = statement.executeQuery();
            results.last();
            if (results.getRow() == 0) {
                event.getHook().sendMessage("No playerhistory found!").queue();
                return;
            }

            results.beforeFirst();
            MessageEmbed embed = PlayerhistoryEmbedBuilder.build(results, event.getOption("player") != null ? event.getOption("player").getAsString() : event.getOption("address").getAsString());
            if (embed != null) event.getHook().sendMessageEmbeds(embed).queue();
            statement.close();
            results.close();
        } catch (SQLException e) {
            event.getHook().sendMessage("Something went wrong executing this command!").queue();
            Main.logger.warn("Exception when running the playerhistory command!", e);
        }
    }
}