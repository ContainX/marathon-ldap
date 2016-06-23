package io.containx.marathon.plugin.auth.type;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LDAPConfig {

    @JsonProperty
    private String url = "ldap://localhost:389";

    @JsonProperty
    private String base = "dc=example,dc=com";

    @JsonProperty
    private String dn = "uid={username}";

    @JsonProperty
    private String userSearch = "(uid={username})";

    @JsonProperty(required = false)
    private String userSubTree = "ou=People";

    @JsonProperty(required = false)
    private String groupSearch = null;

    @JsonProperty(required = false)
    private String groupSubTree = "ou=Group";


    public LDAPConfig() {}

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String search) {
        this.dn = search;
    }

    public String getUserSearch() {
        return userSearch;
    }

    public void setUserSearch(String userSearch) {
        this.userSearch = userSearch;
    }

    public String getGroupSubTree() {
        return groupSubTree;
    }

    public void setGroupSubTree(String groupSubTree) {
        this.groupSubTree = groupSubTree;
    }

    public String getGroupSearch() {
        return groupSearch;
    }

    public void setGroupSearch(String groupSearch) {
        this.groupSearch = groupSearch;
    }

    public String getUserSubTree() {
        return userSubTree;
    }

    public void setUserSubTree(String userSubTree) {
        this.userSubTree = userSubTree;
    }

    @Override
    public String toString() {
        return "LDAPConfig{" +
                "url='" + url + '\'' +
                ", base='" + base + '\'' +
                ", dn='" + dn + '\'' +
                ", userSearch='" + userSearch + '\'' +
                ", userSubTree='" + userSubTree + '\'' +
                ", groupSearch='" + groupSearch + '\'' +
                ", groupSubTree='" + groupSubTree + '\'' +
                '}';
    }
}
