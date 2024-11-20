package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.util.PermissionsCheck;

import java.io.FileWriter;
import java.io.IOException;

public class Blacklist {
    public static void blacklist(SlashCommandInteractionEvent event) {
        String id = event.getUser().getId();
        String user = event.getOption("user").getAsString();

        if (!PermissionsCheck.ownerCheck(id) && !PermissionsCheck.trustedUsersCheck(id)) {
            event.reply("Sorry! You are not authorized to run this command!").setEphemeral(true).queue();
            return;
        }

        if (user.equals(Main.ownerId)) {
            event.reply("So you think you're smart huh?").queue();
            return;
        }

        if (event.getOption("blacklist").getAsString().equalsIgnoreCase("add")) {
            try (FileWriter file = new FileWriter("blacklist.txt")) {
                file.write(user + "\n");

                Main.logger.info("Adding {} to the blacklist", user);
                event.reply("Added <@" + user + "> to the blacklist!").setEphemeral(true).queue();
            } catch (IOException e) {
                Main.logger.error("blacklist.txt malformed or not found!", e);
            }
        }
    }
}