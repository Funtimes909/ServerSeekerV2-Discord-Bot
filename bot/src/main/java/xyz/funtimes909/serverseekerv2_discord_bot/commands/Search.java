package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import xyz.funtimes909.serverseekerv2_discord_bot.builders.SearchEmbedBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.builders.ServerEmbedBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.util.GenericErrorEmbed;
import xyz.funtimes909.serverseekerv2_discord_bot.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Search {
    private final SlashCommandInteractionEvent interaction;
    private StringBuilder query = new StringBuilder();
    public int offset = 0;
    public int pointer = 5;

    public Search(SlashCommandInteractionEvent event) {
        this.interaction = event;
    }

    public void search() {
        if (interaction.getOptions().isEmpty()) {
            interaction.getHook().editOriginal(":x: You must provide some search queries!").queue();
            return;
        }

        buildQuery(interaction.getOptions());
        runQuery();
    }

    public void buildQuery(List<OptionMapping> options) {
        query = new StringBuilder("api/v1/servers?");

        for (OptionMapping option : options) {
            switch (option.getName()) {
                case "prevents_reports" -> query.append("prevents_chat_reports=").append(option.getAsString()).append("&");
                case "enforces_secure_chat" -> query.append("enforces_secure_chat=").append(option.getAsString()).append("&");
                case "online_players" -> query.append("online_players=").append(option.getAsString()).append("&");
                case "description" -> {
                    query.append("description=");
                    for (String text : option.getAsString().split(" ")) {
                        query.append(text).append("%20");
                    }

                    query.setLength(query.length() - 3);
                    query.append("&");
                }
                default -> query.append(option.getName()).append("=").append(option.getAsString()).append("&");
            }
        }
        query.append("limit=5&offset=").append(offset);
    }

    public void runQuery() {
        query.replace(query.lastIndexOf("="), query.length(), "=" + offset);
        JsonElement response = Utils.query(query.toString());

        if (response == null || response.isJsonNull()) {
            interaction.getHook().editOriginal(":x: No results!").queue();
            return;
        }

        JsonObject results = response.getAsJsonObject();
        int rowCount = results.get("total_results").getAsInt();
        JsonArray array = results.get("results").getAsJsonArray();

        if (array == null || array.isEmpty()) {
            interaction.getHook().editOriginal(":x: No results!").queue();
            return;
        }

        List<ItemComponent> buttons = new ArrayList<>();
        List<LayoutComponent> pageButtons = new ArrayList<>();

        for (int i = 1; i < array.size() + 1; i++) {
            buttons.add(Button.success("SearchButton" + i, String.valueOf(i)));
        }

        if (rowCount <= 5) {
            // Less than 6 total results, only one page, don't scroll
            pageButtons.add(ActionRow.of(buttons));
        } else if (pointer >= (rowCount - 6)) {
            // Last page, don't let the user scroll forwards anymore
            pageButtons.add(ActionRow.of(buttons));
            pageButtons.add(ActionRow.of(Button.primary("SearchPrevious", Emoji.fromFormatted("U+2B05")), Button.primary("SearchNext", Emoji.fromFormatted("U+27A1")).asDisabled()));
        } else if (pointer <= 6) {
            // First page, don't let the user scroll back
            pageButtons.add(ActionRow.of(buttons));
            pageButtons.add(ActionRow.of(Button.primary("SearchPrevious", Emoji.fromFormatted("U+2B05")).asDisabled(), Button.primary("SearchNext", Emoji.fromFormatted("U+27A1"))));
        } else {
            // Normal page, let the user scroll both directions
            pageButtons.add(ActionRow.of(buttons));
            pageButtons.add(ActionRow.of(Button.primary("SearchPrevious", Emoji.fromFormatted("U+2B05")), Button.primary("SearchNext", Emoji.fromFormatted("U+27A1"))));
        }

        MessageEmbed embed = SearchEmbedBuilder.parse(array, rowCount, rowCount <= 5 ? 1 : (pointer / 5));

        // Send embed, overwriting initial message
        interaction.getHook().editOriginalEmbeds(embed).setComponents(pageButtons).queue();
    }

    public void optionSelected(String address, short port, ButtonInteractionEvent event) {
        JsonObject response = Utils.query(
                "api/v1/servers?address=" +
                        address +
                        "&port=" +
                        port
        ).getAsJsonObject();

        try {
            JsonArray array = response.get("results").getAsJsonArray();
            JsonObject object = array.get(0).getAsJsonObject();
            ServerEmbedBuilder embedBuilder = new ServerEmbedBuilder(Utils.buildServerFromApiResponse(object));
            MessageCreateData embed = embedBuilder.build(false);

            event.getHook().sendMessage(embed).queue();
        } catch (IOException e) {
            GenericErrorEmbed.errorEmbed(event.getChannel(), e.getMessage());
        }
    }
}