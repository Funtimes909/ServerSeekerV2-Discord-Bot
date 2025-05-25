package xyz.funtimes909.serverseekerv2_discord_bot.builders;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class PlayerhistorySearchBuilder {
    public static MessageEmbed build(JsonArray array, String title) {
        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor("ServerSeekerV2", "https://discord.gg/UA5kyprunc", "https://cdn.discordapp.com/app-icons/1375333922765930556/bcc3069c7e9fdb44107faeb74477127d.png?size=256")
                .setFooter("Funtimes909", "https://funtimes909.xyz/assets/images/floppa.png")
                .setTitle("Showing player history for: " + title)
                .setColor(new Color(0, 255, 0));

        for (int i = 0; i < array.size(); i++) {
            if (i > 9) break;
            JsonObject object = array.get(i).getAsJsonObject();

            // Add a field for every player
            String field = String.format("%d. ``%s:%d`` ``%s`` <t:%d:R>",
                    (i + 1),
                    object.get("address").getAsString(),
                    object.get("port").getAsInt(),
                    object.get("name").getAsString(),
                    object.get("last_seen").getAsInt()
            );
            embed.addField(field, "_ _", false);
        }

        return embed.build();
    }
}