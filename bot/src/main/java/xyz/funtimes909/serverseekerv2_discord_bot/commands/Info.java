package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class Info {
    public static void info(SlashCommandInteractionEvent event) {
        MessageEmbed embed = new EmbedBuilder()
                .setTitle("ServerSeekerV2", "https://github.com/Funtimes909/ServerSeekerV2")
                .setColor(new Color(0, 255, 0))
                .setAuthor("Funtimes909", "https://funtimes909.xyz", "https://funtimes909.xyz/avatar-gif")
                .setDescription("ServerSeekerV2 is an open source project by Funtimes909 that scans the internet and logs online minecraft servers and players, advanced filters for searching the database, a whitelist checker, cracked server detection and more!")
                .build();

        event.getHook().sendMessageEmbeds(embed).queue();
    }
}
