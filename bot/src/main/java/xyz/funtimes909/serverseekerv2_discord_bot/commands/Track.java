package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.util.PermissionsManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class Track {
    record TrackedPlayer(String player, String webhook) {}

    public static void track (SlashCommandInteractionEvent event) {
        String id = event.getUser().getId();
        String player = event.getOption("player").getAsString();
        String webhook = event.getOption("webhook").getAsString();

        if (!PermissionsManager.ownerCheck(id) && !PermissionsManager.trustedUsersCheck(id)) {
            event.getHook().sendMessage("Sorry! You are not authorized to run this command!").setEphemeral(true).queue();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("tracks.json"))) {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            Type playersType = new TypeToken<List<TrackedPlayer>>() {}.getType();
            List<TrackedPlayer> players = gson.fromJson(reader, playersType);
            players.add(new TrackedPlayer(player, webhook));

            event.getHook().sendMessage("Tracking " + player + " on next scan").queue();
            gson.toJson(players, new FileWriter(Main.tracksFile));

        } catch (IOException e) {
            Main.logger.error("Error when running /track!", e);
        }
    }
}