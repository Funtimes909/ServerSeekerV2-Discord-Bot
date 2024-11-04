package xyz.funtimes909.serverseekerv2_discord_bot.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchCommandBuilder {
    public static boolean compact = true;
    public static MessageEmbed parse(ResultSet resultSet, SlashCommandInteractionEvent event) {
        List<MessageEmbed.Field> fields = new ArrayList<>();

        try {
            int current = 1;
            while (resultSet.next() && current < 6) {
                StringBuilder address = new StringBuilder("``").append(resultSet.getString("address")).append("``");
                StringBuilder version = new StringBuilder("``").append(resultSet.getString("version")).append("``");
                StringBuilder description = new StringBuilder("**").append(resultSet.getString("motd")).append("**");
                String organization = "**" + resultSet.getString("organization") + "**";
                String timestamp = "<t:" + resultSet.getLong("lastseen") + ">";
                String protocol = "**Protocol (" + resultSet.getInt("protocol") + ")**";
                address.insert(0, ":flag_" + resultSet.getString("country").toLowerCase() + ": **:**");

                // Make everything the same length
                if (version.length() > 20) {
                    version.setLength(20);
                    version.replace(version.length() - 5, version.length(), "...``");
                }

                while (version.length() < 17) {
                    version.insert(version.length() - 2, " ");
                }

                while (address.length() < 34) {
                    address.insert(address.length() - 2, " ");
                }

                if (description.length() > 24) {
                    description.insert(16, "\n");
                }

                if (description.toString().isBlank()) {
                    description.replace(0, description.length(), "N/A");
                }

                // Add embeds to list
                MessageEmbed.Field addressField = new MessageEmbed.Field(address + " - ", compact ? "_ _" : description.toString(), true);
                MessageEmbed.Field versionField = new MessageEmbed.Field(version + " - ", compact ? "_ _" : protocol, true);
                MessageEmbed.Field timestampField = new MessageEmbed.Field(timestamp, compact ? "_ _" : organization, true);
                fields.add(addressField);
                fields.add(versionField);
                fields.add(timestampField);

                current += 1;
            }

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Results: ")
                    .setAuthor("ServerSeekerV2", "https://discord.gg/WEErxAP8kz", "https://funtimes909.xyz/I-wake-up-mad.jpg")
                    .setFooter("Funtimes909", "https://funtimes909.xyz/avatar-gif")
                    .setColor(new Color(5, 106, 5));

            for (MessageEmbed.Field field : fields) {
                embed.addField(field);
            }

            return embed.build();
        } catch (SQLException e) {
            Main.logger.error("SQL Exception running the search command! [Channel: {}] [Options: {}]", event.getChannel(), event.getOptions(), e);
        }
        return null;
    }
}
