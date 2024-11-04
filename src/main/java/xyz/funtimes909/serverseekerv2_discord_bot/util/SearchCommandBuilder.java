package xyz.funtimes909.serverseekerv2_discord_bot.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import xyz.funtimes909.serverseekerv2_discord_bot.commands.Search;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchCommandBuilder {
    public static boolean compact = true;

    public static MessageEmbed parse(HashMap<Integer, Server> servers) {
        List<MessageEmbed.Field> fields = new ArrayList<>();

        servers.values().forEach(server -> {
            StringBuilder address = new StringBuilder("``").append(server.address()).append("``");
            StringBuilder version = new StringBuilder("``").append(server.version()).append("``");
            String timestamp = "<t:" + server.timestamp() + ">";
            address.insert(0, ":flag_" + server.country().toLowerCase() + ": **:**");

            // Make everything the same length
            if (version.length() > 17) {
                version.setLength(17);
                version.replace(version.length() - 5, version.length(), "...``");
            }

            while (version.length() < 17) {
                version.insert(version.length() - 2, " ");
            }

            while (address.length() < 34) {
                address.insert(address.length() - 2, " ");
            }

            MessageEmbed.Field addressField = new MessageEmbed.Field(address + " **-** ", "_ _", true);
            MessageEmbed.Field versionField = new MessageEmbed.Field(version + " **-** ", "_ _", true);
            MessageEmbed.Field timestampField = new MessageEmbed.Field(timestamp, "_ _", true);

            fields.add(addressField);
            fields.add(versionField);
            fields.add(timestampField);
        });

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Results: (" + Search.searchResults.size() + "/" + Search.rowCount + ")")
                .setAuthor("ServerSeekerV2", "https://discord.gg/WEErxAP8kz", "https://funtimes909.xyz/I-wake-up-mad.jpg")
                .setFooter("Funtimes909", "https://funtimes909.xyz/avatar-gif")
                .setColor(new Color(5, 106, 5));

        for (MessageEmbed.Field field : fields) {
            embed.addField(field);
        }

        return embed.build();
    }
}
