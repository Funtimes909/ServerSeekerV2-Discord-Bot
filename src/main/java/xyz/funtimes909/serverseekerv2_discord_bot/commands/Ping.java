package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.builders.ServerEmbedBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.records.Mod;
import xyz.funtimes909.serverseekerv2_discord_bot.records.Player;
import xyz.funtimes909.serverseekerv2_discord_bot.records.Server;
import xyz.funtimes909.serverseekerv2_discord_bot.util.Database;
import xyz.funtimes909.serverseekerv2_discord_bot.util.PingUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class Ping {
    public static void ping(SlashCommandInteractionEvent event) {
        if (event.getOption("address").getAsString().equals("localhost") || event.getOption("address").getAsString().equals("0.0.0.0") || event.getOption("address").getAsString().startsWith("127")) {
            event.getHook().sendMessage("You can't ping this address!").queue();
            return;
        }

        short port = 25565;
        if (event.getOption("port") != null) port = (short) event.getOption("port").getAsInt();

        PingUtils ping = new PingUtils(event.getOption("address").getAsString(), port);
        Server server = ping.parse();

        if (server == null) {
            event.getHook().sendMessage("Server did not connect!").queue();
            return;
        }

        updateServer(server);
        ServerEmbedBuilder embedBuilder = new ServerEmbedBuilder(server);
        MessageEmbed embed = embedBuilder.build(true);

        if (embed == null) {
            event.getHook().sendMessage("Something went wrong running this command!").queue();
            return;
        }

        event.getHook().sendMessageEmbeds(embed).queue();
    }

    private static void updateServer(Server server) {
        try (Connection conn = Database.getConnection()) {
            String address = server.getAddress();
            short port = server.getPort();
            long timestamp = server.getLastSeen();
            String country = server.getCountry();
            String asn = server.getAsn();
            String reverseDns = server.getReverseDns();
            String organization = server.getOrganization();
            String version = server.getVersion();
            Integer protocol = server.getProtocol();
            Integer fmlNetworkVersion = server.getFmlNetworkVersion();
            String motd = server.getMotd();
            String icon = server.getIcon();
            int timesSeen = server.getTimesSeen();
            Boolean preventsReports = server.getPreventsReports();
            Boolean enforceSecure = server.getEnforceSecure();
            Boolean whitelist = server.getWhitelist();
            Boolean cracked = server.getCracked();
            Integer maxPlayers = server.getMaxPlayers();
            Integer onlinePlayers = server.getOnlinePlayers();
            List<Player> players = server.getPlayers();
            List<Mod> mods = server.getMods();

            // Attempt to insert new server, if address and port already exist, update relevant information
            PreparedStatement insertServer = conn.prepareStatement("INSERT INTO Servers " +
                    "(Address," +
                    "Port," +
                    "FirstSeen," +
                    "LastSeen," +
                    "Country," +
                    "Asn," +
                    "ReverseDNS," +
                    "Organization," +
                    "Version," +
                    "Protocol," +
                    "FmlNetworkVersion," +
                    "Motd," +
                    "Icon," +
                    "TimesSeen," +
                    "PreventsReports," +
                    "EnforceSecure," +
                    "Whitelist," +
                    "Cracked," +
                    "MaxPlayers," +
                    "OnlinePlayers)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" +
                    "ON CONFLICT (Address, Port) DO UPDATE SET " +
                    "LastSeen = EXCLUDED.LastSeen," +
                    "Country = EXCLUDED.Country," +
                    "Asn = EXCLUDED.Asn," +
                    "ReverseDNS = EXCLUDED.ReverseDNS," +
                    "Organization = EXCLUDED.Organization," +
                    "Version = EXCLUDED.Version," +
                    "Protocol = EXCLUDED.Protocol," +
                    "FmlNetworkVersion = EXCLUDED.FmlNetworkVersion," +
                    "Motd = EXCLUDED.Motd," +
                    "Icon = EXCLUDED.Icon," +
                    "TimesSeen = Servers.TimesSeen + 1," +
                    "PreventsReports = EXCLUDED.PreventsReports," +
                    "EnforceSecure = EXCLUDED.EnforceSecure," +
                    "Whitelist = EXCLUDED.Whitelist," +
                    "Cracked = EXCLUDED.Cracked," +
                    "MaxPlayers = EXCLUDED.MaxPlayers," +
                    "OnlinePlayers = EXCLUDED.OnlinePlayers");

            // Set most values as objects to insert a null if value doesn't exist
            insertServer.setString(1, address);
            insertServer.setInt(2, port);
            insertServer.setLong(3, timestamp);
            insertServer.setLong(4, timestamp);
            insertServer.setString(5, country);
            insertServer.setString(6, asn);
            insertServer.setString(7, reverseDns);
            insertServer.setString(8, organization);
            insertServer.setString(9, version);
            insertServer.setObject(10, protocol, Types.INTEGER);
            insertServer.setObject(11, fmlNetworkVersion, Types.INTEGER);
            insertServer.setString(12, motd);
            insertServer.setString(13, icon);
            insertServer.setInt(14, timesSeen);
            insertServer.setObject(15, preventsReports, Types.BOOLEAN);
            insertServer.setObject(16, enforceSecure, Types.BOOLEAN);
            insertServer.setObject(17, whitelist, Types.BOOLEAN);
            insertServer.setObject(18, cracked, Types.BOOLEAN);
            insertServer.setObject(19, maxPlayers, Types.INTEGER);
            insertServer.setObject(20, onlinePlayers, Types.INTEGER);
            insertServer.executeUpdate();
            insertServer.close();

            // Add players, update LastSeen and Name (Potential name change) if duplicate
            if (!players.isEmpty()) {
                PreparedStatement updatePlayers = conn.prepareStatement("INSERT INTO PlayerHistory (Address, Port, PlayerUUID, PlayerName, FirstSeen, LastSeen) VALUES (?, ?, ?, ?, ?, ?) " +
                        "ON CONFLICT (Address, Port, PlayerUUID) DO UPDATE SET " +
                        "LastSeen = EXCLUDED.LastSeen," +
                        "PlayerName = EXCLUDED.PlayerName");

                // Constants
                updatePlayers.setString(1, address);
                updatePlayers.setShort(2, port);

                for (Player player : players) {
                    updatePlayers.setString(3, player.uuid());
                    updatePlayers.setString(4, player.name());
                    updatePlayers.setLong(5, player.lastseen());
                    updatePlayers.setLong(6, player.lastseen());
                    updatePlayers.addBatch();
                }

                // Execute and close
                updatePlayers.executeBatch();
                updatePlayers.close();
            }

            // Add mods, do nothing if duplicate
            if (!mods.isEmpty()) {
                PreparedStatement updateMods = conn.prepareStatement("INSERT INTO Mods (Address, Port, ModId, ModMarker) " +
                        "VALUES (?, ?, ?, ?)" +
                        "ON CONFLICT (Address, Port, ModId) DO NOTHING");

                // Constants
                updateMods.setString(1, address);
                updateMods.setShort(2, port);

                for (Mod mod : mods) {
                    updateMods.setString(3, mod.modid());
                    updateMods.setString(4, mod.modmarker());
                    updateMods.addBatch();
                }

                // Execute and close
                updateMods.executeBatch();
                updateMods.close();
            }
        } catch (SQLException e) {
            Main.logger.error("There was an error during a database transaction!", e);
        }
    }
}
