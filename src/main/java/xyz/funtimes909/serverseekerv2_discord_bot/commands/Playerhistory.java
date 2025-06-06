package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import xyz.funtimes909.serverseekerv2_discord_bot.builders.PlayerhistorySearchBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.util.Utils;

public class Playerhistory {
    public static void playerhistory(SlashCommandInteractionEvent event) {
        if (event.getOption("player") != null && event.getOption("address") != null) {
            event.getHook().editOriginal(":x: Please select only one option!").queue();
            return;
        }

        if (event.getOption("player") == null && event.getOption("address") == null) {
            event.getHook().editOriginal(":x: Please select an option!").queue();
            return;
        }

        String pageName;
        if (event.getOption("player") != null) {
            pageName = event.getOption("player").getAsString();
        } else {
            pageName = event.getOption("address").getAsString();
        }

        String query = event.getOption("player") != null ?
                "api/v1/playerhistory?limit=10&player=" + event.getOption("player").getAsString() :
                "api/v1/playerhistory?limit=10&address=" + event.getOption("address").getAsString();

        JsonElement response = Utils.query(query);

        if (response == null) {
            event.getHook().editOriginal(":x: No results!").queue();
            return;
        }

        JsonArray array = response.getAsJsonObject().getAsJsonArray("results");

        if (array.isEmpty()) {
            event.getHook().editOriginal(":x: No results!").queue();
            return;
        }

        MessageEmbed embed = PlayerhistorySearchBuilder.build(array, pageName);
        event.getHook().editOriginalEmbeds(embed).queue();
    }
}