package io.containx.marathon.plugin.auth.type;

import com.google.common.collect.Sets;
import mesosphere.marathon.plugin.auth.AuthorizedResource;
import mesosphere.marathon.plugin.auth.Identity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static io.containx.marathon.plugin.auth.predicates.PermissionPredicates.*;

public class UserIdentity implements Identity {

    private String username;
    private String password;
    private Set<String> groups = Sets.newHashSet();
    private Set<Permission> combinedPerms = Collections.newSetFromMap(new ConcurrentHashMap<Permission, Boolean>());

    public UserIdentity() { }

    public UserIdentity(String username, Set<String> groups) {
        this(username, null, groups);
    }

    public UserIdentity(String username, String password, Set<String> groups) {
        this.username = username;
        this.password = password;
        this.groups = groups;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public Set<Permission> getCombinedPerms() {
        return combinedPerms;
    }

    public UserIdentity applyResolvePermissions(Configuration config) {
        combinedPerms.clear();
        for (String group : groups) {
            Optional<Access> access = config.getAuthorization().accessFor(group);
            if (!access.isPresent()) {
                continue;
            }

            Set<Permission> perms = access.get().getPermissions();
            if (perms != null) {
                combinedPerms.addAll(perms);
            }
        }
        return this;
    }

    public boolean isAuthorized(Action action, String path) {
        if( combinedPerms.stream().filter(matchesNoneAction(action)).anyMatch(pathContains(path))) return false;
        return combinedPerms.stream().filter(matchesAction(action)).anyMatch(pathContains(path));
    }

    public boolean isAuthorized(Action action, AuthorizedResource resource) {
        if( combinedPerms.stream().filter(matchesNoneAction(action)).anyMatch(resourceIs(resource))) return false;
        return combinedPerms.stream().filter(matchesAction(action)).anyMatch(resourceIs(resource));
    }

    public static UserIdentity keyFor(String username) {
        return new UserIdentity(username, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserIdentity that = (UserIdentity) o;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return com.google.common.base.MoreObjects.toStringHelper(this)
            .add("username", username)
            .add("groups", groups)
            .add("combinedPerms", combinedPerms)
            .toString();
    }
}
