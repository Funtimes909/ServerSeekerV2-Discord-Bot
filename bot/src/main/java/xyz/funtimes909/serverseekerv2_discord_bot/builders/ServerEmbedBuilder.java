package xyz.funtimes909.serverseekerv2_discord_bot.builders;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import xyz.funtimes909.serverseekerv2_core.records.Mod;
import xyz.funtimes909.serverseekerv2_core.records.Player;
import xyz.funtimes909.serverseekerv2_core.records.Server;
import xyz.funtimes909.serverseekerv2_core.types.ServerType;
import xyz.funtimes909.serverseekerv2_core.util.HTTPUtils;
import xyz.funtimes909.serverseekerv2_discord_bot.util.PingUtils;

import java.awt.*;
import java.util.List;

public class ServerEmbedBuilder {
    private final String address;
    private final short port;
    private final ServerType type;
    private String description;
    private final String version;
    private final Integer protocol;
    private String country;
    private String asn;
    private String hostname;
    private String organization;
    private long firstseen;
    private final long lastseen;
    private final int timesSeen;
    private final Boolean whitelist;
    private final Boolean enforceSecure;
    private final Boolean cracked;
    private final Boolean preventsReports;
    private final Integer maxPlayers;
    private final Integer fmlNetworkVersion;
    private final List<Player> players;
    private final List<Mod> mods;

    public ServerEmbedBuilder(Server server) {
        address = server.getAddress();
        port = server.getPort();
        type = server.getServerType();
        description = server.getMotd();
        version = server.getVersion();
        protocol = server.getProtocol();
        country = server.getCountry();
        asn = server.getAsn();
        hostname = server.getReverseDns();
        organization = server.getOrganization();
        firstseen = server.getFirstSeen();
        lastseen = server.getLastSeen();
        timesSeen = server.getTimesSeen();
        whitelist = server.getWhitelist();
        enforceSecure = server.getEnforceSecure();
        cracked = server.getCracked();
        preventsReports = server.getPreventsReports();
        maxPlayers = server.getMaxPlayers();
        fmlNetworkVersion = server.getFmlNetworkVersion();
        players = server.getPlayers();
        mods = server.getMods();
    }

    public MessageEmbed build(boolean ping) {
        StringBuilder miscInfo = new StringBuilder();
        StringBuilder addressInfo = new StringBuilder();
        StringBuilder playerInfo = new StringBuilder();
        StringBuilder softwareInfo = new StringBuilder();
        StringBuilder modInfo = new StringBuilder();

        // Miscellaneous info
        if (!ping) miscInfo.append("Times Seen: **").append(timesSeen).append("**\n");
        miscInfo.append("Whitelist: **").append(whitelist != null ? whitelist + "**\n" : "N/A**\n");
        miscInfo.append("Cracked: **").append(cracked != null ? cracked + "**\n" : "N/A**\n");
        miscInfo.append("Prevents Chat Reports: **").append(preventsReports != null ? preventsReports + "**\n" : "N/A**\n");
        miscInfo.append("Enforces Secure Chat: **").append(enforceSecure != null ? enforceSecure + "**\n" : "N/A**\n");

        // Address information
        if (ping) {
            String primaryResponse = HTTPUtils.run(address);
            if (primaryResponse != null) {
                JsonObject parsedPrimaryResponse = JsonParser.parseString(primaryResponse).getAsJsonObject();
                if (parsedPrimaryResponse.has("reverse")) hostname = parsedPrimaryResponse.get("reverse").getAsString();
                if (parsedPrimaryResponse.has("countryCode")) country = parsedPrimaryResponse.get("countryCode").getAsString();
                if (parsedPrimaryResponse.has("org")) organization = parsedPrimaryResponse.get("org").getAsString();
                if (parsedPrimaryResponse.has("as")) asn = parsedPrimaryResponse.get("as").getAsString();
            }
        }

        if (description != null && description.contains("§")) {
            System.out.println(description);
            description = PingUtils.parseMOTD(description);
        }

        addressInfo.append("ASN: **").append(asn != null ? asn + "**\n" : "N/A**\n");
        addressInfo.append("Hostname: **").append(hostname != null ? hostname + "**\n" : "N/A**\n");
        addressInfo.append("Organization: **").append(organization != null ? organization + "**\n" : "N/A**\n");

        if (firstseen == 0) firstseen = System.currentTimeMillis() / 1000;

        // Create field for players
        playerInfo.append("Players: **").append(players != null ? players.size() + "/" + maxPlayers : 0).append("**\n");
        if (players == null || players.isEmpty()) {
            playerInfo.append("```No players found!```");
        } else {
            playerInfo.append("```\n");

            int count = 0;
            for (Player player : players) {
                playerInfo.append("\n").append(player.name()).append("\n").append(player.uuid()).append("\n");
                count++;
                if (count == 5) break;
            }

            if (players.size() > 5) playerInfo.append("\n").append(players.size() - 5).append(" Players not shown...\n");
            playerInfo.append("```");
        }

        if (mods != null && !mods.isEmpty()) {
            modInfo.append("Mods: **").append(mods.size()).append("**\n");
            modInfo.append("```\n");
            int count = 0;

            for (Mod mod : mods) {
                if (mod.modmarker().startsWith("OHNOES")) continue;
                modInfo.append("\n").append(mod.modid()).append("\n").append(mod.modmarker()).append("\n");
                count++;
                if (count == 5) break;
            }

            if (mods.size() > 5) modInfo.append("\n").append(mods.size() - 5).append(" Mods not shown...\n");
            modInfo.append("```");
        }

        softwareInfo.append("Server Type: **").append(type != null ? type.name() + "**\n" : "N/A**\n");
        if (fmlNetworkVersion != null && fmlNetworkVersion != 0) {
            softwareInfo.append("Forge: **").append("true**\n");
            softwareInfo.append("Forge Version: **").append(fmlNetworkVersion).append("**\n");
        }

        // Build server information embed
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(new Color(0, 255, 0))
                .setAuthor("ServerSeekerV2", "https://cdn.discordapp.com/app-icons/1300318661168594975/cb3825c45b033454cf027a878e96196c.png")
                .setThumbnail("https://funtimes909.xyz/avatar-gif")
                .setTitle(address + ":" + port)
                .addField("** -- __Version__ -- **", version + " (" + protocol + ")", false)
                .addField("** -- __Description__ -- **", description != null ? "```ansi\n" + description + "```" : "```No description found!```", false);

        if (!ping) {
            embed.addField("** -- __First Seen__ -- **", "<t:" + firstseen + ":R>", false);
            embed.addField("** -- __Last Seen__ -- **", "<t:" + lastseen + ":R>", false);
        }

        embed.addField("** -- __Country__ -- **", country != null ? ":flag_" + country.toLowerCase() + ": " + country : ":x: No Country Information", false);
        embed.addField("** -- __Server Software__ -- **", softwareInfo.toString(), false);
        embed.addField("** -- __Miscellaneous__ -- **", miscInfo.toString(), false);
        embed.addField("** -- __Players__ -- **",  playerInfo.toString(), false);
        if (mods != null && !mods.isEmpty()) embed.addField("** -- __Mods__ -- **",  modInfo.toString(), false);
        embed.addField("** -- __Address Information__ -- **", addressInfo.toString(), false);
        return embed.build();
    }
}