package xyz.funtimes909.serverseekerv2_discord_bot.util;

public class Server {
    private final String address;
    private final String country;
    private final String version;
    private final long timestamp;
    private final short port;

    public Server(String address, String country, String version, long timestamp, short port) {
        this.address = address;
        this.country = country;
        this.version = version;
        this.timestamp = timestamp;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public String getCountry() {
        return country;
    }

    public String getVersion() {
        return version;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public short getPort() {
        return port;
    }
}
