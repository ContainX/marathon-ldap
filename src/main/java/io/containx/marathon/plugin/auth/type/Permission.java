package io.containx.marathon.plugin.auth.type;

import com.google.common.base.MoreObjects;

import java.util.Objects;

public class Permission {

    private PermissionType allowed = PermissionType.ALL;
    private EntityType type = EntityType.APP;
    private String path = "/";

    public PermissionType getAllowed() {
        return allowed;
    }

    public void setAllowed(PermissionType allowed) {
        this.allowed = allowed;
    }

    public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return Objects.equals(allowed, that.allowed) &&
            Objects.equals(type, that.type) &&
            Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allowed, type, path);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("allowed", allowed)
            .add("type", type)
            .add("path", path)
            .toString();
    }
}
