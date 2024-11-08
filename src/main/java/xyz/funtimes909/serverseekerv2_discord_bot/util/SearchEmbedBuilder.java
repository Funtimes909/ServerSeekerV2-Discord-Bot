package xyz.funtimes909.serverseekerv2_discord_bot.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import xyz.funtimes909.serverseekerv2_discord_bot.Records.ServerEmbed;
import xyz.funtimes909.serverseekerv2_discord_bot.commands.Search;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchEmbedBuilder {
    public static boolean compact = true;

    public static MessageEmbed parse(HashMap<Integer, ServerEmbed> servers) {
        List<MessageEmbed.Field> fields = new ArrayList<>();

        final int[] index = {1};
        servers.values().forEach(server -> {
            StringBuilder address = new StringBuilder("``").append(server.address()).append("``");
            StringBuilder version = new StringBuilder("``").append(server.version()).append("``");
            String timestamp = "<t:" + server.timestamp() + ":R>";

            if (server.country() != null) {
                address.insert(0, ":flag_" + server.country().toLowerCase() + ": **:**");
            } else {
                address.insert(0, ":x: **:**");
            }

            // Make everything the same length
            if (version.length() > 17) {
                version.setLength(17);
                version.replace(version.length() - 5, version.length(), "...``");
            }

            while (version.length() < 17) {
                version.insert(version.length() - 2, " ");
            }

            if (server.country() != null) {
                while (address.length() < 34) {
                    address.insert(address.length() - 2, " ");
                }
            } else {
                while (address.length() < 28) {
                    address.insert(address.length() - 2, " ");
                }
            }

            MessageEmbed.Field addressField = new MessageEmbed.Field(index[0] + ". " + address + " **-** ", "_ _", true);
            MessageEmbed.Field versionField = new MessageEmbed.Field(version + " **-** ", "_ _", true);
            MessageEmbed.Field timestampField = new MessageEmbed.Field(timestamp, "_ _", true);

            fields.add(addressField);
            fields.add(versionField);
            fields.add(timestampField);

            index[0]++;
        });

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Pages: (" + Search.page + "/" + Search.rowCount / 5 + ")")
                .setAuthor("ServerSeekerV2", "https://discord.gg/WEErxAP8kz", "https://funtimes909.xyz/I-wake-up-mad.jpg")
                .addField("**Address**", "_ _", true)
                .addField("**Version**", "_ _", true)
                .addField("**Last Seen**", "_ _", true)
                .setFooter("Funtimes909", "https://funtimes909.xyz/avatar-gif")
                .setColor(new Color(5, 106, 5));

        for (MessageEmbed.Field field : fields) {
            embed.addField(field);
        }

        return embed.build();
    }
}
