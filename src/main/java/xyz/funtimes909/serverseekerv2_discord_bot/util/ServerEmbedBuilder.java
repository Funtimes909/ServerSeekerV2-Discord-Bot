package xyz.funtimes909.serverseekerv2_discord_bot.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServerEmbedBuilder {
    public static MessageEmbed build(ResultSet results) {
        try (results) {
            results.next();
            String description = "N/A";
            String country = "N/A";
            String asn = "N/A";
            String hostname = "N/A";
            String organization = "N/A";
            StringBuilder miscInfo = new StringBuilder();
            StringBuilder addressInfo = new StringBuilder();

            if (results.getString("motd") != null) {
                description = results.getString("motd");
            }

            if (description.isBlank()) {
                description = "N/A";
            }

            // Build address information
            if (results.getString("country") != null) {
                country = results.getString("country");
            }

            if (results.getString("asn") != null) {
                addressInfo.append("ASN: **").append(results.getString("asn")).append("**");
            }

            if (results.getString("reversedns") != null) {
                addressInfo.append("Hostname: **").append(results.getString("reversedns")).append("**");
            }

            if (results.getString("organization") != null) {
                addressInfo.append("Org: **").append(results.getString("organization")).append("**");
            }

            // Build Miscellaneous information
            miscInfo.append("Times Seen: **").append(results.getInt("timesSeen")).append("** \n");
            miscInfo.append(results.getBoolean("whitelist") ? "Whitelist: **True** \n" : "Whitelist: **False** \n");
            miscInfo.append(results.getBoolean("cracked") ? "Cracked: **True** \n" : "Cracked: **False** \n");
            miscInfo.append(results.getBoolean("preventsReports") ? "Prevents Chat Reports: **True** \n" : "Prevents Chat Reports: **False** \n");
            miscInfo.append(results.getBoolean("enforceSecure") ? "Enforces Secure Chat: **True** \n" : "Enforces Secure Chat: **False** \n");

            if (results.getInt("FmlNetworkVersion") != 0) {
                miscInfo.append("Forge: **True** \n");
            }

            // Build player information field
            MessageEmbed.Field players = new MessageEmbed.Field("**__Players__**",
                    "Max Players: **" + results.getInt("maxPlayers") + "** \n" +
                            "Online Players: **" + results.getInt("onlinePlayers") + "**", false);

            // Build miscellaneous information field
            MessageEmbed.Field misc = new MessageEmbed.Field("**__Miscellaneous__**", miscInfo.toString(), false);
            MessageEmbed.Field addressInformation = new MessageEmbed.Field("**__Address Information__**", addressInfo.toString(), false);

            // Build server information embed
            return new EmbedBuilder()
                    .setColor(new Color(0, 255, 0))
                    .setAuthor("ServerSeekerV2")
                    .setThumbnail("https://funtimes909.xyz/avatar-gif")
                    .setTitle(results.getString("address") + ":" + results.getString("port"))
                    .addField("**__Version__**", results.getString("version") + " (" + results.getString("protocol") + ")", false)
                    .addField("**__Description__**", description, false)
                    .addField("**__Country__**", ":flag_" + country.toLowerCase() + ": " + country, false)
                    .addField("**__First Seen__**", "<t:" + results.getString("firstseen") + ">", false)
                    .addField("**__Last Seen__**", "<t:" + results.getString("lastseen") + ">", false)
                    .addField(misc)
                    .addField(players)
                    .addField(addressInformation)
                    .build();

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
