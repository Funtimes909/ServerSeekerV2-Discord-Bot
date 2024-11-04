package xyz.funtimes909.serverseekerv2_discord_bot.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
                addressInfo.append("ASN: **").append(results.getString("asn")).append("** \n");
            } else {
                addressInfo.append("ASN: **N/A** \n");
            }

            if (results.getString("reversedns") != null) {
                addressInfo.append("Hostname: **").append(results.getString("reversedns")).append("** \n");
            } else {
                addressInfo.append("Hostname: **N/A** \n");
            }

            if (results.getString("organization") != null) {
                addressInfo.append("Organization: **").append(results.getString("organization")).append("** \n");
            } else {
                addressInfo.append("Organization: **N/A** \n");
            }

            Connection conn = DatabaseConnectionPool.getConnection();
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM playerhistory WHERE address = ? AND port = ?");
            statement.setString(1, results.getString("address"));
            statement.setInt(2, results.getInt("port"));

            ResultSet resultSet = statement.executeQuery();

            List<String> playerNames = new ArrayList<>();
            List<String> playerUuids = new ArrayList<>();

            while (resultSet.next()) {
                playerNames.add(resultSet.getString("playername"));
                playerUuids.add(resultSet.getString("playeruuid"));
            }

            for (String playerName : playerNames) {
                System.out.println(playerName);
            }

            for (String playerUuid : playerUuids) {
                System.out.println(playerUuid);
            }

            StringBuilder players = new StringBuilder();


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
            MessageEmbed.Field players2 = new MessageEmbed.Field("**__Players__**",
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
                    .addField("**__Description__**", "```" + description + "```", false)
                    .addField("**__Country__**", ":flag_" + country.toLowerCase() + ": " + country, false)
                    .addField("**__First Seen__**", "<t:" + results.getString("firstseen") + ">", false)
                    .addField("**__Last Seen__**", "<t:" + results.getString("lastseen") + ">", false)
                    .addField(misc)
                    .addField(players2)
                    .addField(addressInformation)
                    .build();

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
