package xyz.funtimes909.serverseekerv2_discord_bot.Records;

import java.util.List;

public class Server {
    private final List<Player> players;
    private final List<Mod> mods;
    private final String version;
    private final String motd;
    private final String icon;
    private final String country;
    private final String asn;
    private final String reverseDns;
    private final String organization;
    private final String address;
    private final Integer maxPlayers;
    private final Integer onlinePlayers;
    private final short port;
    private final Integer protocol;
    private final Integer fmlNetworkVersion;
    private final int timesSeen;
    private final long firstseen;
    private final long lastseen;
    private final Boolean whitelist;
    private final Boolean cracked;
    private final Boolean preventsReports;
    private final Boolean enforceSecure;

    public List<Player> getPlayers() {
        return players;
    }

    public List<Mod> getMods() {
        return mods;
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

    public String getReverseDns() {
        return reverseDns;
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

    public long getFirstseen() {
        return firstseen;
    }

    public long getLastseen() {
        return firstseen;
    }

    public Boolean getWhitelist() {
        return whitelist;
    }

    public Boolean getCracked() {
        return cracked;
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
        this.version = builder.version;
        this.motd = builder.motd;
        this.icon = builder.icon;
        this.country = builder.country;
        this.asn = builder.asn;
        this.reverseDns = builder.reverseDns;
        this.organization = builder.organization;
        this.address = builder.address;
        this.maxPlayers = builder.maxPlayers;
        this.onlinePlayers = builder.onlinePlayers;
        this.port = builder.port;
        this.protocol = builder.protocol;
        this.fmlNetworkVersion = builder.fmlNetworkVersion;
        this.timesSeen = builder.timesSeen;
        this.firstseen = builder.firstseen;
        this.lastseen = builder.lastseen;
        this.whitelist = builder.whitelist;
        this.cracked = builder.cracked;
        this.preventsReports = builder.preventsReports;
        this.enforceSecure = builder.enforceSecure;
    }

    public static class Builder {
        private List<Player> players;
        private List<Mod> mods;
        private String version;
        private String motd;
        private String icon;
        private String country;
        private String asn;
        private String reverseDns;
        private String organization;
        private String address;
        private Integer maxPlayers;
        private Integer onlinePlayers;
        private short port;
        private Integer protocol;
        private Integer fmlNetworkVersion;
        private int timesSeen;
        private long firstseen;
        private long lastseen;
        private Boolean whitelist;
        private Boolean cracked;
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

        public Builder setReverseDns(String reverseDns) {
            this.reverseDns = reverseDns;
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

        public Builder setFirstSeen(long firstseen) {
            this.firstseen = firstseen;
            return this;
        }

        public Builder setLastSeen(long lastseen) {
            this.lastseen = lastseen;
            return this;
        }

        public Builder setWhitelist(Boolean whitelist) {
            this.whitelist = whitelist;
            return this;
        }

        public Builder setCracked(Boolean cracked) {
            this.cracked = cracked;
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