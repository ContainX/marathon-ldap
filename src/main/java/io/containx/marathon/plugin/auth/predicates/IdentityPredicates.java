package io.containx.marathon.plugin.auth.predicates;

import io.containx.marathon.plugin.auth.type.UserIdentity;

import java.util.function.Predicate;

public class IdentityPredicates {

    public static Predicate<UserIdentity> matchByName(String username) {
        return u -> u.getUsername().equalsIgnoreCase(username);
    }
}
