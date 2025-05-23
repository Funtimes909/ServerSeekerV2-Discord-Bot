package xyz.funtimes909.serverseekerv2_discord_bot.builders;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class PlayerhistorySearchBuilder {
    public static MessageEmbed build(JsonArray array, String title) {
        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor("ServerSeekerV2", "https://discord.gg/UA5kyprunc", "https://cdn.discordapp.com/avatars/1300318661168594975/1222800bc7003f89c849e55d274b2c52?size=256")
                .setTitle("Showing player history for: " + title)
                .setFooter("Funtimes909", "https://funtimes909.xyz/avatar-gif")
                .setColor(new Color(0, 255, 0));

        for (int i = 0; i < array.size(); i++) {
            JsonObject object = array.get(i).getAsJsonObject();
            StringBuilder address = new StringBuilder(object.get("address").getAsString());
            address.setLength(16);

            // Add a field for every player
            embed.addField(i + 1 + ". ``" +
                    address + "`` ``" +
                    object.get("name").getAsString() + "``  <t:" +
                    object.get("last_seen").getAsString() + ":R>", "_ _", false);
        }

        return embed.build();
    }
}