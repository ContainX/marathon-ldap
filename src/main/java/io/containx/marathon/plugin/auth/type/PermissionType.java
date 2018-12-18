package io.containx.marathon.plugin.auth.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PermissionType {
    CREATE,
    UPDATE,
    DELETE,
    VIEW,
    ALL,
    NONE
    ;

    @JsonCreator
    public static PermissionType forValue(String type) {
        if (type != null && (type.equals("*") || type.toUpperCase().equals("ALL"))) {
            return ALL;
        }
        if (type == null || type.equals("") || type.toUpperCase().equals("NONE")) {
            return NONE;
        }
        return PermissionType.valueOf(type.toUpperCase());
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

    public boolean isDenied() {
        return this == NONE;
    }

}
