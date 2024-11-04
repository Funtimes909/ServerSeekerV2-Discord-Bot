package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.util.BlacklistCheck;
import xyz.funtimes909.serverseekerv2_discord_bot.util.DatabaseConnectionPool;
import xyz.funtimes909.serverseekerv2_discord_bot.util.ServerEmbedBuilder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Random {
    public static void random(SlashCommandInteractionEvent event) {
        if (BlacklistCheck.check(event.getUser().getId())) {
            event.reply("Sorry! You're not authorized to use this command!").queue();
            return;
        }
        event.deferReply().queue();

        try {
            // Create connection and query
            Connection conn = DatabaseConnectionPool.getConnection();
            Statement random = conn.createStatement();
            long startTime = System.currentTimeMillis();
            String query = "SELECT * FROM servers ORDER BY RANDOM() LIMIT 1";
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            Main.logger.info("Query took {}ms", duration);

            ResultSet result = random.executeQuery(query);

            MessageEmbed embed = ServerEmbedBuilder.build(result);
            if (embed != null) event.getHook().sendMessageEmbeds(embed).queue();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
