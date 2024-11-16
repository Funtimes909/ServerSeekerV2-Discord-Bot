package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.util.DatabaseConnectionPool;
import xyz.funtimes909.serverseekerv2_discord_bot.util.PermissionsCheck;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Takedown {
    public static void takedown(SlashCommandInteractionEvent event) {
        String id = event.getInteraction().getUser().getId();
        String address = event.getOption("address").getAsString();

        if (!PermissionsCheck.ownerCheck(id) && !PermissionsCheck.trustedUsersCheck(id) || PermissionsCheck.blacklistCheck(id)) {
            event.reply("Sorry! You are not authorized to run this command!").setEphemeral(true).queue();
            return;
        }

        if (event.getOption("remove-entries") != null && event.getOption("remove-entries").getAsBoolean()) {
            try (Connection conn = DatabaseConnectionPool.getConnection()) {
                PreparedStatement statement = conn.prepareStatement("DELETE FROM servers WHERE address = ?");
                statement.addBatch("DELETE FROM playerhistory WHERE address = ?");
                statement.addBatch("DELETE FROM mods WHERE address = ?");
                statement.setString(1, address);
                statement.executeBatch();
            } catch (SQLException e) {
                Main.logger.error("Error removing entries from database", e);
            }
        }
    }
}