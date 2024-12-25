package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.util.APIUtils;
import xyz.funtimes909.serverseekerv2_discord_bot.util.PermissionsManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Inet4Address;
import java.util.regex.Pattern;

public class Takedown {
    public static void takedown(SlashCommandInteractionEvent event) {
        String id = event.getInteraction().getUser().getId();
        String address = event.getOption("address").getAsString();

        if (!PermissionsManager.ownerCheck(id) && !PermissionsManager.trustedUsersCheck(id)) {
            event.getHook().sendMessage("Sorry! You are not authorized to run this command!").setEphemeral(true).queue();
            return;
        }

        if (Pattern.compile("[A-Za-z]").matcher(address).find()) {
            event.getHook().sendMessage("Invalid address!").queue();
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Main.exclude, true))) {
            // Throw an unknown host exception if option isn't an address
            Inet4Address.getByName(address);
            writer.write(address + "\n");

            // Remove entries from the database if requested
            if (event.getOption("remove-entries") != null && event.getOption("remove-entries").getAsBoolean()) {
                APIUtils.api("takedown?address=" + address);
            }

            event.getHook().sendMessage("Added " + address + " to the exclude file").queue();
        } catch (IOException e) {
            event.getHook().sendMessage("Invalid address!").queue();
            Main.logger.error("Error writing to exclude.txt", e);
        }
    }
}