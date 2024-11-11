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
    public static MessageEmbed build(ResultSet results) {
        List<PlayerEmbed> players = new ArrayList<>();
        try (results) {
            while (results.next()) players.add(new PlayerEmbed(results.getString("address"), results.getString("playername"), results.getString("playeruuid"), results.getLong("lastseen")));
            StringBuilder playersInfo = new StringBuilder();

            players.forEach(player -> {
                playersInfo.append("**``").append(player.address()).append("``** **``").append(player.playername()).append("``** ").append("<t:").append(player.lastseen()).append(":R> \n");
            });

            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor("ServerSeekerV2", "https://discord.gg/WEErxAP8kz", "https://funtimes909.xyz/assets/images/serverseekerv2-icon-cropped.png")
                    .addField("Players", playersInfo.toString(), false)
                    .setFooter("Funtimes909", "https://funtimes909.xyz/avatar-gif")
                    .setColor(new Color(248, 127, 5));

            return embed.build();
        } catch (SQLException e) {
            Main.logger.warn("Failed to execute playerhistory command!", e);
            return null;
        }
    }
}
