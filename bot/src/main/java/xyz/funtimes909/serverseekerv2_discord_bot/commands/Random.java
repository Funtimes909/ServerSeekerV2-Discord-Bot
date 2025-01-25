package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import xyz.funtimes909.serverseekerv2_core.records.Server;
import xyz.funtimes909.serverseekerv2_core.util.ServerObjectBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.builders.ServerEmbedBuilder;
import xyz.funtimes909.serverseekerv2_discord_bot.util.APIUtils;
import xyz.funtimes909.serverseekerv2_discord_bot.util.GenericErrorEmbed;

import java.io.IOException;

public class Random {
    public static void random(SlashCommandInteractionEvent event) {
        JsonElement response = APIUtils.query("random");

        if (response == null || !response.isJsonObject()) {
            event.getHook().sendMessage("No results!").queue();
            return;
        }

        try {
            JsonObject object = response.getAsJsonObject();
            Server server = ServerObjectBuilder.buildServerFromApiResponse(object);
            ServerEmbedBuilder embedBuilder = new ServerEmbedBuilder(server);
            MessageEditData embed = embedBuilder.build(event.getChannel(), false);

            if (embed == null) {
                event.getHook().sendMessage("No results!").queue();
                return;
            }
        } catch (IOException e) {
            GenericErrorEmbed.errorEmbed(event.getChannel(), e.getMessage());
        }
    }
}
