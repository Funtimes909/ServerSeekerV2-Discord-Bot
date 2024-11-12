package xyz.funtimes909.serverseekerv2_discord_bot.builders;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import xyz.funtimes909.serverseekerv2_discord_bot.Records.Player;
import xyz.funtimes909.serverseekerv2_discord_bot.Records.Server;

import java.awt.*;
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

    public ServerEmbedBuilder(ResultSet results) {
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
    }

    public ServerEmbedBuilder(Server server) {
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

        if (server.getFmlNetworkVersion() == null) fmlNetworkVersion = 0;
        else fmlNetworkVersion = server.getFmlNetworkVersion();
    }

    public MessageEmbed build() {
        StringBuilder miscInfo = new StringBuilder();
        StringBuilder addressInfo = new StringBuilder();
        StringBuilder playerInfo = new StringBuilder();

        // Miscellaneous info
        miscInfo.append("Times Seen: **").append(timesSeen).append("**\n");
        miscInfo.append("Whitelist: **").append(whitelist != null ? whitelist + "**\n" : "N/A**\n");
        miscInfo.append("Cracked: **").append(cracked != null ? cracked + "**\n" : "N/A**\n");
        miscInfo.append("Prevents Chat Reports: **").append(preventsReports != null ? preventsReports + "**\n" : "N/A**\n");
        miscInfo.append("Enforces Secure Chat: **").append(enforceSecure != null ? enforceSecure + "**\n" : "N/A**\n");
        miscInfo.append("Forge: **").append(fmlNetworkVersion != 0 ? "true**\n" : "false**\n");

        // Address information
        addressInfo.append("ASN: **").append(asn != null ? asn + "**\n" : "N/A**\n");
        addressInfo.append("Hostname: **").append(hostname != null ? hostname + "**\n" : "N/A**\n");
        addressInfo.append("Organization: **").append(organization != null ? organization + "**\n" : "N/A**\n");

        if (firstseen == 0) firstseen = System.currentTimeMillis() / 1000;

        if (players.isEmpty()) {
            playerInfo.append("No players found!");
        } else {
            for (Player player : players) {
                playerInfo.append("\n").append(player.name()).append("\n").append(player.uuid()).append("\n");
            }
        }

        // Build server information embed
        return new EmbedBuilder()
                .setColor(new Color(0, 255, 0))
                .setAuthor("ServerSeekerV2", "https://funtimes909.xyz/assets/images/serverseekerv2-icon-cropped.png")
                .setTitle(address + ":" + port)
                .setThumbnail("https://funtimes909.xyz/avatar-gif")
                .addField("** -- __Version__ -- **", version + " (" + protocol + ")", false)
                .addField("** -- __Description__ -- **", description != null ? "```" + description + "```" : "```No description found!```", false)
                .addField("** -- __Country__ -- **", country != null ? ":flag_" + country.toLowerCase() + ":" + country : ":x: No country information available", false)
                .addField("** -- __First Seen__ -- **", "<t:" + firstseen + ":R>", false)
                .addField("** -- __Last Seen__ -- **", "<t:" + lastseen + ":R>", false)
                .addField("** -- __Miscellaneous__ -- **", miscInfo.toString(), false)
                .addField("** -- __Players__ -- **",  "```\n" + playerInfo + "```", false)
                .addField("** -- __Address Information__ -- **", addressInfo.toString(), false)
                .build();
    }
}

