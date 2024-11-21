package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.util.Database;

import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Stats {
    public static void stats(SlashCommandInteractionEvent event) {
        try (Connection connection = Database.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT COUNT(*) FROM servers");
            results.next();
            int serverCount = results.getInt("count");

            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("Stats")
                    .setColor(new Color(0, 255, 0))
                    .setAuthor("ServerSeekerV2", "https://discord.gg/WEErxAP8kz", "https://funtimes909.xyz/assets/images/serverseekerv2-icon-cropped.png")
                    .setFooter("Funtimes909", "https://funtimes909.xyz/avatar-gif")
                    .addField("Unique Servers Found", "**" + serverCount + "**", false)
                    .build();

            event.getHook().sendMessageEmbeds(embed).queue();
            results.close();
            statement.close();
        } catch (SQLException e) {
            Main.logger.error("Error running the stats command!", e);
        }
    }
}
