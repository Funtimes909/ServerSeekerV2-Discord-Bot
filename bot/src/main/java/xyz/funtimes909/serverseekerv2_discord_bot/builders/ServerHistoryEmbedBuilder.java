package xyz.funtimes909.serverseekerv2_discord_bot.builders;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import xyz.funtimes909.serverseekerv2_discord_bot.util.APIUtils;

import java.awt.*;

public class ServerHistoryEmbedBuilder {
    public static MessageEmbed build(String address) {
        JsonElement response = APIUtils.query("history?address=" + address);
        if (response == null || !response.isJsonArray()) return null;

        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor("ServerSeekerV2", "https://discord.gg/wYTe2ZwD7g", "https://cdn.discordapp.com/app-icons/1300318661168594975/cb3825c45b033454cf027a878e96196c.png?size=512")
                .setTitle("Showing player history for: " + address)
                .setFooter("Funtimes909", "https://funtimes909.xyz/avatar-gif")
                .setColor(new Color(0, 255, 0));

        for (JsonElement element : response.getAsJsonArray()) {
            JsonObject object = element.getAsJsonObject();

            StringBuilder ip = new StringBuilder(object.get("playername"). getAsString());
            ip.setLength(16);

            String entry = "**``" +
                    ip +
                    "``** <t:" +
                    object.get("lastseen").getAsString() +
                    ":R>\n";

            embed.addField("_ _", entry, false);
        }

        return embed.build();
    }
}
