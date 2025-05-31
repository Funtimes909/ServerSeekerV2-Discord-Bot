package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.builders.ServerEmbedBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.types.Server;
import xyz.funtimes909.serverseekerv2_discord_bot.util.GenericErrorEmbed;
import xyz.funtimes909.serverseekerv2_discord_bot.util.Utils;

import java.io.IOException;

public class Random {
    public static void random(SlashCommandInteractionEvent event) {
        JsonElement response = Utils.query("api/v1/server?random=true");

        if (response == null || !response.isJsonObject()) {
            event.getHook().sendMessage("No results!").queue();
            return;
        }

        try {
            JsonObject object = response.getAsJsonObject();
            Server server = Utils.buildLargeServer(object);
            ServerEmbedBuilder embedBuilder = new ServerEmbedBuilder(server);

            event.getHook().editOriginal(new MessageEditBuilder()
                            .applyCreateData(embedBuilder.build(false))
                            .build()
            ).queue();
        } catch (IOException e) {
            GenericErrorEmbed.errorEmbed(event.getChannel(), e.getMessage());
        }
    }
}
