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
                .setFooter("Funtimes909", "https://funtimes909.xyz/avatar-gif");

        int longestAddress = 0;
        for (JsonElement entry : servers) {
            JsonObject object = entry.getAsJsonObject();

            if (object.get("address").getAsString().length() > longestAddress) {
                longestAddress = object.get("address").getAsString().length();
            }
        }

        int index = 1;
        for (JsonElement server : servers) {
            JsonObject entry = server.getAsJsonObject();

            StringBuilder address = new StringBuilder("``").append(entry.get("address").getAsString()).append(":").append(entry.get("port").getAsString()).append("``");
            StringBuilder version = new StringBuilder("``").append(entry.get("version").getAsString()).append("``");
            String timestamp = "<t:" + entry.get("last_seen") + ":R>";

            if (!entry.get("country").isJsonNull()) {
                address.insert(0, ":flag_" + entry.get("country").getAsString().toLowerCase() + ": : ");
            } else {
                address.insert(0, ":x: : ");
            }

            if (version.length() > 18) {
                version.setLength(18);
                version.replace(version.length() - 2, version.length(), "``");
            }

            while (version.length() < 18) {
                version.insert(version.length() - 2, " ");
            }

            while (address.length() < longestAddress + 18) {
                address.insert(address.length() - 2, " ");
            }

            MessageEmbed.Field field = new MessageEmbed.Field(index + ". " + address + " - " + version + " - " + timestamp, "_ _", false);
            embed.addField(field);
            index++;
        }

        return embed.build();
    }
}