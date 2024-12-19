package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import xyz.funtimes909.serverseekerv2_core.records.Server;
import xyz.funtimes909.serverseekerv2_core.util.ServerObjectBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.builders.SearchEmbedBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.builders.ServerEmbedBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.util.APIUtils;

import java.util.ArrayList;
import java.util.List;

import static xyz.funtimes909.serverseekerv2_discord_bot.util.APIUtils.api;

public class Search {
    private final SlashCommandInteractionEvent interaction;
    private final String endpoint = "servers?";
    private StringBuilder query = new StringBuilder();
    public int rowCount = 1600;
    public int pointer = 0;

    public Search(SlashCommandInteractionEvent event) {
        this.interaction = event;
    }

    public void search() {
        if (interaction.getOptions().isEmpty()) {
            interaction.getHook().sendMessage("You must provide some search queries!").queue();
            return;
        }

        // Build Query
        buildQuery(interaction.getOptions());

        // Run query
        runQuery();
    }

    public void buildQuery(List<OptionMapping> options) {
        query = new StringBuilder();

        for (OptionMapping option : options) {
            System.out.println(option.getName());
        }

        for (OptionMapping option : options) {
            switch (option.getName()) {
                case "version" -> query.append("version=").append(option.getAsString()).append("&");
                case "software" -> query.append("software=").append(option.getAsString().toUpperCase()).append("&");
                case "description" -> query.append("motd=").append(option.getAsString()).append("&");
                case "country" -> query.append("country=").append(option.getAsString()).append("&");
                case "asn" -> query.append("asn=").append(option.getAsString()).append("&");
                case "org" -> query.append("org=").append(option.getAsString()).append("&");
                case "hostname" -> query.append("hostname=").append(option.getAsString()).append("&");
                case "icon" -> query.append("icon=").append(option.getAsString()).append("&");
                case "preventsreports" -> query.append("prevents_reports=").append(option.getAsString()).append("&");
                case "whitelist" -> query.append("whitelist=").append(option.getAsString()).append("&");
                case "cracked" -> query.append("cracked=").append(option.getAsString()).append("&");
                case "enforcessecurechat" -> query.append("enforce_secure_chat=").append(option.getAsString()).append("&");
                case "empty" -> query.append("empty=").append(option.getAsString()).append("&");
                case "full" -> query.append("full=").append(option.getAsString()).append("&");
                case "seenafter" -> query.append("seenafter").append(option.getAsString()).append("&");
                case "seenbefore" -> query.append("seenbefore").append(option.getAsString()).append("&");
                case "onlineplayers" -> query.append("onlineplayers=").append(option.getAsString()).append("&");
                case "maxplayers" -> query.append("maxplayers=").append(option.getAsString()).append("&");
            }
        }

        query.append("&limit=5 ");
        query.deleteCharAt(query.length() - 1);
    }

    private void runQuery() {
        JsonArray response = (JsonArray) api(endpoint + query);

        if (response == null || !response.isJsonArray()) {
            interaction.getHook().sendMessage("Something went wrong!").queue();
            return;
        }


        List<Button> buttons = new ArrayList<>();
        List<LayoutComponent> pageButtons = new ArrayList<>();

        for (int i = 1; i < response.size() + 1; i++) {
            buttons.add(Button.of(ButtonStyle.SUCCESS, "SearchButton" + i, String.valueOf(i)));
        }

        if (rowCount <= 5) {
            // Less than 6 total results, only one page, don't scroll
            pageButtons.add(ActionRow.of(buttons));
        } else if (pointer >= (rowCount - 6)) {
            // Last page, don't let the user scroll forwards anymore
            pageButtons.add(ActionRow.of(buttons));
            pageButtons.add(ActionRow.of(Button.primary("PagePrevious",Emoji.fromFormatted("U+2B05")), Button.primary("PageNext", Emoji.fromFormatted("U+27A1")).asDisabled()));
        } else if (pointer <= 6) {
            // First page, don't let the user scroll back
            pageButtons.add(ActionRow.of(buttons));
            pageButtons.add(ActionRow.of(Button.primary("PagePrevious", Emoji.fromFormatted("U+2B05")).asDisabled(), Button.primary("PageNext", Emoji.fromFormatted("U+27A1"))));
        } else {
            // Normal page, let the user scroll both directions
            pageButtons.add(ActionRow.of(buttons));
            pageButtons.add(ActionRow.of(Button.primary("PagePrevious", Emoji.fromFormatted("U+2B05")), Button.primary("PageNext", Emoji.fromFormatted("U+27A1"))));
        }

        MessageEmbed embed = SearchEmbedBuilder.parse(response, rowCount, (pointer / 5));
        interaction.getHook().sendMessageEmbeds(embed).addComponents(pageButtons).queue();
    }

    public void serverSelectedButtonEvent(String address, short port, ButtonInteractionEvent event) {
        JsonArray response = (JsonArray) APIUtils.api(
                "servers?address=" +
                        address +
                        "&port=" +
                        port
        );

        if (response == null) {
            event.getHook().sendMessage("No results!").queue();
            return;
        }

        JsonObject object = response.get(0).getAsJsonObject();
        Server server = ServerObjectBuilder.buildServerFromApiResponse(object);

        ServerEmbedBuilder embedBuilder = new ServerEmbedBuilder(server);
        MessageEmbed embed = embedBuilder.build(false);

        event.getHook().sendMessageEmbeds(embed).queue();
    }
}