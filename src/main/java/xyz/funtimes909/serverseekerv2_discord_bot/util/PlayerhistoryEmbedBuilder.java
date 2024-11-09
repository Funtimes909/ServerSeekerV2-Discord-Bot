package xyz.funtimes909.serverseekerv2_discord_bot.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayerhistoryEmbedBuilder {
    private record PlayerEmbed(String address, String playername, String uuid, long lastseen) {}
    private static List<PlayerEmbed> players = new ArrayList<>();

    public static MessageEmbed build(ResultSet results) {
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
                    .setColor(new Color(5, 106, 5));

            return embed.build();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
