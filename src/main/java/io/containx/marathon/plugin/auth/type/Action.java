package io.containx.marathon.plugin.auth.type;

import com.google.common.base.MoreObjects;
import mesosphere.marathon.plugin.auth.*;

/**
 * Enumeration for handling AuthorizedActions
 */
public enum Action {
    // App Actions
    CREATE_APP(CreateRunSpec$.MODULE$, PermissionType.CREATE, EntityType.APP),
    UPDATE_APP(UpdateRunSpec$.MODULE$, PermissionType.UPDATE, EntityType.APP),
    DELETE_APP(DeleteRunSpec$.MODULE$, PermissionType.DELETE, EntityType.APP),
    VIEW_APP(ViewRunSpec$.MODULE$, PermissionType.VIEW, EntityType.APP),
    VIEW_RESOURCE(ViewResource$.MODULE$, PermissionType.VIEW, EntityType.APP),
    UPDATE_RESOURCE(UpdateResource$.MODULE$, PermissionType.UPDATE, EntityType.APP),

    // Group Actions
    CREATE_GROUP(CreateGroup$.MODULE$, PermissionType.CREATE, EntityType.GROUP),
    UPDATE_GROUP(UpdateGroup$.MODULE$, PermissionType.UPDATE, EntityType.GROUP),
    DELETE_GROUP(DeleteGroup$.MODULE$, PermissionType.DELETE, EntityType.GROUP),
    VIEW_GROUP(ViewGroup$.MODULE$, PermissionType.VIEW, EntityType.GROUP),
    ;
    private final AuthorizedAction<?> action;
    private final EntityType entityType;
    private final PermissionType permType;

    Action(AuthorizedAction<?> action, PermissionType permType, EntityType entityType) {
        this.action = action;
        this.permType = permType;
        this.entityType = entityType;
    }

    public static Action byAction(AuthorizedAction<?> action) {
        for (Action a : values()) {
            if (a.action.equals(action)) {
                return a;
            }
        }
        throw new IllegalArgumentException("Unknown Action: " + action);
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public PermissionType getPermType() {
        return permType;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("action", action)
                .add("entityType", entityType)
                .add("permType", permType)
                .toString();
    }
}
