package xyz.funtimes909.serverseekerv2_discord_bot.builders;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import xyz.funtimes909.serverseekerv2_core.records.Mod;
import xyz.funtimes909.serverseekerv2_core.records.Player;
import xyz.funtimes909.serverseekerv2_core.records.Server;
import xyz.funtimes909.serverseekerv2_core.types.ServerType;
import xyz.funtimes909.serverseekerv2_core.util.HTTPUtils;
import xyz.funtimes909.serverseekerv2_discord_bot.util.PingUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

public class ServerEmbedBuilder {
    private final String address;
    private final short port;
    private final ServerType type;
    private String description;
    private final String version;
    private final String icon;
    private final Integer protocol;
    private final String country;
    private final String asn;
    private final String hostname;
    private final String organization;
    private final long firstseen;
    private final long lastseen;
    private final Boolean whitelist;
    private final Boolean enforceSecure;
    private final Boolean cracked;
    private final Boolean preventsReports;
    private final Integer maxPlayers;
    private final List<Player> players;
    private final List<Mod> mods;

    public ServerEmbedBuilder(Server server) {
        address = server.getAddress();
        port = server.getPort();
        type = server.getServerType();
        description = server.getMotd();
        version = server.getVersion();
        icon = server.getIcon();
        protocol = server.getProtocol();
        country = server.getCountry();
        asn = server.getAsn();
        hostname = server.getReverseDns();
        organization = server.getOrganization();
        firstseen = server.getFirstSeen();
        lastseen = server.getLastSeen();
        whitelist = server.getWhitelist();
        enforceSecure = server.getEnforceSecure();
        cracked = server.getCracked();
        preventsReports = server.getPreventsReports();
        maxPlayers = server.getMaxPlayers();
        players = server.getPlayers();
        mods = server.getMods();
    }

    public MessageCreateData build(boolean ping) throws IOException {
        StringBuilder miscInfo = new StringBuilder();
        StringBuilder addressInfo = new StringBuilder();
        StringBuilder playerInfo = new StringBuilder();
        StringBuilder modInfo = new StringBuilder();
        StringBuilder versionInfo = new StringBuilder();

        // Handle server software and version field
        if (description != null && description.contains("§")) {
            description = PingUtils.parseMOTD(description);
        }

        if (Character.isDigit(version.charAt(0)) && type != null) {
            versionInfo.append(type.name().charAt(0))
                    .append(type.name().substring(1).toLowerCase())
                    .append(" ")
                    .append(version)
                    .append(" (")
                    .append(protocol)
                    .append(")");
        } else {
            versionInfo.append(version)
                    .append(" (")
                    .append(protocol)
                    .append(")");
        }

        // Handle timestamp field
        String timestamps =
                "First Seen: <t:" + firstseen + ":R>\n" +
                "Last Seen: <t:" + lastseen + ":R>";

        // If this is a ping, request address information, else use address information from the database
        if (ping) {
            String response = HTTPUtils.run(address);
            if (response != null) {
                JsonObject parsed = JsonParser.parseString(response).getAsJsonObject();

                addressInfo.append(parsed.get("countryCode").getAsString().isBlank() ?
                        "Country: **N/A** \n" :
                        "Country: **" + parsed.get("countryCode").getAsString() + " :flag_" + country.toLowerCase() + ":**\n");

                addressInfo.append(parsed.get("reverse").getAsString().isBlank() ?
                        "Hostname: **N/A** \n" :
                        "Hostname: **" + parsed.get("reverse").getAsString() + "**\n");

                addressInfo.append(parsed.get("org").getAsString().isBlank() ?
                        "Organization: **N/A** \n" :
                        "Organization: **" + parsed.get("org").getAsString() + "**\n");

                addressInfo.append(parsed.get("as").getAsString().isBlank() ?
                        "ASN: **N/A**" :
                        "ASN: **" + parsed.get("as").getAsString() + "**");
            }
        } else {
            addressInfo.append("Country: **").append(country != null ?
                    country + " :flag_" + country.toLowerCase() + ":" :
                    "N/A").append("**\n");
            addressInfo.append("Hostname: **").append(hostname != null ? hostname + "**\n" : "N/A**\n");
            addressInfo.append("Organization: **").append(organization != null ? organization + "**\n" : "N/A**\n");
            addressInfo.append("ASN: **").append(asn != null ? asn + "**" : "N/A**");
        }

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

        // Handle field for mods if they exist
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

        // Miscellaneous info
        miscInfo.append("Whitelist: **").append(whitelist != null ? whitelist + "**\n" : "N/A**\n");
        miscInfo.append("Cracked: **").append(cracked != null ? cracked + "**\n" : "N/A**\n");
        miscInfo.append("Prevents Chat Reports: **").append(preventsReports != null ? preventsReports + "**\n" : "N/A**\n");
        miscInfo.append("Enforces Secure Chat: **").append(enforceSecure != null ? enforceSecure + "**\n" : "N/A**\n");

        byte[] image;
        if (icon != null && !icon.isBlank()) {
            image = Base64.getDecoder().decode(icon.split(",")[1]);
        } else {
            image = Files.readAllBytes(new File("default_icon.png").toPath());
        }

        // Send icon to discord and use that attachment as the icon
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(new Color(0, 255, 0))
                .setAuthor("ServerSeekerV2", "https://cdn.discordapp.com/avatars/1300318661168594975/1222800bc7003f89c849e55d274b2c52?size=256")
                .setThumbnail("attachment://icon.png") // The icon file
                .setTitle(address + ":" + port)
                .addField("** -- __Version__ -- **", versionInfo.toString(), false)
                .addField("** -- __Description__ -- **", description != null ? "```ansi\n" + description + "```" : "```No description found!```", false);

        // Add all the fields to the embed
        if (!ping) {
            embed.addField("** -- __Timestamps__ -- **", timestamps, false);
        }

        embed.addField("** -- __Miscellaneous__ -- **", miscInfo.toString(), false);
        embed.addField("** -- __Players__ -- **",  playerInfo.toString(), false);
        if (mods != null && !mods.isEmpty()) embed.addField("** -- __Mods__ -- **",  modInfo.toString(), false);
        embed.addField("** -- __Address Information__ -- **", addressInfo.toString(), false);

        return new MessageCreateBuilder()
                .setFiles(FileUpload.fromData(image, "icon.png"))
                .setEmbeds(embed.build())
                .setContent(":white_check_mark: Success!")
                .build();
    }
}