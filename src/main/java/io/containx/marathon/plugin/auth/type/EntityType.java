package io.containx.marathon.plugin.auth.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EntityType {
    APP,
    GROUP,
    RESOURCE
    ;

    @JsonCreator
    public static EntityType forValue(String type) {

        return EntityType.valueOf(type.toUpperCase());
    }

    @JsonValue
    public String value() {
        return this.name().toLowerCase();
    }
}
