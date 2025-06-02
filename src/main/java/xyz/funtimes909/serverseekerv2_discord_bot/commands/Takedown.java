package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.util.PermissionsManager;
import xyz.funtimes909.serverseekerv2_discord_bot.util.Utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.http.HttpResponse;
import java.util.regex.Pattern;

public class Takedown {
    public static void takedown(SlashCommandInteractionEvent event) {
        String id = event.getInteraction().getUser().getId();
        String address = event.getOption("address").getAsString();

        // Only trusted users and the owner can run /takedown
        if (!PermissionsManager.ownerCheck(id) && !PermissionsManager.trustedUsersCheck(id)) {
            event.getHook().editOriginal(":x: Sorry! You are not authorized to run this command!").queue();
            return;
        }

        // Prevent letters
        if (Pattern.compile("[A-Za-z]").matcher(address).find()) {
            event.getHook().editOriginal(":x: Invalid address!").queue();
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Main.exclude, true))) {
            // Throw an unknown host exception if option isn't an address
            Inet4Address.getByName(address);

            // Write the server to the exclude file
            writer.write(address + "\n");

            // Query the API to remove the server from the database
            HttpResponse<String> response = Utils.post("api/v1/takedown?address=" + address);

            String reply = switch (response.statusCode()) {
                case 200 -> String.format(":white_check_mark: Added %s to the exclude file", address);
                case 401 -> ":x: Missing API key! (401 Unauthorized)";
                case 403 -> ":x: Invalid API token! (403 Forbidden)";
                default -> String.format(":x: Unknown response from API! (%d)", response.statusCode());
            };

            event.getHook().editOriginal(reply).queue();
        } catch (IOException e) {
            event.getHook().editOriginal(":x: Invalid address!").queue();
            Main.logger.error("Error writing to exclude.txt", e);
        }
    }
}