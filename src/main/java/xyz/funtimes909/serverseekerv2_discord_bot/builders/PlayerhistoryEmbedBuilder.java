package xyz.funtimes909.serverseekerv2_discord_bot.builders;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.Records.PlayerEmbed;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayerhistoryEmbedBuilder {
    public static MessageEmbed build(ResultSet results, String title) {
        List<PlayerEmbed> players = new ArrayList<>();

        try (results) {
            while (results.next()) {
                players.add(new PlayerEmbed(results.getString("address"), results.getString("playername"), results.getString("playeruuid"), results.getLong("lastseen")));
            }

            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor("ServerSeekerV2", "https://discord.gg/WEErxAP8kz", "https://funtimes909.xyz/assets/images/serverseekerv2-icon-cropped.png")
                    .setTitle("Showing player history for: " + title)
                    .setFooter("Funtimes909", "https://funtimes909.xyz/avatar-gif")
                    .setColor(new Color(255, 115, 0));

            for (PlayerEmbed player : players) {
                StringBuilder address = new StringBuilder(player.address());
                while (address.length() < 15) {
                    address.insert(address.length(), " ");
                }

                embed.addField("**``" + address + "``** **``" + player.playername() + "``** <t:" + player.lastseen() + ":R>", "_ _ ", false);
            }

            return embed.build();
        } catch (SQLException e) {
            Main.logger.warn("Failed to execute playerhistory command!", e);
            return null;
        }
    }
}