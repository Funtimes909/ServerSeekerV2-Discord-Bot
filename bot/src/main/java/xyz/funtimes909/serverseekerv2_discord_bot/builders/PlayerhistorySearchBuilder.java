package xyz.funtimes909.serverseekerv2_discord_bot.builders;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class PlayerhistorySearchBuilder {
    public static MessageEmbed build(JsonArray array, String title) {
        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor("ServerSeekerV2", "https://discord.gg/WEErxAP8kz", "https://funtimes909.xyz/assets/images/serverseekerv2-icon-cropped.png")
                .setTitle("Showing player history for: " + title)
                .setFooter("Funtimes909", "https://funtimes909.xyz/avatar-gif")
                .setColor(new Color(0, 255, 0));

        for (int i = 1; i < array.size(); i++) {
            JsonObject object = array.get(i).getAsJsonObject();
            StringBuilder address = new StringBuilder(object.get("address").getAsString());
            address.setLength(16);

            // Add a field for every player
            embed.addField(i + ". ``" +
                    address + "`` ``" +
                    object.get("playername").getAsString() + "``  <t:" +
                    object.get("lastseen").getAsString() + ":R>", "_ _", false);
        }

        return embed.build();
    }
}