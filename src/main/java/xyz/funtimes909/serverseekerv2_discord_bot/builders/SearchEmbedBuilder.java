package xyz.funtimes909.serverseekerv2_discord_bot.builders;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import xyz.funtimes909.serverseekerv2_discord_bot.commands.Search;
import xyz.funtimes909.serverseekerv2_discord_bot.records.ServerEmbed;
import xyz.funtimes909.serverseekerv2_discord_bot.util.HttpUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchEmbedBuilder {
    public static MessageEmbed parse(HashMap<Integer, ServerEmbed> servers) {
        List<MessageEmbed.Field> fields = new ArrayList<>();
        int longestAddress = 0;
        int longestVersion = 0;

        for (HashMap.Entry<Integer, ServerEmbed> entry : servers.entrySet()) {
            if (entry.getValue().address().length() > longestAddress) longestAddress = entry.getValue().address().length();
            if (entry.getValue().version().length() > longestVersion) longestVersion = entry.getValue().version().length();
        }

        for (HashMap.Entry<Integer, ServerEmbed> entry : servers.entrySet()) {
            StringBuilder address = new StringBuilder("``").append(entry.getValue().address()).append("``");
            StringBuilder version = new StringBuilder("``").append(entry.getValue().version()).append("``");
            String timestamp = "<t:" + entry.getValue().timestamp() + ":R>";

            if (entry.getValue().country() != null) {
                address.insert(0, ":flag_" + entry.getValue().country().toLowerCase() + ": **:** ");
            } else {
                String primaryResponse = HttpUtils.run(entry.getValue().address());
                if (primaryResponse != null) {
                    JsonObject parsedPrimaryResponse = JsonParser.parseString(primaryResponse).getAsJsonObject();
                    if (parsedPrimaryResponse.has("countryCode")) address.insert(0, ":flag_" + parsedPrimaryResponse.get("countryCode").getAsString().toLowerCase() + ": **:** ");
                }
            }

            // Make everything the same length
            while (version.length() < longestVersion + 4) {
                version.insert(version.length() - 2, " ");
            }

            while (address.length() < longestAddress + 20) {
                address.insert(address.length() - 2, " ");
            }

            MessageEmbed.Field addressField = new MessageEmbed.Field(entry.getKey() + ". " + address + " **-** " + version + " **-** " + timestamp, "_ _", false);
            fields.add(addressField);
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(new Color(0, 255, 0))
                .setTitle("Results: " + Search.totalRows)
                .setAuthor("ServerSeekerV2", "https://discord.gg/WEErxAP8kz", "https://funtimes909.xyz/assets/images/serverseekerv2-icon-cropped.png")
                .setFooter("Funtimes909", "https://funtimes909.xyz/avatar-gif");

        for (MessageEmbed.Field field : fields) {
            embed.addField(field);
        }

        return embed.build();
    }
}
