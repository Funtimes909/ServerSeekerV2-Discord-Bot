package xyz.funtimes909.serverseekerv2_discord_bot.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.Records.Server;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public ServerEmbedBuilder(ResultSet results, boolean rescan) {
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
        } catch (SQLException e) {
            Main.logger.warn(e.getMessage());
        }
    }

    public ServerEmbedBuilder(Server server, boolean rescan) {
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

        if (server.getFmlNetworkVersion() == null) fmlNetworkVersion = 0;
        else fmlNetworkVersion = server.getFmlNetworkVersion();
    }

    public MessageEmbed build() {
        StringBuilder miscInfo = new StringBuilder();
        StringBuilder addressInfo = new StringBuilder();

        // Misc information
        if (whitelist != null) miscInfo.append("Whitelist: **").append(whitelist).append("**\n");
        if (cracked != null) miscInfo.append("Cracked: **").append(cracked).append("**\n");
        if (preventsReports != null) miscInfo.append("Prevents Chat Reports: **").append(preventsReports).append("**\n");
        if (enforceSecure!= null) miscInfo.append("Enforces Secure Chat: **").append(enforceSecure).append("**\n");
        if (fmlNetworkVersion != 0) miscInfo.append("Forge: **True** \n");

        // Address information
        if (asn != null) {
            addressInfo.append("ASN: **").append(asn).append("** \n");
        } else {
            addressInfo.append("ASN: **N/A** \n");
        }

        if (hostname != null) {
            addressInfo.append("Hostname: **").append(hostname).append("** \n");
        } else {
            addressInfo.append("Hostname: **N/A** \n");
        }

        if (organization != null) {
            addressInfo.append("Organization: **").append(organization).append("** \n");
        } else {
            addressInfo.append("Organization: **N/A** \n");
        }

        MessageEmbed.Field miscField = new MessageEmbed.Field("** -- __Miscellaneous__ -- **", miscInfo.toString(), false);
        MessageEmbed.Field addressField = new MessageEmbed.Field("** -- __Address Information__ -- **", addressInfo.toString(), false);
        MessageEmbed.Field countryField;

        if (country != null) {
            countryField = new MessageEmbed.Field("** -- __Country__ -- **", ":flag_" + country.toLowerCase() + ": " + country, false);
        } else {
            countryField = new MessageEmbed.Field("** -- __Country__ -- **", ":x: No country information available", false);
        }

        // Build server information embed
        return new EmbedBuilder()
                .setColor(new Color(0, 255, 0))
                .setAuthor("ServerSeekerV2")
                .setThumbnail("https://funtimes909.xyz/avatar-gif")
                .setTitle(address + ":" + port)
                .addField("** -- __Version__ -- **", version + " (" + protocol + ")", false)
                .addField("** -- __Description__ -- **", "```" + description + "```", false)
                .addField(countryField)
                .addField("** -- __First Seen__ -- **", "<t:" + firstseen + ">", false)
                .addField("** -- __Last Seen__ -- **", "<t:" + System.currentTimeMillis() / 1000 + ">", false)
                .addField(miscField)
                .addField(addressField)
                .build();
    }
}

