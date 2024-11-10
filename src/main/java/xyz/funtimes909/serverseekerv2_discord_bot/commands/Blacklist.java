package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.util.PermissionsCheck;

import java.io.FileWriter;
import java.io.IOException;

public class Blacklist {
    public static void blacklist(SlashCommandInteractionEvent event) {
        if (!PermissionsCheck.trustedUsersCheck(event.getUser().getId())) {
            event.reply("Sorry! You're not authorized to use this command!").queue();
            return;
        }

        if (event.getOption("blacklist").getAsString().equalsIgnoreCase("add")) {
            try (FileWriter file = new FileWriter("blacklist.txt")) {
                file.write(event.getOption("user").getAsString() + "\n");
                Main.logger.info("Adding {} to the blacklist", event.getOption("user").getAsString());
                event.reply("Added " + event.getOption("user").getAsString() + " to the blacklist!").setEphemeral(true).queue();
            } catch (IOException e) {
                Main.logger.error("blacklist.txt malformed or not found!", e);
            }
        }
    }
}