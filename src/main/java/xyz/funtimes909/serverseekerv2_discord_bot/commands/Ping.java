package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import xyz.funtimes909.serverseekerv2_discord_bot.builders.ServerEmbedBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.records.Server;
import xyz.funtimes909.serverseekerv2_discord_bot.util.PermissionsCheck;
import xyz.funtimes909.serverseekerv2_discord_bot.util.PingUtils;

public class Ping {
    public static void ping(SlashCommandInteractionEvent event) {
        String id = event.getUser().getId();

        if (PermissionsCheck.blacklistCheck(id)) {
            event.reply("Sorry! You are not authorized to run this command!").setEphemeral(true).queue();
            return;
        }

        if (event.getOption("address").getAsString().equals("localhost") || event.getOption("address").getAsString().equals("0.0.0.0") || event.getOption("address").getAsString().startsWith("127")) {
            event.reply("You can't ping this address!").queue();
            return;
        }

        event.deferReply().queue();

        short port = 25565;
        if (event.getOption("port") != null) port = (short) event.getOption("port").getAsInt();

        PingUtils ping = new PingUtils(event.getOption("address").getAsString(), port);
        Server server = ping.parse();

        if (server == null) {
            event.getHook().sendMessage("Server did not connect!").queue();
            return;
        }

        ServerEmbedBuilder embedBuilder = new ServerEmbedBuilder(server);
        MessageEmbed embed = embedBuilder.build(true);

        if (embed == null) {
            event.getHook().sendMessage("Something went wrong running this command!").queue();
            return;
        }

        event.getHook().sendMessageEmbeds(embed).queue();
    }
}
