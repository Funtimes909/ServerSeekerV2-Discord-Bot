package xyz.funtimes909.serverseekerv2_discord_bot.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServerEmbedBuilder {
    public static MessageEmbed build(ResultSet results) {
        try (results) {
            results.next();
            String description = "N/A";
            String country = "N/A";
            StringBuilder miscInfo = new StringBuilder();
            StringBuilder addressInfo = new StringBuilder();

            // Build address information
            if (description.isBlank()) description = "N/A";
            if (results.getString("motd") != null) description = results.getString("motd");
            if (results.getString("country") != null) country = results.getString("country");

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
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM playerhistory WHERE address = ? AND port = ? AND lastseen = ? ", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, results.getString("address"));
            statement.setInt(2, results.getInt("port"));
            statement.setLong(3, results.getLong("lastseen"));

            ResultSet resultSet = statement.executeQuery();
            StringBuilder playerInfo = new StringBuilder();

            while (resultSet.next()) {
                playerInfo.append("\n").append(resultSet.getString("playername")).append("\n").append(resultSet.getString("playeruuid")).append("\n");
            }

            conn.close();
            if (playerInfo.isEmpty()) playerInfo.append("No players found online!");

            // Build Miscellaneous information
            miscInfo.append("Times Seen: **").append(results.getInt("timesSeen")).append("** \n");
            miscInfo.append(results.getBoolean("whitelist") ? "Whitelist: **True** \n" : "Whitelist: **False** \n");
            miscInfo.append(results.getBoolean("cracked") ? "Cracked: **True** \n" : "Cracked: **False** \n");
            miscInfo.append(results.getBoolean("preventsReports") ? "Prevents Chat Reports: **True** \n" : "Prevents Chat Reports: **False** \n");
            miscInfo.append(results.getBoolean("enforceSecure") ? "Enforces Secure Chat: **True** \n" : "Enforces Secure Chat: **False** \n");

            if (results.getInt("FmlNetworkVersion") != 0) miscInfo.append("Forge: **True** \n");

            // Build player information field
            MessageEmbed.Field playersField = new MessageEmbed.Field("** -- __Players__ -- **",
                    "Max Players: **" + results.getInt("maxPlayers") + "** \n" +
                            "Online Players: **" + results.getInt("onlinePlayers") + "** \n" +
                            "```" + playerInfo + "```", false);

            // Build miscellaneous information field
            MessageEmbed.Field misc = new MessageEmbed.Field("** -- __Miscellaneous__ -- **", miscInfo.toString(), false);
            MessageEmbed.Field addressInformation = new MessageEmbed.Field("** -- __Address Information__ -- **", addressInfo.toString(), false);

            // Build server information embed
            return new EmbedBuilder()
                    .setColor(new Color(0, 255, 0))
                    .setAuthor("ServerSeekerV2")
                    .setThumbnail("https://funtimes909.xyz/avatar-gif")
                    .setTitle(results.getString("address") + ":" + results.getString("port"))
                    .addField("** -- __Version__ -- **", results.getString("version") + " (" + results.getString("protocol") + ")", false)
                    .addField("** -- __Description__ -- **", "```" + description + "```", false)
                    .addField("** -- __Country__ -- **", ":flag_" + country.toLowerCase() + ": " + country, false)
                    .addField("** -- __First Seen__ -- **", "<t:" + results.getString("firstseen") + ">", false)
                    .addField("** -- __Last Seen__ -- **", "<t:" + results.getString("lastseen") + ">", false)
                    .addField(misc)
                    .addField(playersField)
                    .addField(addressInformation)
                    .build();

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
