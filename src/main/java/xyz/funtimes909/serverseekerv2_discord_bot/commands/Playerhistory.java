package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.util.BlacklistCheck;
import xyz.funtimes909.serverseekerv2_discord_bot.util.DatabaseConnectionPool;
import xyz.funtimes909.serverseekerv2_discord_bot.util.PlayerhistoryEmbedBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Playerhistory {
    public static void playerhistory(SlashCommandInteractionEvent event) {
        if (BlacklistCheck.check(event.getUser().getId())) event.reply("Sorry! You're not authorized to use this command!").queue();

        if (event.getOptions().isEmpty()) {
            event.reply("You must provide a user to search for!").queue();
            return;
        }

        event.deferReply().queue();

        try (Connection conn = DatabaseConnectionPool.getConnection()) {
            PreparedStatement statement = conn.prepareStatement("SELECT address, playername, playeruuid, lastseen FROM playerhistory WHERE playername = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, event.getOption("player").getAsString());
            ResultSet results = statement.executeQuery();

            results.last();
            int rows = results.getRow();
            if (rows == 0) {
                event.getHook().sendMessage("No playerhistory found!").queue();
                return;
            }

            results.beforeFirst();
            MessageEmbed embed = PlayerhistoryEmbedBuilder.build(results);

            if (embed != null) {
                event.getHook().sendMessageEmbeds(embed).queue();
            }

        } catch (SQLException e) {
            event.reply("Something went wrong executing this command!").queue();
            Main.logger.warn("Exception when running the playerhistory command!", e);
        }
    }
}