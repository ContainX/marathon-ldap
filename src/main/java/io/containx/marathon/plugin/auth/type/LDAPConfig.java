package io.containx.marathon.plugin.auth.type;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LDAPConfig {

    @JsonProperty
    private String server;

    @JsonProperty
    private String domain;

    public LDAPConfig() {}

    public LDAPConfig(String server, String domain) {
        this.server = server;
        this.domain = domain;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String toString() {
        return "LDAPConfig{" +
            "server='" + server + '\'' +
            ", domain='" + domain + '\'' +
            '}';
    }
}
