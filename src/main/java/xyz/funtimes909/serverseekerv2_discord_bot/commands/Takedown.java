package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Takedown {
    public static void takedown(SlashCommandInteractionEvent event) {
        try {
            InetAddress address = Inet4Address.getByName(event.getOption("address").getAsString());
            Main.logger.info("Adding {} to the exclude list! Requested by {}", address, event.getUser().getName());
            event.reply("Added " + address + " to the exclude list!").setEphemeral(true).queue();
        } catch (UnknownHostException e) {
            event.reply("This isn't a valid address!").setEphemeral(true).queue();
        }
    }
}
