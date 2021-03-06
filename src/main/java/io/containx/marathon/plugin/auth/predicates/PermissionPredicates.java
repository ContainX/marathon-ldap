package io.containx.marathon.plugin.auth.predicates;

import io.containx.marathon.plugin.auth.type.Action;
import io.containx.marathon.plugin.auth.type.Permission;
import mesosphere.marathon.plugin.auth.AuthorizedResource;

import java.util.function.Predicate;

public class PermissionPredicates {

    public static Predicate<Permission> matchesNoneAction(Action action) {
        return p -> p.getType() == action.getEntityType() && p.getAllowed().isDenied();
    }

    public static Predicate<Permission> matchesAction(Action action) {
        return p -> p.getType() == action.getEntityType() &&
                (p.getAllowed().isAllWildcard() || p.getAllowed() == action.getPermType());
    }

    public static Predicate<Permission> pathContains(String path) {
        return p -> {
            String regpath = p.getPath();
            boolean match = pathValue(path).matches(regpath);
            if (regpath.endsWith("/")) {
                match = match || pathValue(path).matches(regpath + ".*");
            } else {
                match = match || pathValue(path).matches(regpath + "/.*");
            }
            return match;
        };
    }

    public static Predicate<Permission> resourceIs(AuthorizedResource resource) {
        return p -> p.getPath().equals("/") || resource.toString().matches(p.getPath());
    }

    private static String pathValue(String path) {
        if (path == null) {
            return "/";
        }
        return path;
    }
}


