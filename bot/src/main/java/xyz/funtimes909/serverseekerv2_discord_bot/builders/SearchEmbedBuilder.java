package xyz.funtimes909.serverseekerv2_discord_bot.builders;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class SearchEmbedBuilder {
    public static MessageEmbed parse(JsonArray servers, int rowCount, int page) {
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(new Color(0, 255, 0))
                .setTitle("Page: " + page + " (Total Results: " + rowCount + ")")
                .setAuthor("ServerSeekerV2", "https://discord.gg/UA5kyprunc", "https://cdn.discordapp.com/app-icons/1375333922765930556/bcc3069c7e9fdb44107faeb74477127d.png?size=256")
                .setFooter("Funtimes909", "https://funtimes909.xyz/assets/images/floppa.png");

        int index = 1;
        for (JsonElement server : servers) {
            JsonObject entry = server.getAsJsonObject();

            StringBuilder address = new StringBuilder(String.format("``%s:%d``", entry.get("address").getAsString(), entry.get("port").getAsShort()));
            StringBuilder version = new StringBuilder(String.format("``%s``", entry.get("version").getAsString()));

            // Add the countries flag before the index if it exists
            if (!entry.get("country").isJsonNull()) {
                address.insert(0, ":flag_" + entry.get("country").getAsString().toLowerCase() + ": : ");

                // Ensure it's all the same length
                while (address.length() < 38) {
                    address.insert(address.length() - 2, ' ');
                }
            } else {
                address.insert(0, ":x: : ");

                // Ensure it's all the same length
                while (address.length() < 32) {
                    address.insert(address.length() - 2, ' ');
                }
            }

            // Make sure version field is the same length always
            if (version.length() > 10) {
                version.setLength(8);
                version.insert(version.length(), "``");
            }

            while (version.length() < 10) {
                version.insert(version.length() - 2, ' ');
            }

            MessageEmbed.Field field = new MessageEmbed.Field(String.format("%d. %s - %s - %s",
                    index,
                    address,
                    version,
                    String.format("<t:%d:R>", entry.get("last_seen").getAsLong())
            ), "_ _", false);
            embed.addField(field);
            index++;
        }

        return embed.build();
    }
}