package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import xyz.funtimes909.serverseekerv2_discord_bot.util.BlacklistCheck;

public class Ping {
    public static void ping(SlashCommandInteractionEvent event) {
        event.deferReply();
        if (BlacklistCheck.check(event.getUser().getId())) {
            event.reply("Sorry! You're not authorized to use this command!").queue();
            return;
        }
    }
}
