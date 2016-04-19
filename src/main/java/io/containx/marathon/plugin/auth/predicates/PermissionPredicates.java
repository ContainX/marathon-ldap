package io.containx.marathon.plugin.auth.predicates;

import io.containx.marathon.plugin.auth.type.Action;
import io.containx.marathon.plugin.auth.type.Permission;

import java.util.function.Predicate;

public class PermissionPredicates {

    public static Predicate<Permission> matchesAction(Action action) {
        return p -> p.getType() == action.getEntityType() && (p.getAllowed().isAllWildcard() || p.getAllowed() == action.getPermType());
    }

    public static Predicate<Permission> pathContains(String path) {
        return p -> pathValue(path).startsWith(p.getPath());
    }

    private static String pathValue(String path) {
        if (path == null) {
            return "/";
        }
        return path;
    }
}


