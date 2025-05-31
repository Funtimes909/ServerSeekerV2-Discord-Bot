package xyz.funtimes909.serverseekerv2_discord_bot.events;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.commands.*;
import xyz.funtimes909.serverseekerv2_discord_bot.util.PermissionsManager;

import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SlashCommandListener extends ListenerAdapter {
    public static final HashMap<Long, Search> searchCommands = new HashMap<>();
    private static final Executor executor = Executors.newVirtualThreadPerTaskExecutor();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (PermissionsManager.blacklistCheck(event.getUser().getId())) {
            Main.logger.warn("Blacklisted user attempted to run command! {} Ran {}", event.getUser().getName(), event.getName());
            event.reply("You are blacklisted!").setEphemeral(true).queue();
            return;
        }

        if (event.isFromGuild() && PermissionsManager.blacklistServerCheck(event.getGuild().getId())) {
            Main.logger.warn("{} in blacklisted server, {} attempted to run {}", event.getUser().getName(), event.getGuild().getName(), event.getName());
            event.reply("This server is blacklisted!").queue();
            return;
        }

        Main.logger.info("Command: {} run by {} [{}] ({} options)", event.getName(), event.getUser().getName(), event.getUser().getId(), event.getOptions().size());

        event.deferReply().queue();
        event.getHook().sendMessage(":thinking: Thinking...").queue(message -> {

            // Execute command accordingly
            switch (event.getName()) {
                case "stats" -> executor.execute(() -> Stats.stats(event));
                case "random" -> executor.execute(() -> Random.random(event));
                case "ping" -> executor.execute(() -> Ping.ping(event));
                case "info" -> executor.execute(() -> Info.info(event));
                case "takedown" -> executor.execute(() -> Takedown.takedown(event));
                case "blacklist" -> executor.execute(() -> Blacklist.blacklist(event));
                case "playerhistory" -> executor.execute(() -> Playerhistory.playerhistory(event));
                case "track" -> executor.execute(() -> Track.track(event));
                case "search" -> {
                    Search command = new Search(event);
                    executor.execute(command::search);
                    searchCommands.put(message.getIdLong(), command);
                }
            }
        });
    }
}