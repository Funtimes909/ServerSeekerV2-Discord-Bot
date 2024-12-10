package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import com.google.gson.JsonArray;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import xyz.funtimes909.serverseekerv2_discord_bot.builders.PlayerhistoryEmbedBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.util.APIUtils;

import java.util.ArrayList;
import java.util.List;

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

        List<ItemComponent> buttons = new ArrayList<>();
        for (int i = 1; i < response.size() + 1; i++) {
            buttons.add(Button.success("SearchButton" + i, String.valueOf(i)));
        }

        if (response.size() < 5) {
            event.getHook().sendMessageEmbeds(embed).addActionRow(buttons).queue();
        } else {
            event.getHook().sendMessageEmbeds(embed)
                    .addActionRow(buttons.stream().limit(5).toList())
                    .addActionRow(buttons.stream().skip(5).limit(5).toList())
                    .queue();
        }
    }
}