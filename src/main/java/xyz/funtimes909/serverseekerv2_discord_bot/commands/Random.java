package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.util.DatabaseConnectionPool;
import xyz.funtimes909.serverseekerv2_discord_bot.util.PermissionsCheck;
import xyz.funtimes909.serverseekerv2_discord_bot.util.ServerEmbedBuilder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Random {
    public static void random(SlashCommandInteractionEvent event) {
        if (PermissionsCheck.blacklistCheck(event.getUser().getId())) {
            event.reply("Sorry! You're not authorized to use this command!").queue();
            return;
        }
        event.deferReply().queue();

        try {
            // Create connection and query
            Connection conn = DatabaseConnectionPool.getConnection();
            Statement random = conn.createStatement();
            long startTime = System.currentTimeMillis() / 1000;
            String query = "SELECT * FROM servers ORDER BY RANDOM() LIMIT 1";
            long endTime = System.currentTimeMillis() / 1000;
            long duration = endTime - startTime;
            Main.logger.debug("Query took {}ms", duration);

            ResultSet results = random.executeQuery(query);
            ServerEmbedBuilder embedBuilder = new ServerEmbedBuilder(results, false);
            MessageEmbed embed = embedBuilder.build();

            if (embed != null) event.getHook().sendMessageEmbeds(embed).queue();
        } catch (SQLException e) {
            Main.logger.warn("SQL Exception", e);
        }
    }
}
