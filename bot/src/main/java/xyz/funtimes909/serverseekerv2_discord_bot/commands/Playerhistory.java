package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import com.google.gson.JsonArray;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import xyz.funtimes909.serverseekerv2_discord_bot.builders.PlayerhistoryEmbedBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.util.APIUtils;

public class Playerhistory {
    public static void playerhistory(SlashCommandInteractionEvent event) {
        if (event.getOption("player") != null && event.getOption("address") != null) {
            event.getHook().sendMessage("Please select only one option!").queue();
            return;
        }


        // Select query
        String query = event.getOption("player") != null ?
                "history?player=" + event.getOption("player").getAsString() :
                "history?address=" + event.getOption("address").getAsString();

        // temp fix for appending arbitrary parameters
        if (query.contains("&")) return;

        JsonArray response = APIUtils.api(query);
        if (response == null) {
            event.getHook().sendMessage("No results!").queue();
            return;
        }

        MessageEmbed embed = PlayerhistoryEmbedBuilder.build(response, query.split("=")[1]);
        if (embed == null) {
            event.getHook().sendMessage("No results!").queue();
            return;
        }

        // Send
        event.getHook().sendMessageEmbeds(embed).queue();
    }
}