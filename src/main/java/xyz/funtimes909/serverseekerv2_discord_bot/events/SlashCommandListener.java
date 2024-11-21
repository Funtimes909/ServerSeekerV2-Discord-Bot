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
            Main.logger.info("Command: {} run by {} [{}] ({} options)", event.getName(), event.getUser().getName(), event.getUser().getId(), event.getOptions().size());

            switch (event.getName()) {
                case "search":
                    Search.search(event);
                    break;
                case "stats":
                    Stats.stats(event);
                    break;
                case "random":
                    Random.random(event);
                    break;
                case "ping":
                    Ping.ping(event);
                    break;
                case "info":
                    Info.info(event);
                    break;
                case "takedown":
                    Takedown.takedown(event);
                    break;
                case "blacklist":
                    Blacklist.blacklist(event);
                    break;
                case "playerhistory":
                    Playerhistory.playerhistory(event);
                    break;
                case "track":
                    Track.track(event);
                    break;
            }
        }
    }
}
