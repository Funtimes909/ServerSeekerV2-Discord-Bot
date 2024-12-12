package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import xyz.funtimes909.serverseekerv2_core.records.Server;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.builders.ServerEmbedBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.util.ConnectionPool;
import xyz.funtimes909.serverseekerv2_discord_bot.util.PingUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutionException;

public class Random {
    public static void random(SlashCommandInteractionEvent event) {
        try (Connection conn = ConnectionPool.getConnection()) {
            Statement statement = conn.createStatement();
            long startTime = System.currentTimeMillis() / 1000;
            String query = "SELECT * FROM servers LEFT JOIN playerhistory ON servers.address = playerhistory.address AND servers.port = playerhistory.port LEFT JOIN mods ON servers.address = mods.address AND servers.port = mods.port ORDER BY RANDOM() LIMIT 1";
            Main.logger.debug("Query took {}ms", System.currentTimeMillis() / 1000L - startTime);

            Server server = PingUtils.buildResultsToObject(statement.executeQuery(query));
            if (server == null) return;

            ServerEmbedBuilder embedBuilder = new ServerEmbedBuilder(server);
            MessageEmbed embed = embedBuilder.build(event.getMessageChannel(), true).get();

            if (embed != null) event.getHook().sendMessageEmbeds(embed).queue();
        } catch (SQLException | ExecutionException | InterruptedException e) {
            Main.logger.warn("SQL Exception while running the random command!", e);
        }
    }
}
