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
                .setDescription("ServerSeekerV2 is an open source project made by Funtimes909 to scan for Minecraft servers, it supports Java and Bedrock edition Minecraft servers, advanced filters for narrowing down search results, and eventually, a whitelist checker. Get information on specific commands by running /help")
                .build();

        event.getHook().sendMessageEmbeds(embed).queue();
    }
}
