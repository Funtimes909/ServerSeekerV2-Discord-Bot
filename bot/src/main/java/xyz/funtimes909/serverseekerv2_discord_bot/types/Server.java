package xyz.funtimes909.serverseekerv2_discord_bot.types;

import java.util.List;

public class Server {
    private final List<Player> players;
    private final List<Mod> mods;
    private final Software software;
    private final String version;
    private final String motd;
    private final String icon;
    private final String country;
    private final String asn;
    private final String hostname;
    private final String organization;
    private final String address;
    private final Integer maxPlayers;
    private final Integer onlinePlayers;
    private final short port;
    private final Integer protocol;
    private final Integer fmlNetworkVersion;
    private final int timesSeen;
    private final long firstSeen;
    private final long lastSeen;
    private final Boolean preventsReports;
    private final Boolean enforceSecure;

    public List<Player> getPlayers() {
        return players;
    }

    public List<Mod> getMods() {
        return mods;
    }

    public Software getSoftware() {
        return software;
    }

    public String getVersion() {
        return version;
    }

    public String getMotd() {
        return motd;
    }

    public String getIcon() {
        return icon;
    }

    public String getCountry() {
        return country;
    }

    public String getAsn() {
        return asn;
    }

    public String getHostname() {
        return hostname;
    }

    public String getOrganization() {
        return organization;
    }

    public String getAddress() {
        return address;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public Integer getOnlinePlayers() {
        return onlinePlayers;
    }

    public short getPort() {
        return port;
    }

    public Integer getProtocol() {
        return protocol;
    }

    public Integer getFmlNetworkVersion() {
        return fmlNetworkVersion;
    }

    public int getTimesSeen() {
        return timesSeen;
    }

    public long getFirstSeen() {
        return firstSeen;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public Boolean getPreventsReports() {
        return preventsReports;
    }

    public Boolean getEnforceSecure() {
        return enforceSecure;
    }

    private Server(Builder builder) {
        this.players = builder.players;
        this.mods = builder.mods;
        this.software = builder.software;
        this.version = builder.version;
        this.motd = builder.motd;
        this.icon = builder.icon;
        this.country = builder.country;
        this.asn = builder.asn;
        this.hostname = builder.hostname;
        this.organization = builder.organization;
        this.address = builder.address;
        this.maxPlayers = builder.maxPlayers;
        this.onlinePlayers = builder.onlinePlayers;
        this.port = builder.port;
        this.protocol = builder.protocol;
        this.fmlNetworkVersion = builder.fmlNetworkVersion;
        this.timesSeen = builder.timesSeen;
        this.firstSeen = builder.firstSeen;
        this.lastSeen = builder.lastSeen;
        this.preventsReports = builder.preventsReports;
        this.enforceSecure = builder.enforceSecure;
    }

    public static class Builder {
        private List<Player> players;
        private List<Mod> mods;
        private Software software;
        private String version;
        private String motd;
        private String icon;
        private String country;
        private String asn;
        private String hostname;
        private String organization;
        private String address;
        private Integer maxPlayers;
        private Integer onlinePlayers;
        private short port;
        private Integer protocol;
        private Integer fmlNetworkVersion;
        private int timesSeen;
        private long firstSeen;
        private long lastSeen;
        private Boolean preventsReports;
        private Boolean enforceSecure;

        // Setters
        public Builder setPlayers(List<Player> players) {
            this.players = players;
            return this;
        }

        public Builder setMods(List<Mod> mods) {
            this.mods = mods;
            return this;
        }

        public Builder setSoftware(Software software) {
            this.software = software;
            return this;
        }

        public Builder setVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder setMotd(String motd) {
            this.motd = motd;
            return this;
        }

        public Builder setIcon(String icon) {
            this.icon = icon;
            return this;
        }

        public Builder setCountry(String country) {
            this.country = country;
            return this;
        }

        public Builder setAsn(String asn) {
            this.asn = asn;
            return this;
        }

        public Builder setHostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        public Builder setOrganization(String organization) {
            this.organization = organization;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setMaxPlayers(Integer maxPlayers) {
            this.maxPlayers = maxPlayers;
            return this;
        }

        public Builder setOnlinePlayers(Integer onlinePlayers) {
            this.onlinePlayers = onlinePlayers;
            return this;
        }

        public Builder setPort(short port) {
            this.port = port;
            return this;
        }

        public Builder setProtocol(Integer protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder setFmlNetworkVersion(Integer fmlNetworkVersion) {
            this.fmlNetworkVersion = fmlNetworkVersion;
            return this;
        }

        public Builder setTimesSeen(Integer timesSeen) {
            this.timesSeen = timesSeen;
            return this;
        }

        public Builder setFirstSeen(long firstSeen) {
            this.firstSeen = firstSeen;
            return this;
        }

        public Builder setLastSeen(long lastSeen) {
            this.lastSeen = lastSeen;
            return this;
        }

        public Builder setPreventsReports(Boolean preventsReports) {
            this.preventsReports = preventsReports;
            return this;
        }

        public Builder setEnforceSecure(Boolean enforceSecure) {
            this.enforceSecure = enforceSecure;
            return this;
        }

        public Server build() {
            return new Server(this);
        }
    }
}