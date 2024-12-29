package xyz.funtimes909.serverseekerv2_discord_bot.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;

import java.awt.*;

public class GenericErrorEmbed {
    public static void errorEmbed(MessageChannel channel, String error) {
        Main.logger.error(error);
        if (channel != null) {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(new Color(0xFF0000))
                    .setTitle("Fatal error occurred!")
                    .addField("Error Message", error + "\n This is a bug! please report it at the link below!", false)
                    .build();

            channel.sendMessageEmbeds(embed).addActionRow(Button.link("https://github.com/Funtimes909/ServerSeekerV2-Discord-Bot/issues", "Report Bug")).queue();
        }
    }
}
