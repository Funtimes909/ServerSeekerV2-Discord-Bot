package xyz.funtimes909.serverseekerv2_discord_bot.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class APIUtils {
    public static JsonElement api(String query) {
        System.out.println(Main.apiUrl + query);
        try (HttpClient client = HttpClient.newHttpClient()) {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Main.apiUrl + query))
                    .header("x-auth-key",  Main.apiToken)
                    .build();

            HttpResponse<String> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).join();
            if (response.statusCode() == 429) return null;
            System.out.println(response.body());
            return JsonParser.parseString(response.body());

        }
    }
}
