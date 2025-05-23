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
}
