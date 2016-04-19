package io.containx.marathon.plugin.auth.type;

import java.util.Set;

public class Access {

    private String group;
    private Set<Permission> permissions;

    public Access() { }

    private Access(String group) {
        this.group = group;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public static Access keyFor(String group) {
        return new Access(group);
    }

    @Override
    public String toString() {
        return "Access{" +
            "group='" + group + '\'' +
            ", permissions=" + permissions +
            '}';
    }
}
