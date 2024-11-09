package xyz.funtimes909.serverseekerv2_discord_bot.events;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.commands.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SlashCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Main.logger.info("Command: {} run by {} [{}]", event.getName(), event.getUser().getName(), event.getUser().getId());

            switch (event.getName()) {
                case "search":
                    executor.submit(() -> {Search.search(event);});
                    break;
                case "stats":
                    executor.submit(() -> {Stats.stats(event);});
                    break;
                case "random":
                    executor.submit(() -> {Random.random(event);});
                    break;
                case "ping":
                    executor.submit(() -> {Ping.ping(event);});
                    break;
                case "info":
                    executor.submit(() -> {Info.info(event);});
                    break;
                case "takedown":
                    executor.submit(() -> {Takedown.takedown(event);});
                    break;
                case "blacklist":
                    executor.submit(() -> {Blacklist.blacklist(event);});
                    break;
                case "playerhistory":
                    executor.submit(() -> {Playerhistory.playerhistory(event);});
                    break;
            }
        }
    }
}
