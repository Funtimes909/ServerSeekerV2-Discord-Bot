package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.util.APIUtils;
import xyz.funtimes909.serverseekerv2_discord_bot.util.ConnectionPool;

import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Stats {
    public static void stats(SlashCommandInteractionEvent event) {
        JsonElement response = APIUtils.api("stats");

        if (response == null || !response.isJsonObject()) {
            event.getHook().sendMessage("No results!").queue();
            return;
        }

        JsonObject object = response.getAsJsonObject();
        int serverCount = object.get("servers").getAsInt();

        MessageEmbed embed = new EmbedBuilder()
                .setTitle("Stats")
                .setColor(new Color(0, 255, 0))
                .setAuthor("ServerSeekerV2", "https://discord.gg/WEErxAP8kz", "https://funtimes909.xyz/assets/images/serverseekerv2-icon-cropped.png")
                .setFooter("Funtimes909", "https://funtimes909.xyz/avatar-gif")
                .addField("Unique Servers Found", "**" + serverCount + "**", false)
                .build();

        event.getHook().sendMessageEmbeds(embed).queue();
    }
}