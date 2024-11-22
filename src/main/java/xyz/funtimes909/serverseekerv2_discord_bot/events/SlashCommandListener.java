package xyz.funtimes909.serverseekerv2_discord_bot.events;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.commands.*;
import xyz.funtimes909.serverseekerv2_discord_bot.util.PermissionsCheck;

public class SlashCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (PermissionsCheck.blacklistCheck(event.getUser().getId())) {
            Main.logger.info("Blacklisted user attempted to run command! {} Ran {}", event.getUser().getName(), event.getName());
            event.reply("You are blacklisted!").setEphemeral(true).queue();
            return;
        }

        Main.logger.info("Command: {} run by {} [{}] ({} options)", event.getName(), event.getUser().getName(), event.getUser().getId(), event.getOptions().size());
        event.deferReply().queue();
        switch (event.getName()) {
            case "search" -> Search.search(event);
            case "stats" -> Stats.stats(event);
            case "random" -> Random.random(event);
            case "ping" -> Ping.ping(event);
            case "info" -> Info.info(event);
            case "takedown" -> Takedown.takedown(event);
            case "blacklist" -> Blacklist.blacklist(event);
            case "playerhistory" -> Playerhistory.playerhistory(event);
            case "track" -> Track.track(event);
        }
    }
}
