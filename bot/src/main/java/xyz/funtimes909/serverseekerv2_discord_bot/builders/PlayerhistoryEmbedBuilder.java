package xyz.funtimes909.serverseekerv2_discord_bot.builders;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class PlayerhistoryEmbedBuilder {
    public static MessageEmbed build(JsonArray playersArray, String title) {
        if (playersArray.isEmpty()) return null;

        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor("ServerSeekerV2", "https://discord.gg/WEErxAP8kz", "https://funtimes909.xyz/assets/images/serverseekerv2-icon-cropped.png")
                .setTitle("Showing player history for: " + title)
                .setFooter("Funtimes909", "https://funtimes909.xyz/avatar-gif")
                .setColor(new Color(0, 255, 0));

        int i = 1;
        for (JsonElement result : playersArray.getAsJsonArray()) {
            JsonObject resultObj = result.getAsJsonObject();
            StringBuilder address = new StringBuilder(resultObj.get("address").getAsString());
            address.setLength(16);

            // Add a field for every player
            embed.addField(":number_" + i + ": **``" +
                    address + "``**  **``" +
                    resultObj.get("playername").getAsString() + "``**  <t:" +
                    resultObj.get("lastseen").getAsString() + ":R>", "_ _", false);

            i++;
        }

        return embed.build();
    }
}