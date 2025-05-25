package xyz.funtimes909.serverseekerv2_discord_bot.builders;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import xyz.funtimes909.serverseekerv2_discord_bot.types.Mod;
import xyz.funtimes909.serverseekerv2_discord_bot.types.Player;
import xyz.funtimes909.serverseekerv2_discord_bot.types.Server;
import xyz.funtimes909.serverseekerv2_discord_bot.types.ServerType;
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
    private final ServerType software;
    private String description;
    private final String version;
    private final String icon;
    private final Integer protocol;
    private final long firstSeen;
    private final long lastSeen;
    private final Boolean enforceSecure;
    private final Boolean preventsReports;
    private final Integer onlinePlayers;
    private final Integer maxPlayers;
    private final List<Player> players;
    private final List<Mod> mods;

    public ServerEmbedBuilder(Server server) {
        address = server.getAddress();
        port = server.getPort();
        software = server.getServerType();
        description = server.getMotd();
        version = server.getVersion();
        icon = server.getIcon();
        protocol = server.getProtocol();
        firstSeen = server.getFirstSeen();
        lastSeen = server.getLastSeen();
        enforceSecure = server.getEnforceSecure();
        preventsReports = server.getPreventsReports();
        onlinePlayers = server.getOnlinePlayers();
        maxPlayers = server.getMaxPlayers();
        players = server.getPlayers();
        mods = server.getMods();
    }

    public MessageCreateData build(boolean ping) throws IOException {
        StringBuilder modInfo = new StringBuilder();

        // Handle server software and version field
        if (description != null && description.contains("§")) {
            description = PingUtils.parseMOTD(description);
        }

        // Version field
        String versionField = String.format("Version: **%s** **(%d)**\nSoftware: **%s**", version, protocol, software);

        // Timestamp field
        String timestampField = String.format("First Seen: <t:%d:R>\nLast Seen: <t:%d:R>", firstSeen, lastSeen);

        // Misc field
        String miscField = String.format("Prevents Chat Reports: **%s**\nEnforces Secure Chat: **%s**",
                preventsReports == null ? "N/A" : preventsReports,
                enforceSecure == null ? "N/A" : enforceSecure
        );

        // Player field
        StringBuilder playersFieldFormat = handlePlayers();
        String playerField = String.format(playersFieldFormat.toString(), onlinePlayers, maxPlayers);

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

        // If an image is present use that, use the default image if not
        byte[] image;
        if (icon != null && !icon.isBlank()) {
            image = Base64.getDecoder().decode(icon.split(",")[1]);
        } else {
            image = Files.readAllBytes(new File("default_icon.png").toPath());
        }

        // Send icon to discord and use that attachment as the icon
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(new Color(0, 255, 0))
                .setAuthor("ServerSeekerV2", "https://discord.gg/UA5kyprunc", "https://cdn.discordapp.com/app-icons/1375333922765930556/bcc3069c7e9fdb44107faeb74477127d.png?size=256")
                .setFooter("Made with <3 by Funtimes909", "https://funtimes909.xyz/assets/images/floppa.png")
                .setThumbnail("attachment://icon.png") // The icon file
                .setTitle(address + ":" + port)
                .addField("** -- __Version__ -- **", versionField, false)
                .addField("** -- __Description__ -- **", description != null ? "```ansi\n" + description + "```" : "```No description found!```", false);

        // Add all the fields to the embed
        if (!ping) {
            embed.addField("** -- __Timestamps__ -- **", timestampField, false);
        }

        embed.addField("** -- __Miscellaneous__ -- **", miscField, false);
        embed.addField("** -- __Players__ -- **",  playerField, false);
        if (mods != null && !mods.isEmpty()) embed.addField("** -- __Mods__ -- **",  modInfo.toString(), false);

        return new MessageCreateBuilder()
                .setFiles(FileUpload.fromData(image, "icon.png"))
                .setEmbeds(embed.build())
                .setContent(":white_check_mark: Success!")
                .build();
    }

    private StringBuilder handlePlayers() {
        StringBuilder playersFieldFormat = new StringBuilder("Players: **%d/%d**\n");
        if (players == null || players.isEmpty()) {
            playersFieldFormat.append("```No players found!```");
        } else {
            playersFieldFormat.append("```\n");

            for (int i = 0; i < Integer.max(players.size(), 5); i++) {
                playersFieldFormat.append(String.format("\n%s\n%s\n", players.get(i).name(), players.get(i).uuid()));
            }

            if (players.size() > 5) playersFieldFormat.append(String.format("\n%d Players not shown...\n", players.size() - 5));

            playersFieldFormat.append("```");
        }
        return playersFieldFormat;
    }
}