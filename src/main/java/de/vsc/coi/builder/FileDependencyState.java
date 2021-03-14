package de.vsc.coi.builder;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileDependencyState {
    MISSING("Missing"),
    INACTIVE("Inactive"),
    ACTIVE("Active");
    private final String value;
}
