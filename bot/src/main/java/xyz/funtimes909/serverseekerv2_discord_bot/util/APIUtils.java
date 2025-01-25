package xyz.funtimes909.serverseekerv2_discord_bot.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class APIUtils {
    public static JsonElement query(String query) {
        System.out.println(Main.apiUrl + query);
        try (HttpClient client = HttpClient.newHttpClient()) {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Main.apiUrl + query))
                    .header("X-Auth-Key",  Main.apiToken)
                    .build();

            HttpResponse<String> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).join();
            if (response.statusCode() == 429) return null;
            return JsonParser.parseString(response.body());

        }
    }

    public static JsonElement post(String query) {
        System.out.println(Main.apiUrl + query);
        try (HttpClient client = HttpClient.newHttpClient()) {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Main.apiUrl + query))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("X-Auth-Key",  Main.apiToken)
                    .build();

            HttpResponse<String> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).join();
            if (response.statusCode() == 429) return null;
            return JsonParser.parseString(response.body());

        }
    }

    public static JsonArray getAsArray(JsonElement response) {
        if (response == null || !response.isJsonArray()) {
            return null;
        } else {
            return response.getAsJsonArray();
        }
    }

    public static JsonObject getAsObject(JsonElement response) {
        if (response == null || !response.isJsonObject()) {
            return null;
        } else {
            return response.getAsJsonObject();
        }
    }
}