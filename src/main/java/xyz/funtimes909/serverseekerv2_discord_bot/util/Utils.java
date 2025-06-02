package xyz.funtimes909.serverseekerv2_discord_bot.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import xyz.funtimes909.serverseekerv2_discord_bot.Main;
import xyz.funtimes909.serverseekerv2_discord_bot.types.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Utils {
    public static JsonElement query(String query) {
        System.out.println(Main.apiUrl + query);
        try (HttpClient client = HttpClient.newHttpClient()) {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Main.apiUrl + query))
                    .header("Authorization",  Main.apiToken)
                    .build();

            HttpResponse<String> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).join();
            return JsonParser.parseString(response.body());
        }
    }

    public static HttpResponse<String> post(String query) {
        System.out.println(Main.apiUrl + query);
        try (HttpClient client = HttpClient.newHttpClient()) {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Main.apiUrl + query))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Authorization", Main.apiToken)
                    .build();

            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).join();
        }
    }

    public static Server buildLargeServer(JsonObject response) {
        ArrayList<Mod> mods = new ArrayList<>();
        ArrayList<Player> players = new ArrayList<>();

        if (response.get("players") != null) {
            JsonArray playersList = response.get("players").getAsJsonArray();
            if (!playersList.isEmpty()) {
                playersList.forEach(e -> {
                    JsonObject i = e.getAsJsonObject();
                    players.add(new Player(i.get("name").getAsString(), i.get("uuid").getAsString()));
                });
            }
        }

        if (response.get("mods") != null) {
            JsonArray modsList = response.get("mods").getAsJsonArray();
            if (!modsList.isEmpty()) {
                modsList.forEach(e -> {
                    JsonObject i = e.getAsJsonObject();
                    mods.add(new Mod(i.get("id").getAsString(), i.get("version").getAsString()));
                });
            }
        }

        return new Server.Builder()
                // Address and port will never be null
                .setAddress(response.get("address").getAsString())
                .setPort(response.get("port").getAsShort())

                .setSoftware(!response.get("software").isJsonNull() ? Software.fromVersionName(response.get("software").getAsString()) : null)
                .setMotd(!response.get("description_formatted").isJsonNull() ? response.get("description_formatted").getAsString() : null)
                .setIcon(!response.get("icon").isJsonNull() ? response.get("icon").getAsString() : null)
                .setVersion(!response.get("version").isJsonNull() ? response.get("version").getAsString() : null)
                .setFirstSeen(!response.get("first_seen").isJsonNull() ? response.get("first_seen").getAsLong() : 0)
                .setLastSeen(!response.get("last_seen").isJsonNull() ? response.get("last_seen").getAsLong() : 0)
                .setProtocol(!response.get("protocol").isJsonNull() ? response.get("protocol").getAsInt() : null)
                .setCountry(!response.get("country").isJsonNull() ? response.get("country").getAsString() : null)
                .setHostname(!response.get("hostname").isJsonNull() ? response.get("hostname").getAsString() : null)
                .setEnforceSecure(!response.get("enforces_secure_chat").isJsonNull() ? response.get("enforces_secure_chat").getAsBoolean() : null)
                .setPreventsReports(!response.get("prevents_chat_reports").isJsonNull() ? response.get("prevents_chat_reports").getAsBoolean() : null)
                .setOnlinePlayers(!response.get("online_players").isJsonNull() ? response.get("online_players").getAsInt() : 0)
                .setMaxPlayers(!response.get("max_players").isJsonNull() ? response.get("max_players").getAsInt() : 0)
                .setMods(mods)
                .setPlayers(players)
                .build();
    }

    public static Server buildSmallServer(JsonObject response) {
        String country = null;

        if (response.has("country")) {
            if (!response.get("country").isJsonNull()) {
                country = response.get("country").getAsString();
            }
        }

        return new Server.Builder()
                .setAddress(response.get("address").getAsString())
                .setPort(response.get("port").getAsShort())
                .setCountry(country)
                .setMotd(response.get("description").isJsonNull() ? response.get("description").getAsString() : null)
                .setLastSeen(response.get("last_seen").isJsonNull() ? response.get("last_seen").getAsLong() : 0)
                .setOnlinePlayers(response.get("online_players").isJsonNull() ? response.get("online_players").getAsInt() : 0)
                .setVersion(response.get("version").isJsonNull() ? response.get("version").getAsString() : null)
                .build();
    }

    public static Server buildServerFromPing(String address, short port, JsonObject parsedJson) {
        try {
            String version = null;
            Integer protocol = null;
            Integer fmlNetworkVersion = null;
            Integer maxPlayers = null;
            Integer onlinePlayers = null;
            Software type = null;
            StringBuilder motd = new StringBuilder();
            List<Player> playerList = new ArrayList<>();
            List<Mod> modsList = new ArrayList<>();
            long timestamp = System.currentTimeMillis() / 1000;

            if (parsedJson.has("version")) {
                Version versionType = getServerType(parsedJson);

                type = versionType.type();
                version = versionType.version();
                protocol = versionType.protocol();
            }

            // Description can be either an object or a string
            if (parsedJson.has("description")) {
                if (parsedJson.get("description").isJsonObject()) {
                    buildMOTD(parsedJson.get("description").getAsJsonObject(), 10, motd);
                } else {
                    motd.append(parsedJson.get("description").getAsString());
                }
            }

            // Handle Forge servers
            if (parsedJson.has("forgeData")) {
                fmlNetworkVersion = parsedJson.get("forgeData").getAsJsonObject().get("fmlNetworkVersion").getAsInt();
                if (parsedJson.get("forgeData").getAsJsonObject().has("mods")) {
                    for (JsonElement mod : parsedJson.get("forgeData").getAsJsonObject().get("mods").getAsJsonArray().asList()) {
                        modsList.add(new Mod(
                                mod.getAsJsonObject().get("modId").getAsString(),
                                mod.getAsJsonObject().get("modmarker").getAsString()
                        ));
                    }
                }
            }

            // Handle players
            if (parsedJson.has("players")) {
                maxPlayers = parsedJson.get("players").getAsJsonObject().get("max").getAsInt();
                onlinePlayers = parsedJson.get("players").getAsJsonObject().get("online").getAsInt();
                if (parsedJson.get("players").getAsJsonObject().has("sample")) {
                    for (JsonElement playerJson : parsedJson.get("players").getAsJsonObject().get("sample").getAsJsonArray().asList()) {
                        playerList.add(new Player(
                                playerJson.getAsJsonObject().get("name").getAsString(),
                                playerJson.getAsJsonObject().get("id").getAsString()
                        ));
                    }
                }
            }

            // Build server
            Server.Builder server = new Server.Builder()
                    .setAddress(address)
                    .setPort(port)
                    .setSoftware(type)
                    .setFirstSeen(timestamp)
                    .setLastSeen(timestamp)
                    .setVersion(version)
                    .setProtocol(protocol)
                    .setFmlNetworkVersion(fmlNetworkVersion)
                    .setMotd(motd.toString())
                    .setTimesSeen(1)
                    .setIcon(parsedJson.has("favicon") ? parsedJson.get("favicon").getAsString() : null)
                    .setPreventsReports(parsedJson.has("preventsChatReports") ? parsedJson.get("preventsChatReports").getAsBoolean() : null)
                    .setEnforceSecure(parsedJson.has("enforcesSecureChat") ? parsedJson.get("enforcesSecureChat").getAsBoolean() : null)
                    .setMaxPlayers(maxPlayers)
                    .setOnlinePlayers(onlinePlayers)
                    .setPlayers(playerList)
                    .setMods(modsList);

            return server.build();
        } catch (Exception ignored) {
            return null;
        }
    }

    public static Version getServerType(JsonObject parsedJson) {
        JsonObject object = parsedJson.get("version").getAsJsonObject();
        String version = object.get("name").getAsString();
        int protocol = object.get("protocol").getAsInt();
        Software type = Software.JAVA;

        if (parsedJson.has("isModded")) {
            type = Software.NEOFORGE;
            return new Version(version, protocol, type);
        } else if (parsedJson.has("forgeData")) {
            type = Software.LEXFORGE;
            return new Version(version, protocol, type);
        }

        if (!Character.isDigit(version.charAt(0))) {
            type = switch (version.split(" ")[0]) {
                case "Paper" -> Software.PAPER;
                case "Velocity" -> Software.VELOCITY;
                case "BungeeCord" -> Software.BUNGEECORD;
                case "Spigot" -> Software.SPIGOT;
                case "CraftBukkit" -> Software.BUKKIT;
                case "Folia" -> Software.FOLIA;
                case "Pufferfish" -> Software.PUFFERFISH;
                case "Purpur" -> Software.PURPUR;
                case "Waterfall" -> Software.WATERFALL;
                case "Leaves" -> Software.LEAVES;
                default -> Software.JAVA;
            };
        }

        return new Version(
                version,
                protocol,
                type
        );
    }

    public static void buildMOTD(JsonElement element, int limit, StringBuilder motd) {
        if (limit == 0) return;

        if (element.isJsonObject()) {
            Map<String, JsonElement> map = element.getAsJsonObject().asMap();

            if (map.containsKey("text")) {
                if (map.containsKey("color")) {
                    if (!map.get("color").getAsString().startsWith("#")) {
                        motd.append('§').append(AnsiCodes.codes.get(map.get("color").getAsString()).c);
                    }
                }

                if (map.containsKey("bold")) {
                    motd.append("§l");
                }

                if (map.containsKey("underlined")) {
                    motd.append("§n");
                }

                motd.append(map.get("text").getAsString());
            }

            if (map.containsKey("extra")) {
                buildMOTD(map.get("extra"), limit, motd);
            }
        } else {
            for (JsonElement jsonElement : element.getAsJsonArray()) {
                if (jsonElement.isJsonPrimitive()) {
                    motd.append(jsonElement.getAsString());
                } else {
                    buildMOTD(jsonElement, limit, motd);
                }
            }
        }
    }
}