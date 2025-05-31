package xyz.funtimes909.serverseekerv2_discord_bot;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.funtimes909.serverseekerv2_discord_bot.events.AutoCompleteBot;
import xyz.funtimes909.serverseekerv2_discord_bot.events.ButtonInteractionEventListener;
import xyz.funtimes909.serverseekerv2_discord_bot.events.SlashCommandListener;
import xyz.funtimes909.serverseekerv2_discord_bot.util.CommandRegisterer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static String exclude;
    public static String tracksFile;
    public static String token;
    public static String apiUrl;
    public static String apiToken;
    public static String ownerId;
    public static JDA client;
    public static final Logger logger = LoggerFactory.getLogger("ServerSeekerV2");

    public static void main(String[] args) {
        String configFile;

        // Set config on launch
        if (args.length == 0) {
            logger.error("Usage: java -jar serverseekerv2-discord-bot.jar --config <file>");
            return;
        } else {
            configFile = args[1];
        }

        // Parse config and login
        try {
            String content = Files.readString(Paths.get(configFile), StandardCharsets.UTF_8);
            JsonObject config = JsonParser.parseString(content).getAsJsonObject();

            // Set accordingly
            token = config.get("discord_token").getAsString();
            apiUrl = config.get("api_url").getAsString();
            apiToken = config.get("api_token").getAsString();
            exclude = config.get("masscan_exclude_file").getAsString();
            tracksFile = config.get("tracks_file").getAsString();
            ownerId = config.get("owner_id").getAsString();

            // Warn user about configs should some of them not exist
            if (token.isBlank()) throw new RuntimeException("Error! No Discord token provided!");
            if (ownerId.isBlank()) throw new RuntimeException("Error! No owner ID provided!");

            File blacklist = new File("blacklist.txt");
            File trusted = new File("trusted_users.txt");
            File tracks = new File("tracks.json");
            blacklist.createNewFile();
            trusted.createNewFile();
            tracks.createNewFile();

            // Create bot instance
            client = JDABuilder.createDefault(token)
                    .addEventListeners(new SlashCommandListener())
                    .addEventListeners(new ButtonInteractionEventListener())
                    .addEventListeners(new AutoCompleteBot())
                    .build();

            CommandRegisterer.registerCommands(client);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read config file!");
        }
    }
}