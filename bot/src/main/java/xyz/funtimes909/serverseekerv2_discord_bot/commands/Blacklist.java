package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.util.PermissionsManager;

import java.io.FileWriter;
import java.io.IOException;

public class Blacklist {
    public static void blacklist(SlashCommandInteractionEvent event) {
        String id = event.getUser().getId();
        String user = event.getOption("user").getAsString();
        String username = event.getOption("user").getAsUser().getName();

        if (!PermissionsManager.ownerCheck(id) && !PermissionsManager.trustedUsersCheck(id)) {
            event.getHook().editOriginal(":x: Sorry! You are not authorized to run this command!").queue();
            return;
        }

        if (user.equals(Main.ownerId)) {
            event.getHook().editOriginal("So you think you're smart huh?").queue();
            return;
        }

        if (PermissionsManager.trustedUsers.contains(user)) {
            event.getHook().editOriginal(":x: You can't blacklist a trusted user!").queue();
            return;
        }

        if (event.getOption("operation").getAsString().equalsIgnoreCase("add")) {
            if (PermissionsManager.blacklist.contains(user)) {
                event.getHook().editOriginal("<@" + user + "> is already blacklisted!").queue();
                return;
            }
            PermissionsManager.blacklist.add(user);

            try (FileWriter file = new FileWriter("blacklist.txt", true)) {
                file.write(user + "\n");
                Main.logger.info("Adding {} to the blacklist (Requested by trusted user {})", username, event.getUser().getName());
                event.getHook().editOriginal("Added <@" + user + "> to the blacklist!").queue();
            } catch (IOException e) {
                Main.logger.error("blacklist.txt malformed or not found!", e);
            }
        }

        if (event.getOption("operation").getAsString().equalsIgnoreCase("remove")) {
            if (!PermissionsManager.blacklist.contains(user)) {
                event.getHook().editOriginal("<@" + user + "> is not blacklisted!").queue();
                return;
            }
            PermissionsManager.blacklist.remove(id);
        }
    }
}