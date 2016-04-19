package io.containx.marathon.plugin.auth;

import io.containx.marathon.plugin.auth.type.Action;
import io.containx.marathon.plugin.auth.type.UserIdentity;
import io.containx.marathon.plugin.auth.util.HTTPHelper;
import mesosphere.marathon.plugin.AppDefinition;
import mesosphere.marathon.plugin.Group;
import mesosphere.marathon.plugin.PathId;
import mesosphere.marathon.plugin.auth.AuthorizedAction;
import mesosphere.marathon.plugin.auth.Authorizer;
import mesosphere.marathon.plugin.auth.Identity;
import mesosphere.marathon.plugin.http.HttpResponse;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LDAPAuthorizor implements Authorizer {

    private static final Logger LOG = Logger.getLogger(LDAPAuthorizor.class.getName());

    @Override
    public <Resource> boolean isAuthorized(Identity identity, AuthorizedAction<Resource> authorizedAction, Resource resource) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("isAuthorized: " + identity + ", action: " + authorizedAction + ", resource: " + resource);
        }

        if (identity instanceof UserIdentity) {
            UserIdentity user = (UserIdentity) identity;
            Action action = Action.byAction(authorizedAction);
            if (resource instanceof Group) {
                return isAuthorized(user, action, ((Group) resource).id());
            }
            if (resource instanceof AppDefinition) {
                return isAuthorized(user, action, ((AppDefinition) resource).id());
            }
            return resource instanceof PathId && isAuthorized(user, action, (PathId) resource);
        }
        return false;
    }

    private boolean isAuthorized(UserIdentity identity, Action action, PathId path) {
        boolean authorized = identity.isAuthorized(action, path.toString());

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("IsAuthorized: Action :: " + action + ", Path = " + path.toString() + ", authorized :: " + authorized);
        }

        return authorized;
    }

    @Override
    public void handleNotAuthorized(Identity identity, HttpResponse response) {
        HTTPHelper.applyNotAuthorizedToResponse(response);
    }
}
