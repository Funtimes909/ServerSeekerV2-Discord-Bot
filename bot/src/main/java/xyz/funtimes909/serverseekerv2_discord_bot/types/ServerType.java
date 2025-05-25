package xyz.funtimes909.serverseekerv2_discord_bot.types;

public enum ServerType {
    JAVA("Java"),
    BEDROCK("Bedrock"),
    NEOFORGE("Neoforge"),
    LEXFORGE("Neoforge"),
    PAPER("Paper"),
    SPIGOT("Spigot"),
    BUKKIT("CraftBukkit"),
    PURPUR("Purpur"),
    FOLIA("Folia"),
    PUFFERFISH("Pufferfish"),
    VELOCITY("Velocity"),
    LEAVES("Leaves"),
    WATERFALL("Waterfall"),
    BUNGEECORD("BungeeCord"),
    LEGACY("Legacy"),
    ANY("Any");

    public final String versionName;

    ServerType(String versionName) {
        this.versionName = versionName;
    }

    public static ServerType fromVersionName(String software) {
        return switch (software) {
            case "Java" -> JAVA;
            case "Bedrock" -> BEDROCK;
            case "Neoforge" -> NEOFORGE;
            case "Paper" -> PAPER;
            case "Spigot" -> SPIGOT;
            case "Purpur" -> PURPUR;
            case "Folia" -> FOLIA;
            case "Velocity" -> VELOCITY;
            case "Leaves" -> LEAVES;
            case "Waterfall" -> WATERFALL;
            case "BungeeCord" -> BUNGEECORD;
            case "Legacy" -> LEGACY;
            case "Any" -> ANY;
            default -> null;
        };
    }
}
