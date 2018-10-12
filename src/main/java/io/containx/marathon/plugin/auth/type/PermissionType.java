package io.containx.marathon.plugin.auth.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PermissionType {
    CREATE,
    UPDATE,
    DELETE,
    VIEW,
    ALL
    ;

    @JsonCreator
    public static PermissionType forValue(String type) {
        if (type != null && (type.equals("*") || type.equals("ALL"))) {
            return ALL;
        }
        if (type != null) {
            return PermissionType.valueOf(type.toUpperCase());
        }
        return null;
    }

    @JsonValue
    public String value() {
        if (this == ALL) {
            return "*";
        }
        return this.name().toLowerCase();
    }

    public boolean isAllWildcard() {
        return this == ALL;
    }
}
