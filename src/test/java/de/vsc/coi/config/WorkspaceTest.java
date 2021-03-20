package de.vsc.coi.config;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class WorkspaceTest {

    @Test
    void should_only_accept_existing_directories() {
        final String[] args = new String[] {"-w", "C:\\some\\stupid\\path\\"};
        Config.init(args);
        assertThrows(IllegalStateException.class, Workspace::init);
    }

}