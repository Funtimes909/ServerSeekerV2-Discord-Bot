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
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import xyz.funtimes909.serverseekerv2_core.records.Server;
import xyz.funtimes909.serverseekerv2_core.util.ServerObjectBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.builders.SearchEmbedBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.builders.ServerEmbedBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.util.APIUtils;
import xyz.funtimes909.serverseekerv2_discord_bot.util.GenericErrorEmbed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Search {
    private final SlashCommandInteractionEvent interaction;
    private StringBuilder query = new StringBuilder();
    private long messageID;
    public int rowCount = 100;
    public int offset = 0;
    public int pointer = 0;

    public Search(SlashCommandInteractionEvent event) {
        this.interaction = event;
    }

    public void search() {
        if (interaction.getOptions().isEmpty()) {
            interaction.getHook().sendMessage("You must provide some search queries!").queue();
            return;
        }

        buildQuery(interaction.getOptions());
        runQuery(true);
    }

    public void buildQuery(List<OptionMapping> options) {
        query = new StringBuilder("servers?");

        for (OptionMapping option : options) {
            switch (option.getName()) {
                case "preventsreports" -> query.append("prevents_reports=").append(option.getAsString()).append("&");
                case "enforcessecurechat" -> query.append("enforce_secure_chat=").append(option.getAsString()).append("&");
                case "description" -> {
                    query.append("motd=");
                    for (String space : option.getAsString().split(" ")) {
                        query.append(space).append("%20");
                    }

                    query.setLength(query.length() - 3);
                    query.append("&");
                }
                default -> query.append(option.getName()).append("=").append(option.getAsString()).append("&");
            }
        }
        query.append("minimal=1&limit=5&offset=").append(offset);
    }

    public void runQuery(boolean firstRun) {
        query.replace(query.lastIndexOf("="), query.length(), "=" + offset);
        JsonElement response = APIUtils.query(query.toString());
        JsonArray array = APIUtils.getAsArray(response);

        if (array == null || array.isEmpty()) {
            interaction.getHook().sendMessage("No results!").queue();
            return;
        }

        if (firstRun) pointer += 5;

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

        // Send embed, update if already sent
        if (firstRun) {
            // Save message ID to reply to it with server embeds later
            interaction.getHook().sendMessageEmbeds(embed).setComponents(pageButtons).queue(message -> {
                messageID = message.getIdLong();
            });
        } else {
            interaction.getHook().editOriginalEmbeds(embed).setComponents(pageButtons).queue();
        }
    }

    public void optionSelected(String address, short port, ButtonInteractionEvent event) {
        JsonArray response = (JsonArray) APIUtils.query(
                "servers?address=" +
                        address +
                        "&port=" +
                        port
        );

        if (response == null || response.isEmpty()) {
            event.getHook().sendMessage("No results!").queue();
            return;
        }

        try {
            JsonObject object = response.get(0).getAsJsonObject();
            Server server = ServerObjectBuilder.buildServerFromApiResponse(object);
            ServerEmbedBuilder embedBuilder = new ServerEmbedBuilder(server);
            MessageCreateAction embed = embedBuilder.build(event.getChannel(), false);

            embed.setMessageReference(messageID);
            embed.queue();
        } catch (IOException e) {
            GenericErrorEmbed.errorEmbed(event.getChannel(), e.getMessage());
        }
    }
}