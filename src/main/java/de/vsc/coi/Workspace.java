package de.vsc.coi;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Workspace {

    private static final Logger LOGGER = LogManager.getLogger(Workspace.class);

    private static Workspace instance;
    private static File workspaceDir;

    private Workspace(final File workspaceDir) {
        Workspace.workspaceDir = workspaceDir;
    }

    public static void init(final File workspace) {
        instance = new Workspace(workspace);
    }

    public static File dir() {
        return workspaceDir;
    }

    public static String relativize(final File f) {
        return workspaceDir.toPath().relativize(f.toPath()).toString();
    }

    public static boolean isInitialised() {
        return instance != null;
    }

    public static void checkIfInitialised() {
        if (instance == null) {
            LOGGER.error("The Relativizer is not initialised, but it should be.");
            throw new IllegalStateException("The Relativizer is not initialised, but it should be.");
        }
    }

    public static String name() {
        return workspaceDir.getName();
    }

    public static String modName() {
        return name();
    }

}
