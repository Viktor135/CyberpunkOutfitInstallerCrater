package de.vsc.coi;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.tngtech.configbuilder.exception.ValidatorException;

class WorkspaceTest {

    @TempDir
    File dir;

    @Test
    void should_correctly_read_the_workspace_from_args() {
        final File workspace = new File(dir, "CP_Workspace");
        workspace.mkdirs();
        final String[] args = new String[] {"-w", workspace.getPath()};
        Workspace.init(args);

        assertThat(Workspace.dir().getPath(), is(workspace.getPath()));
    }

    @Test
    void should_only_accept_existing_directories(){
        final String[] args = new String[] {"-w", "C:\\some\\stupid\\path\\"};
        assertThrows(IllegalStateException.class,()->Workspace.init(args));
    }

    @Test
    void should_not_accept_blank_workspace(){
        final String[] args = new String[] {"-w", "   "};
        assertThrows(ValidatorException.class,()->Workspace.init(args));
    }

}