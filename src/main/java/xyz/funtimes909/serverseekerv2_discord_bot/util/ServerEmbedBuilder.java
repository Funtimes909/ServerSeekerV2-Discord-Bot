package xyz.funtimes909.serverseekerv2_discord_bot.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.Records.Player;
import xyz.funtimes909.serverseekerv2_discord_bot.Records.Server;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServerEmbedBuilder {
    private static String address;
    private static short port;
    private static String description;
    private static String version;
    private static int protocol;
    private static String country;
    private static String asn;
    private static String hostname;
    private static String organization;
    private static long firstseen;
    private static long lastseen;
    private static int timesSeen;
    private static Boolean whitelist;
    private static Boolean enforceSecure;
    private static Boolean cracked;
    private static Boolean preventsReports;
    private static int fmlNetworkVersion;
    private static List<Player> players = new ArrayList<>();
    private static boolean rescanned;

    public ServerEmbedBuilder(ResultSet results, boolean rescan) {
        players.clear();
        try (results) {
            results.next();
            address = results.getString("address");
            port = results.getShort("port");
            description = results.getString("motd");
            version = results.getString("version");
            protocol = results.getInt("protocol");
            country = results.getString("country");
            asn = results.getString("asn");
            hostname = results.getString("reversedns");
            organization = results.getString("organization");
            firstseen = results.getLong("firstseen");
            lastseen = results.getLong("lastseen");
            timesSeen = results.getInt("timesSeen");
            whitelist = results.getBoolean("whitelist");
            enforceSecure = results.getBoolean("enforceSecure");
            cracked = results.getBoolean("cracked");
            preventsReports = results.getBoolean("preventsReports");
            fmlNetworkVersion = results.getInt("fmlNetworkVersion");
            if (results.getString("playername") != null && results.getString("playeruuid") != null) {
                players.add(new Player(results.getString("playername"), results.getString("playeruuid"), results.getLong("lastseen")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        rescanned = rescan;
    }

    public ServerEmbedBuilder(Server server, boolean rescan) {
        players.clear();
        address = server.getAddress();
        port = server.getPort();
        description = server.getMotd();
        version = server.getVersion();
        protocol = server.getProtocol();
        country = server.getCountry();
        asn = server.getAsn();
        hostname = server.getReverseDns();
        organization = server.getOrganization();
        lastseen = server.getTimestamp();
        timesSeen = server.getTimesSeen();
        whitelist = server.getWhitelist();
        enforceSecure = server.getEnforceSecure();
        cracked = server.getCracked();
        preventsReports = server.getPreventsReports();
        players = server.getPlayers();
        rescanned = rescan;

        if (server.getFmlNetworkVersion() == null) fmlNetworkVersion = 0;
        else fmlNetworkVersion = server.getFmlNetworkVersion();
    }

    public MessageEmbed build() {
        StringBuilder miscInfo = new StringBuilder();
        StringBuilder addressInfo = new StringBuilder();
        StringBuilder playerInfo = new StringBuilder();
        String title = rescanned ? address + ":" + port + " (Rescanned)" : address + ":" + port;

        // Get first seen time and the amount of times this server has been seen
        if (rescanned) {
            try (Connection conn = DatabaseConnectionPool.getConnection()) {
                PreparedStatement statement = conn.prepareStatement("SELECT firstseen, timesseen FROM servers WHERE address = ? AND port = ?");
                statement.setString(1, address);
                statement.setShort(2, port);

                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                firstseen = resultSet.getLong("firstseen");
                timesSeen = resultSet.getInt("timesseen");
                lastseen = System.currentTimeMillis() / 1000;
            } catch (SQLException e) {
                Main.logger.error("Failed to execute rescan query!", e);
            }
        }

        // Miscellaneous info
        miscInfo.append("Whitelist: **").append(whitelist != null ? whitelist + "**\n" : "N/A**\n");
        miscInfo.append("Cracked: **").append(cracked != null ? cracked + "**\n" : "N/A**\n");
        miscInfo.append("Prevents Chat Reports: **").append(preventsReports != null ? preventsReports + "**\n" : "N/A**\n");
        miscInfo.append("Enforces Secure Chat: **").append(enforceSecure != null ? enforceSecure + "**\n" : "N/A**\n");
        miscInfo.append("Forge: **").append(fmlNetworkVersion != 0 ? "true**\n" : "false**\n");

        // Address information
        addressInfo.append("ASN: **").append(asn != null ? asn + "**\n" : "N/A**\n");
        addressInfo.append("Hostname: **").append(hostname != null ? hostname + "**\n" : "N/A**\n");
        addressInfo.append("Organization: **").append(organization != null ? organization + "**\n" : "N/A**\n");

        if (players.isEmpty()) {
            playerInfo.append("No players found!");
        } else {
            for (Player player : players) {
                playerInfo.append("\n").append(player.name()).append("\n").append(player.uuid()).append("\n");
            }
        }

        MessageEmbed.Field countryField = new MessageEmbed.Field("** -- __Country__ -- **", country != null ? ":flag_" + country.toLowerCase() + ":" + country : ":x: No country information available", false);
        MessageEmbed.Field descriptionField = new MessageEmbed.Field("** -- __Description__ -- **", description != null ? "```" + description + "```" : "```No description found!```", false);
        MessageEmbed.Field miscField = new MessageEmbed.Field("** -- __Miscellaneous__ -- **", miscInfo.toString(), false);
        MessageEmbed.Field playerField = new MessageEmbed.Field("** -- __Players__ -- **",  "```\n" + playerInfo + "```", false);
        MessageEmbed.Field addressField = new MessageEmbed.Field("** -- __Address Information__ -- **", addressInfo.toString(), false);

        // Build server information embed
        return new EmbedBuilder()
                .setColor(new Color(0, 255, 0))
                .setAuthor("ServerSeekerV2", "https://funtimes909.xyz/assets/images/serverseekerv2-icon-cropped.png")
                .setTitle(title)
                .setThumbnail("https://funtimes909.xyz/avatar-gif")
                .addField("** -- __Version__ -- **", version + " (" + protocol + ")", false)
                .addField(descriptionField)
                .addField(countryField)
                .addField("** -- __First Seen__ -- **", "<t:" + firstseen + ":R>", false)
                .addField("** -- __Last Seen__ -- **", "<t:" + lastseen + ":R>", false)
                .addField(miscField)
                .addField(playerField)
                .addField(addressField)
                .build();
    }
}

