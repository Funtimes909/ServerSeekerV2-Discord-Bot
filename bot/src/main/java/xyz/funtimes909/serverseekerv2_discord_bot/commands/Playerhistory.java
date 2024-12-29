package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import xyz.funtimes909.serverseekerv2_discord_bot.builders.PlayerhistorySearchBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.builders.ServerHistoryEmbedBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.util.APIUtils;

import java.util.ArrayList;
import java.util.List;

public class Playerhistory {
    public static void playerhistory(SlashCommandInteractionEvent event) {
        if (event.getOption("player") != null && event.getOption("address") != null) {
            event.getHook().sendMessage("Please select only one option!").queue();
            return;
        }

        if (event.getOption("player") == null && event.getOption("address") == null) {
            event.getHook().sendMessage("Please select an option!").queue();
            return;
        }

        String query = event.getOption("player") != null ?
                "history?player=" + event.getOption("player").getAsString() :
                "history?address=" + event.getOption("address").getAsString();

        JsonElement response = APIUtils.query(query);
        JsonArray array = APIUtils.getAsArray(response);

        if (array == null || array.isEmpty()) {
            event.getHook().sendMessage("No results!").queue();
            return;
        }

        MessageEmbed embed = PlayerhistorySearchBuilder.build(array, query.split("=")[1]);
        List<ItemComponent> buttons = new ArrayList<>();
        for (int i = 1; i < array.size() + 1; i++) {
            buttons.add(Button.success("PlayerHistory" + i, String.valueOf(i)));
        }

        // Add buttons to message
        if (array.size() < 5) {
            event.getHook().sendMessageEmbeds(embed).addActionRow(buttons).queue();
        } else {
            event.getHook().sendMessageEmbeds(embed)
                    .addActionRow(buttons.stream().limit(5).toList())
                    .addActionRow(buttons.stream().skip(5).limit(5).toList())
                    .queue();
        }
    }

    // Show servers full playerhistory
    public static void optionSelected(String address, ButtonInteractionEvent event) {
        MessageEmbed embed = ServerHistoryEmbedBuilder.build(address);
        if (embed == null) {
            event.getHook().sendMessage("No results!").queue();
            return;
        }

        event.getHook().sendMessageEmbeds(embed).queue();
    }
}