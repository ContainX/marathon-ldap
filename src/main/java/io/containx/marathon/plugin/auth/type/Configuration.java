package io.containx.marathon.plugin.auth.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

public class Configuration {

    @JsonIgnore
    private volatile Map<String, UserIdentity> cache = null;

    @JsonProperty
    private LDAPConfig ldap;

    @JsonProperty("users")
    private Set<UserIdentity> staticUsers = Sets.newHashSet();

    private Authorization authorization;

    public LDAPConfig getLdap() {
        return ldap;
    }

    public void setLdap(LDAPConfig ldap) {
        this.ldap = ldap;
    }

    public Set<UserIdentity> getStaticUsers() {
        return staticUsers;
    }

    public void setStaticUsers(Set<UserIdentity> staticUsers) {
        this.staticUsers = staticUsers;
    }

    public Authorization getAuthorization() {
        return authorization;
    }

    public void setAuthorization(Authorization authorization) {
        this.authorization = authorization;
    }

    public boolean hasStaticUser(String username) {
        return userCache().containsKey(username);
    }

    public UserIdentity getStaticUser(String username) {
        return userCache().get(username);
    }

    private Map<String, UserIdentity> userCache() {
        if (cache == null) {
            synchronized (this) {
                if (cache == null) {
                    Map<String, UserIdentity> uc = Maps.newHashMap();
                    if (staticUsers != null) {
                        for (UserIdentity u : staticUsers) {
                            uc.put(u.getUsername(), u.applyResolvePermissions(this));
                        }
                    }
                    cache = uc;
                }
            }
        }
        return cache;
    }

    @Override
    public String toString() {
        return "Configuration{" +
            "ldap=" + ldap +
            ", staticUsers=" + staticUsers +
            ", authorization=" + authorization +
            '}';
    }
}
