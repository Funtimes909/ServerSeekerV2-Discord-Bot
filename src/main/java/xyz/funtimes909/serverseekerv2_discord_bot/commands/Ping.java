package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import xyz.funtimes909.serverseekerv2_discord_bot.Records.Server;
import xyz.funtimes909.serverseekerv2_discord_bot.util.PermissionsCheck;
import xyz.funtimes909.serverseekerv2_discord_bot.util.PingUtils;
import xyz.funtimes909.serverseekerv2_discord_bot.util.ServerEmbedBuilder;

public class Ping {
    public static void ping(SlashCommandInteractionEvent event) {
        if (PermissionsCheck.blacklistCheck(event.getUser().getId())) {
            event.getHook().sendMessage("Sorry! You're not authorized to use this command!").queue();
            return;
        }

        event.deferReply().queue();
        String address = event.getOption("address").getAsString();
        short port = 25565;

        if (event.getOption("address") == null) {
            event.getHook().sendMessage("You need to provide an address to ping!").queue();
            return;
        }

        if (event.getOption("port") != null) {
            port = (short) event.getOption("port").getAsInt();
        }

        PingUtils ping = new PingUtils(address, port);
        Server server = ping.parse();

        if (server == null) {
            event.getHook().sendMessage("Connection failed!").queue();
            return;
        }

        ServerEmbedBuilder embedBuilder = new ServerEmbedBuilder(server);
        MessageEmbed embed = embedBuilder.build();

        if (embed != null) {
            event.getHook().sendMessageEmbeds(embed).queue();
        }
    }
}
