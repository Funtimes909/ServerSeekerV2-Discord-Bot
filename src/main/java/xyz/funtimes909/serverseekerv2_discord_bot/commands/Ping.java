package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import com.google.gson.JsonParser;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.builders.ServerEmbedBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.types.Server;
import xyz.funtimes909.serverseekerv2_discord_bot.util.GenericErrorEmbed;
import xyz.funtimes909.serverseekerv2_discord_bot.util.PingUtils;
import xyz.funtimes909.serverseekerv2_discord_bot.util.Utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Ping {
    public static void ping(SlashCommandInteractionEvent event) {
        String address = event.getOption("address").getAsString();
        if (address.equals("localhost") || address.equals("0.0.0.0") || address.startsWith("127")) {
            event.getHook().editOriginal(":x: You can't ping this address!").queue();
            return;
        }

        short port = event.getOption("port") != null ?
                Short.parseShort(event.getOption("port").getAsString()) :
                25565;

        String response = PingUtils.ping(connect(address, port));

        if (response == null) {
            event.getHook().editOriginal(":x: Server did not connect!").queue();
            return;
        }

        Server server = Utils.buildServerFromPing(
                address,
                port,
                JsonParser.parseString(response).getAsJsonObject()
        );

        if (server == null) {
            event.getHook().editOriginal(":x: Server did not connect!").queue();
            return;
        }

        try {
            ServerEmbedBuilder embedBuilder = new ServerEmbedBuilder(server);
            MessageCreateData embed = embedBuilder.build(true);

            if (embed == null) {
                event.getHook().editOriginal(":x: Server did not connect!").queue();
                return;
            }

            // Edit success message by ID
            event.getHook().editOriginal(new MessageEditBuilder()
                    .applyCreateData(embedBuilder.build(false))
                    .build()
            ).queue();
            embed.close();
        } catch (IOException e) {
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