package io.containx.marathon.plugin.auth.type;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LDAPConfig {

    @JsonProperty
    private String server;

    @JsonProperty
    private String domain;

    @JsonProperty
    private boolean useSSL;

    @JsonProperty
    private boolean openLdapCompatible;

    public LDAPConfig() {}

    public LDAPConfig(String server, String domain, boolean useSSL, boolean openLdapCompatible) {
        this.server             = server;
        this.domain             = domain;
        this.useSSL             = useSSL;
        this.openLdapCompatible = openLdapCompatible;
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

    public boolean getUseSSL(){
        return useSSL;
    }

    public void setUseSSL(boolean useSSL){
        this.useSSL = useSSL;
    }

    public boolean getOpenLdapCompatible(){
        return this.openLdapCompatible;
    }

    public void setOpenLdapCompatible(boolean openLdapCompatible){
        this.openLdapCompatible = openLdapCompatible;
    }

    @Override
    public String toString() {
        return "LDAPConfig{" +
            "server='" + server + '\'' +
            ", domain='" + domain + '\'' +
            '}';
    }
}
