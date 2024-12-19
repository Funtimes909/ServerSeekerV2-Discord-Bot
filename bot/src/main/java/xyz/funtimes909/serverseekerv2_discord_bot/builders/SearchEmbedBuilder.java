package xyz.funtimes909.serverseekerv2_discord_bot.builders;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import xyz.funtimes909.serverseekerv2_core.util.HTTPUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SearchEmbedBuilder {
    public static MessageEmbed parse(JsonArray servers, int rowCount, int page) {
        List<MessageEmbed.Field> fields = new ArrayList<>();
        int longestAddress = 0;

        for (JsonElement server : servers) {
            JsonObject serverJson = server.getAsJsonObject();

            if (serverJson.get("address").getAsString().length() > longestAddress) {
                longestAddress = serverJson.get("address").getAsString().length();
            }
        }

        int index = 1;
        for (JsonElement server : servers) {
            JsonObject entry = server.getAsJsonObject();

            StringBuilder address = new StringBuilder("``").append(entry.get("address").getAsString()).append("``");
            StringBuilder version = new StringBuilder("``").append(entry.get("version").getAsString()).append("``");
            String timestamp = "<t:" + entry.get("lastseen") + ":R>";

            if (!entry.get("country").isJsonNull()) {
                address.insert(0, ":flag_" + entry.get("country").getAsString().toLowerCase() + ": **:** ");
            } else {
                String primaryResponse = HTTPUtils.run(entry.get("address").getAsString());
                if (primaryResponse != null) {
                    JsonObject parsedPrimaryResponse = JsonParser.parseString(primaryResponse).getAsJsonObject();
                    if (parsedPrimaryResponse.has("countryCode")) {
                        address.insert(0, ":flag_" + parsedPrimaryResponse.get("countryCode").getAsString().toLowerCase() + ": **:** ");
                    }
                }
            }

            if (version.length() > 18) {
                version.setLength(18);
                version.replace(version.length() - 2, version.length(), "``");
            }

            // Make everything the same length
            while (version.length() < 18) {
                version.insert(version.length() - 2, " ");
            }

            while (address.length() < longestAddress + 20) {
                address.insert(address.length() - 2, " ");
            }

            MessageEmbed.Field addressField = new MessageEmbed.Field(index + ". " + address + " - " + version + " - " + timestamp, "_ _", false);
            fields.add(addressField);
            index++;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(new Color(0, 255, 0))
                .setTitle("Page: " + page + " (Total Results: " + rowCount + ")")
                .setAuthor("ServerSeekerV2", "https://discord.gg/WEErxAP8kz", "https://funtimes909.xyz/assets/images/serverseekerv2-icon-cropped.png")
                .setFooter("Funtimes909", "https://funtimes909.xyz/avatar-gif");

        for (MessageEmbed.Field field : fields) {
            embed.addField(field);
        }

        return embed.build();
    }
}
