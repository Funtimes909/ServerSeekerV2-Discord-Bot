package xyz.funtimes909.serverseekerv2_discord_bot.builders;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import xyz.funtimes909.serverseekerv2_discord_bot.Records.Player;
import xyz.funtimes909.serverseekerv2_discord_bot.Records.Server;
import xyz.funtimes909.serverseekerv2_discord_bot.util.IpLookup;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServerEmbedBuilder {
    private String address;
    private short port;
    private String description;
    private String version;
    private Integer protocol;
    private String country;
    private String asn;
    private String hostname;
    private String organization;
    private long firstseen;
    private long lastseen;
    private int timesSeen;
    private Boolean whitelist;
    private Boolean enforceSecure;
    private Boolean cracked;
    private Boolean preventsReports;
    private Integer maxPlayers;
    private Integer fmlNetworkVersion;
    private List<Player> players = new ArrayList<>();

    public ServerEmbedBuilder(ResultSet results) {
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
            maxPlayers = results.getInt("maxPlayers");
            fmlNetworkVersion = results.getInt("fmlNetworkVersion");
            if (results.getString("playername") != null && results.getString("playeruuid") != null) {
                players.add(new Player(results.getString("playername"), results.getString("playeruuid"), results.getLong("lastseen")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ServerEmbedBuilder(Server server) {
        address = server.getAddress();
        port = server.getPort();
        description = server.getMotd();
        version = server.getVersion();
        protocol = server.getProtocol();
        country = server.getCountry();
        asn = server.getAsn();
        hostname = server.getReverseDns();
        organization = server.getOrganization();
        firstseen = server.getFirstseen();
        lastseen = server.getLastseen();
        timesSeen = server.getTimesSeen();
        whitelist = server.getWhitelist();
        enforceSecure = server.getEnforceSecure();
        cracked = server.getCracked();
        preventsReports = server.getPreventsReports();
        maxPlayers = server.getMaxPlayers();
        players = server.getPlayers();
    }

    public MessageEmbed build(boolean ping) {
        StringBuilder miscInfo = new StringBuilder();
        StringBuilder addressInfo = new StringBuilder();
        StringBuilder playerInfo = new StringBuilder();

        // Miscellaneous info
        if (!ping) miscInfo.append("Times Seen: **").append(timesSeen).append("**\n");
        miscInfo.append("Whitelist: **").append(whitelist != null ? whitelist + "**\n" : "N/A**\n");
        miscInfo.append("Cracked: **").append(cracked != null ? cracked + "**\n" : "N/A**\n");
        miscInfo.append("Prevents Chat Reports: **").append(preventsReports != null ? preventsReports + "**\n" : "N/A**\n");
        miscInfo.append("Enforces Secure Chat: **").append(enforceSecure != null ? enforceSecure + "**\n" : "N/A**\n");
        miscInfo.append("Forge: **").append(fmlNetworkVersion != null ? "true**\n" : "false**\n");

        // Address information

        if (ping) {
            String primaryResponse = IpLookup.run(address);
            if (primaryResponse != null) {
                JsonObject parsedPrimaryResponse = JsonParser.parseString(primaryResponse).getAsJsonObject();
                if (parsedPrimaryResponse.has("reverse")) hostname = parsedPrimaryResponse.get("reverse").getAsString();
                if (parsedPrimaryResponse.has("countryCode")) country = parsedPrimaryResponse.get("countryCode").getAsString();
                if (parsedPrimaryResponse.has("org")) organization = parsedPrimaryResponse.get("org").getAsString();
                if (parsedPrimaryResponse.has("as")) asn = parsedPrimaryResponse.get("as").getAsString();
            }
        }

        addressInfo.append("ASN: **").append(asn != null ? asn + "**\n" : "N/A**\n");
        addressInfo.append("Organization: **").append(organization != null ? organization + "**\n" : "N/A**\n");

        if (hostname == null || hostname.isBlank()) {
            addressInfo.append("Hostname: **").append("N/A**\n");
        } else {
            addressInfo.append("Hostname: **").append(hostname + "**\n");
        }

        if (firstseen == 0) firstseen = System.currentTimeMillis() / 1000;

        playerInfo.append("Players: **").append(players != null ? players.size() : 0).append("/").append(maxPlayers).append("**\n");
        if (players == null || players.isEmpty()) {
            playerInfo.append("```No players found!```");
        } else {
            playerInfo.append("```\n");
            for (Player player : players) {
                playerInfo.append("\n").append(player.name()).append("\n").append(player.uuid()).append("\n");
            }
            playerInfo.append("```");
        }

        // Build server information embed
        return new EmbedBuilder()
                .setColor(new Color(0, 255, 0))
                .setAuthor("ServerSeekerV2", "https://funtimes909.xyz/assets/images/serverseekerv2-icon-cropped.png")
                .setThumbnail("https://funtimes909.xyz/avatar-gif")
                .setTitle(address + ":" + port)
                .addField("** -- __Version__ -- **", version + " (" + protocol + ")", false)
                .addField("** -- __Description__ -- **", description != null ? "```" + description + "```" : "```No description found!```", false)
                .addField("** -- __Country__ -- **", country != null ? ":flag_" + country.toLowerCase() + ":" + country : ":x: No Country Information", false)
                .addField("** -- __First Seen__ -- **", "<t:" + firstseen + ":R>", false)
                .addField("** -- __Last Seen__ -- **", "<t:" + lastseen + ":R>", false)
                .addField("** -- __Miscellaneous__ -- **", miscInfo.toString(), false)
                .addField("** -- __Players__ -- **",  playerInfo.toString(), false)
                .addField("** -- __Address Information__ -- **", addressInfo.toString(), false)
                .build();
    }
}
