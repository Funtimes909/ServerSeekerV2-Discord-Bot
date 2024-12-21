package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import com.google.gson.JsonParser;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import xyz.funtimes909.serverseekerv2_core.database.Database;
import xyz.funtimes909.serverseekerv2_core.records.Server;
import xyz.funtimes909.serverseekerv2_core.util.ServerObjectBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.builders.ServerEmbedBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.util.ConnectionPool;
import xyz.funtimes909.serverseekerv2_discord_bot.util.GenericErrorEmbed;
import xyz.funtimes909.serverseekerv2_discord_bot.util.PingUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class Ping {
    public static void ping(SlashCommandInteractionEvent event) {
//        if (event.getOption("address").getAsString().equals("localhost") || event.getOption("address").getAsString().equals("0.0.0.0") || event.getOption("address").getAsString().startsWith("127")) {
//            event.getHook().sendMessage("You can't ping this address!").queue();
//            return;
//        }

        String address = event.getOption("address").getAsString();
        short port = event.getOption("port") != null ?
                Short.parseShort(event.getOption("port").getAsString()) :
                25565;

        String response = PingUtils.ping(connect(address, port));

        if (response == null) {
            event.getHook().sendMessage("Server did not connect!").queue();
            return;
        }

        Server server = ServerObjectBuilder.buildServerFromPing(
                address,
                port,
                JsonParser.parseString(response).getAsJsonObject()
        );

        if (server == null) {
            event.getHook().sendMessage("Server did not connect!").queue();
            return;
        }

        try {
            ServerEmbedBuilder embedBuilder = new ServerEmbedBuilder(server);
            MessageEmbed embed = embedBuilder.build(event.getMessageChannel(), true).get();
            if (embed == null) {
                event.getHook().sendMessage("Server did not connect!").queue();
                return;
            }

            event.getHook().sendMessageEmbeds(embed).queue();
        } catch (InterruptedException | ExecutionException e) {
            GenericErrorEmbed.errorEmbed(event.getMessageChannel(), e.getMessage());
        }

        try (Connection conn = ConnectionPool.getConnection()) {
            if (conn != null) {
                Database.updateServer(conn, server);
            }
        } catch (SQLException e) {
            GenericErrorEmbed.errorEmbed(event.getMessageChannel(), e.getMessage());
        }
    }

    private static Socket connect(String address, int port) {
        try {
            // Don't use try-with-resources, socket needs to be used later
            Socket socket = new Socket();
            socket.setSoTimeout(5000);
            socket.connect(new InetSocketAddress(address, port), 5000);
            return socket;
        } catch (IOException e) {
            return null;
        }
    };
}