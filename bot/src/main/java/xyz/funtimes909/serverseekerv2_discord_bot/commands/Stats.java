package xyz.funtimes909.serverseekerv2_discord_bot.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import xyz.funtimes909.serverseekerv2_discord_bot.util.APIUtils;

import java.awt.*;

public class Stats {
    public static void stats(SlashCommandInteractionEvent event) {
        JsonElement response = APIUtils.query("stats");
        JsonObject object = APIUtils.getAsObject(response);

        if (object == null) return;
        int serverCount = object.get("all").getAsInt();
        int vanilla = object.get("java").getAsInt();
        int modded = aggregateServers(object.getAsJsonObject("modded"));
        int bukkit = aggregateServers(object.getAsJsonObject("plugin"));
        int proxies = aggregateServers(object.getAsJsonObject("proxies"));
        int folia = object.getAsJsonObject("multi_threaded").get("folia").getAsInt();   // There are no other multithreaded server types in the database right now, we can always change this later

        MessageEmbed embed = new EmbedBuilder()
                .setTitle("Stats")
                .setColor(new Color(0, 255, 0))
                .setAuthor("ServerSeekerV2", "https://discord.gg/WEErxAP8kz", "https://funtimes909.xyz/assets/images/serverseekerv2-icon-cropped.png")
                .setFooter("Funtimes909", "https://funtimes909.xyz/avatar-gif")
                .addField("**Unique Servers Found**", String.valueOf(serverCount), false)
                .addField("**Vanilla**", String.valueOf(vanilla), false)
                .addField("**Forge-based**", String.valueOf(modded), false)
                .addField("**Bukkit-based**", String.valueOf(bukkit), false)
                .addField("**Folia**", String.valueOf(folia), false)
                .addField("**Proxy**", String.valueOf(proxies), false)
                .build();

        MessageEditData data = new MessageEditBuilder()
                .setEmbeds(embed)
                .setContent(":white_check_mark: Success!")
                .build();

        event.getHook().editOriginal(data).queue();
    }

    private static int aggregateServers(JsonObject obj) {
        return obj.entrySet()
                .stream()
                .mapToInt(entry -> entry.getValue().getAsInt())
                .sum();
    }
}