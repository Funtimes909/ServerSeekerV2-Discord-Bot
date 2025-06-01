package xyz.funtimes909.serverseekerv2_discord_bot.builders;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class PlayerhistorySearchBuilder {
    public static MessageEmbed build(JsonArray array, String title) {
        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor("ServerSeekerV2", "https://discord.gg/UA5kyprunc", "https://cdn.discordapp.com/app-icons/1375333922765930556/bcc3069c7e9fdb44107faeb74477127d.png?size=256")
                .setFooter("Made with <3 by Funtimes909", "https://funtimes909.xyz/assets/images/floppa.png")
                .setTitle("Showing player history for: " + title)
                .setColor(new Color(0, 255, 0));

        int longestName = 0;

        // The longest length name must be known before we create the embed
        // First we have to iterate over all the elements to get the length
        for (JsonElement element : array) {
            JsonObject object = element.getAsJsonObject();

            String name = object.get("name").getAsString();
            if (name.length() > longestName) longestName = name.length();
        }

        for (int i = 0; i < array.size(); i++) {
            if (i > 9) break;
            JsonObject object = array.get(i).getAsJsonObject();

            StringBuilder address = new StringBuilder(object.get("address").getAsString() + ":" + object.get("port").getAsShort());
            StringBuilder name = new StringBuilder(object.get("name").getAsString());

            // Index 10 has an extra indent
            if (i == 9) {
                while (address.length() < 23) {
                    address.insert(address.length(), " ");
                }
            } else {
                // Ensure it's all the same length
                while (address.length() < 24) {
                    address.insert(address.length(), " ");
                }
            }

            // Ensure all player names are consistent lengths
            while (name.length() < longestName) {
                name.insert(name.length(), " ");
            }

            // Add a field for every player
            String field = String.format("%d. ``%s`` ``%s`` <t:%d:R>",
                    (i + 1),
                    address,
                    name,
                    object.get("last_seen").getAsInt()
            );
            embed.addField(field, "_ _", false);
        }

        return embed.build();
    }
}