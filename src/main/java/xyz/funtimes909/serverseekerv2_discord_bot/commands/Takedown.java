package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.util.PermissionsCheck;

import java.io.FileWriter;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;

public class Takedown {
    public static void takedown(SlashCommandInteractionEvent event) {
        if (PermissionsCheck.blacklistCheck(event.getUser().getId()) || !PermissionsCheck.trustedUsersCheck(event.getUser().getId())) {
            event.reply("Sorry! You are not authorized to run this command!").setEphemeral(true).queue();
            return;
        }

        try {
            InetAddress address = Inet4Address.getByName(event.getOption("address").getAsString());

            FileWriter writer = new FileWriter("exclude.txt");
            writer.write(event.getOption("address").getAsString() + "\n");
            writer.close();

            Main.logger.info("Adding {} to the exclude list! Requested by {}", address, event.getUser().getName());
            event.reply("Added " + address + " to the exclude list!").setEphemeral(true).queue();
        } catch (IOException e) {
            event.reply("This isn't a valid address!").setEphemeral(true).queue();
        }
    }
}
