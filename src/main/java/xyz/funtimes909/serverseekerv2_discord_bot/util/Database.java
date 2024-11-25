package xyz.funtimes909.serverseekerv2_discord_bot.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.apache.commons.dbcp2.BasicDataSource;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;

import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

public class Database {
    private static final BasicDataSource dataSource = new BasicDataSource();

    public static void initPool() {
        dataSource.setUrl("jdbc:postgresql://" + Main.url);
        dataSource.setPassword(Main.password);
        dataSource.setUsername(Main.username);
        dataSource.setMinIdle(20);
    }

    public static Connection getConnection(MessageChannel channel) {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            Main.logger.error("Couldn't get connection to the database!", e);
            if (channel != null) {
                MessageEmbed embed = new EmbedBuilder()
                        .setColor(new Color(0xFF0000))
                        .setTitle("Fatal database error!")
                        .addField("Database Error Message", e.getMessage(), false)
                        .build();

                channel.sendMessageEmbeds(embed).addActionRow(Button.link("https://github.com/Funtimes909/ServerSeekerV2-Discord-Bot/issues", "Report Bug")).queue();
            }
            return null;
        }
    }
}
