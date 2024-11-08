package xyz.funtimes909.serverseekerv2_discord_bot;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.funtimes909.serverseekerv2_discord_bot.commands.Register;
import xyz.funtimes909.serverseekerv2_discord_bot.events.ButtonInteractionEventListener;
import xyz.funtimes909.serverseekerv2_discord_bot.events.SlashCommandListener;
import xyz.funtimes909.serverseekerv2_discord_bot.util.DatabaseConnectionPool;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static String exclude;
    public static String token;
    public static String username;
    public static String password;
    public static String url;
    public static String ownerId;
     public static final Logger logger = LoggerFactory.getLogger("ServerSeekerV2");

    public static void main(String[] args) {
        String configFile;

        // Set config on launch
        if (args.length == 0) {
            logger.error("Usage: java -jar serverseekerv2.jar --config <file>");
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
            username = config.get("postgres_user").getAsString();
            password = config.get("postgres_password").getAsString();
            url = config.get("postgres_url").getAsString();
            exclude = config.get("masscan_exclude_file").getAsString();
            ownerId = config.get("owner_id").getAsString();

            DatabaseConnectionPool.initPool();

            // Create bot instance
            JDA client = JDABuilder.createDefault(token)
                    .addEventListeners(new SlashCommandListener())
                    .addEventListeners(new ButtonInteractionEventListener())
                    .build();

            // Register commands
            Register.registerCommands(client);
        } catch (IOException e) {
            System.out.println("Failed to read config file: " + e.getMessage());
        }
    }
}