package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.util.DatabaseConnectionPool;
import xyz.funtimes909.serverseekerv2_discord_bot.util.PermissionsCheck;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Inet4Address;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class Takedown {
    public static void takedown(SlashCommandInteractionEvent event) {
        String id = event.getInteraction().getUser().getId();
        String address = event.getOption("address").getAsString();

        if (!PermissionsCheck.ownerCheck(id) && !PermissionsCheck.trustedUsersCheck(id) || PermissionsCheck.blacklistCheck(id)) {
            event.reply("Sorry! You are not authorized to run this command!").setEphemeral(true).queue();
            return;
        }

        if (Pattern.compile("[A-Za-z]").matcher(address).find()) {
            event.reply("Invalid address!").queue();
            return;
        }

        event.deferReply().queue();

        if (event.getOption("remove-entries") != null && event.getOption("remove-entries").getAsBoolean()) {
            try (Connection conn = DatabaseConnectionPool.getConnection()) {
                PreparedStatement playerhistory = conn.prepareStatement("DELETE FROM playerhistory WHERE address = ?");
                playerhistory.setString(1, address);

                PreparedStatement mods = conn.prepareStatement("DELETE FROM mods WHERE address = ?");
                mods.setString(1, address);

                PreparedStatement servers = conn.prepareStatement("DELETE FROM servers WHERE address = ?");
                servers.setString(1, address);

                playerhistory.executeUpdate();
                mods.executeUpdate();
                servers.executeUpdate();
            } catch (SQLException e) {
                Main.logger.error("Error removing entries from database", e);
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("exclude.txt", true))) {
                // Throw an unknown host exception if option isn't an address
                Inet4Address.getByName(address);
                writer.write(address + "\n");
                event.getHook().sendMessage("Added " + address + " to the exclude file").queue();
            } catch (IOException e) {
                event.getHook().sendMessage("Invalid address!").queue();
                Main.logger.error("Error writing to exclude.txt", e);
            }
        }
    }
}