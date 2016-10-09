package io.containx.marathon.plugin.auth;

import io.containx.marathon.plugin.auth.type.Action;
import io.containx.marathon.plugin.auth.type.UserIdentity;
import io.containx.marathon.plugin.auth.util.HTTPHelper;
import mesosphere.marathon.plugin.Group;
import mesosphere.marathon.plugin.PathId;
import mesosphere.marathon.plugin.RunSpec;
import mesosphere.marathon.plugin.auth.AuthorizedAction;
import mesosphere.marathon.plugin.auth.Authorizer;
import mesosphere.marathon.plugin.auth.Identity;
import mesosphere.marathon.plugin.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LDAPAuthorizor implements Authorizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(LDAPAuthorizor.class);

    @Override
    public <Resource> boolean isAuthorized(Identity identity, AuthorizedAction<Resource> authorizedAction, Resource resource) {
        LOGGER.debug("isAuthorized: {}, action: {}, resource: {}", identity, authorizedAction, resource);

        if (identity instanceof UserIdentity) {
            UserIdentity user = (UserIdentity) identity;
            Action action = Action.byAction(authorizedAction);

            if (resource instanceof Group) {
                return isAuthorized(user, action, ((Group) resource).id());
            }
            if (resource instanceof RunSpec) {
                return isAuthorized(user, action, ((RunSpec) resource).id());
            }

            // We don't get the PathID from View Resource but prior calls ensure the RunSpec is authorized
            // in general
            if (action == Action.VIEW_RESOURCE) {
                return true;
            }
            return resource instanceof PathId && isAuthorized(user, action, (PathId) resource);
        }
        return false;
    }

    private boolean isAuthorized(UserIdentity identity, Action action, PathId path) {
        boolean authorized = identity.isAuthorized(action, path.toString());
        LOGGER.debug("IsAuthorized (private): Action :: {}, Path = {}, authorized = {}", action, path.toString(), authorized);
        return authorized;
    }

    @Override
    public void handleNotAuthorized(Identity identity, HttpResponse response) {
        HTTPHelper.applyNotAuthorizedToResponse(response);
    }
}
