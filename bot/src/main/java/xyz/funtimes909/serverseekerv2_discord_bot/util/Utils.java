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
import java.util.HashMap;
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
            if (response.statusCode() == 429) return null;
            return JsonParser.parseString(response.body());
        }
    }

    public static void post(String query) {
        System.out.println(Main.apiUrl + query);
        try (HttpClient client = HttpClient.newHttpClient()) {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Main.apiUrl + query))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Authorization",  Main.apiToken)
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).join();
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

    public static Server buildServerFromApiResponse(JsonObject response) {
        return new Server.Builder()
                .setAddress(response.get("address").getAsString())
                .setPort(response.get("port").getAsShort())
                .setMotd(!response.get("description_formatted").isJsonNull() ? response.get("description_formatted").getAsString() : null)
                .setIcon(!response.get("icon").isJsonNull() ? response.get("icon").getAsString() : null)
                .setVersion(!response.get("version").isJsonNull() ? response.get("version").getAsString() : null)
                .setFirstSeen(!response.get("first_seen").isJsonNull() ? response.get("first_seen").getAsLong() : 0)
                .setLastSeen(!response.get("last_seen").isJsonNull() ? response.get("last_seen").getAsLong() : 0)
                .setProtocol(!response.get("protocol").isJsonNull() ? response.get("protocol").getAsInt() : null)
                .setCountry(!response.get("country").isJsonNull() ? response.get("country").getAsString() : null)
                .setReverseDns(!response.get("hostname").isJsonNull() ? response.get("hostname").getAsString() : null)
                .setEnforceSecure(!response.get("enforces_secure_chat").isJsonNull() ? response.get("enforces_secure_chat").getAsBoolean() : null)
                .setPreventsReports(!response.get("prevents_chat_reports").isJsonNull() ? response.get("prevents_chat_reports").getAsBoolean() : null)
                .setOnlinePlayers(!response.get("online_players").isJsonNull() ? response.get("online_players").getAsInt() : 0)
                .setMaxPlayers(!response.get("max_players").isJsonNull() ? response.get("max_players").getAsInt() : 0)
                .build();
    }

    public enum AnsiCodes {
        BLACK('0', "black", 30),
        DARK_BLUE('1', "dark_blue", 34),
        DARK_GREEN('2', "dark_green", 32),
        DARK_AQUA('3', "dark_aqua", 36),
        DARK_RED('4', "dark_red", 31),
        PURPLE('5', "dark_purple", 35),
        GOLD('6', "gold", 33),
        GRAY('7', "gray", 37),
        DARK_GRAY('8', "dark_gray", 30),
        BLUE('9', "blue", 34),
        GREEN('a', "green", 32),
        AQUA('b', "aqua", 36),
        RED('c', "red", 31),
        PINK('d', "light_purple", 35),
        YELLOW('e', "yellow", 33),
        WHITE('f', "white", 37),
        BOLD('l', "bold", 1),
        UNDERLINE('n', "underline", 4),
        RESET('r', "reset", 50);

        public static final HashMap<String, AnsiCodes> codes = new HashMap<>();
        public static final HashMap<Character, AnsiCodes> colors = new HashMap<>();

        static {
            for (AnsiCodes v: AnsiCodes.values()) {
                codes.put(v.name, v);
                colors.put(v.c, v);
            }
        }

        public final char c;
        public final String name;
        public final int ansi;

        AnsiCodes(char c, String name, Integer ansi) {
            this.c = c;
            this.name = name;
            this.ansi = ansi;
        }
    }

    public static Server buildServerFromPing(String address, short port, JsonObject parsedJson) {
        try {
            String version = null;
            String asn = null;
            String country = null;
            String hostname = null;
            String organization = null;
            Integer protocol = null;
            Integer fmlNetworkVersion = null;
            Integer maxPlayers = null;
            Integer onlinePlayers = null;
            ServerType type = null;
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
                                playerJson.getAsJsonObject().get("id").getAsString(),
                                timestamp,
                                timestamp
                        ));
                    }
                }
            }

            // Build server
            Server.Builder server = new Server.Builder()
                    .setAddress(address)
                    .setPort(port)
                    .setServerType(type)
                    .setFirstSeen(timestamp)
                    .setLastSeen(timestamp)
                    .setAsn(asn)
                    .setCountry(country)
                    .setReverseDns(hostname)
                    .setOrganization(organization)
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
        ServerType type = ServerType.JAVA;

        if (parsedJson.has("isModded")) {
            type = ServerType.NEOFORGE;
            return new Version(version, protocol, type);
        } else if (parsedJson.has("forgeData")) {
            type = ServerType.LEXFORGE;
            return new Version(version, protocol, type);
        }

        if (!Character.isDigit(version.charAt(0))) {
            type = switch (version.split(" ")[0]) {
                case "Paper" -> ServerType.PAPER;
                case "Velocity" -> ServerType.VELOCITY;
                case "BungeeCord" -> ServerType.BUNGEECORD;
                case "Spigot" -> ServerType.SPIGOT;
                case "CraftBukkit" -> ServerType.BUKKIT;
                case "Folia" -> ServerType.FOLIA;
                case "Pufferfish" -> ServerType.PUFFERFISH;
                case "Purpur" -> ServerType.PURPUR;
                case "Waterfall" -> ServerType.WATERFALL;
                case "Leaves" -> ServerType.LEAVES;
                default -> ServerType.JAVA;
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