package xyz.funtimes909.serverseekerv2_discord_bot.builders;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.Records.PlayerEmbed;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerhistoryEmbedBuilder {
    public static MessageEmbed build(ResultSet results, String title) {
        HashMap<Integer, PlayerEmbed> players = new HashMap<>();

        try (results) {
            int count = 1;
            while (results.next()) {
                players.put(count, new PlayerEmbed(results.getString("address"), results.getString("playername"), results.getString("playeruuid"), results.getLong("lastseen")));
                count++;
            }

            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor("ServerSeekerV2", "https://discord.gg/WEErxAP8kz", "https://funtimes909.xyz/assets/images/serverseekerv2-icon-cropped.png")
                    .setTitle("Showing player history for: " + title)
                    .setFooter("Funtimes909", "https://funtimes909.xyz/avatar-gif")
                    .setColor(new Color(255, 115, 0));

            players.forEach((K, V) -> {
                StringBuilder address = new StringBuilder(V.address());
                while (address.length() < 15) {
                    address.insert(address.length(), " ");
                }
                String emoji = switch (K) {
                    case 1 -> ":number_1:";
                    case 2 -> ":number_2:";
                    case 3 -> ":number_3:";
                    case 4 -> ":number_4:";
                    case 5 -> ":number_5:";
                    case 6 -> ":number_6:";
                    case 7 -> ":number_7:";
                    case 8 -> ":number_8:";
                    case 9 -> ":number_9:";
                    case 10 -> ":number_10:";
                    default -> throw new IllegalStateException("Unexpected value: " + K);
                };
                embed.addField(emoji + ". **``" + address + "``**  **``" + V.playername() + "``**  <t:" + V.lastseen() + ":R>", "_ _", false);
            });

            return embed.build();
        } catch (SQLException e) {
            Main.logger.warn("Failed to execute playerhistory command!", e);
            return null;
        }
    }
}