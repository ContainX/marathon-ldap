package io.containx.marathon.plugin.auth.type;

import com.google.common.collect.Sets;

import java.util.Optional;
import java.util.Set;

public class Authorization {

    private Set<Access> access = Sets.newHashSet();

    public Set<Access> getAccess() {
        return access;
    }

    public Optional<Access> accessFor(String group) {
        return access.stream().filter(a -> a.getGroup().equalsIgnoreCase(group)).findFirst();
    }

    @Override
    public String toString() {
        return "Authorization{" +
            "access=" + access +
            '}';
    }
}
