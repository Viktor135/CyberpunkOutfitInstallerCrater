package de.vsc.coi.config;

import static de.vsc.coi.config.Config.config;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.util.VisibleForTesting;

import de.vsc.coi.utils.DirectoryUtils;

public class Workspace {

    private static final Logger LOGGER = LogManager.getLogger(Workspace.class);

    private static File workspaceDir;

    private Workspace() {
    }

    @VisibleForTesting
    public static void init(final File workspace) {
        workspaceDir = workspace;
    }

    public static File dir() {
        return workspaceDir;
    }

    public static String relativize(final File f) {
        return workspaceDir.toPath().relativize(f.toPath()).toString();
    }

    public static boolean isInitialised() {
        return workspaceDir != null;
    }

    public static void checkIfInitialised() {
        if (workspaceDir == null) {
            LOGGER.error("The Workspace is not initialised, but it should be.");
            throw new IllegalStateException("The Workspace is not initialised, but it should be.");
        }
    }

    public static String name() {
        return workspaceDir.getName();
    }

    public static String modName() {
        return name();
    }

    public static void init() {
        final File workspace;
        if (config().getWorkspacePath() != null) {
            workspace = new File(config().getWorkspacePath());
        } else {
            final File selection = DirectoryUtils.chooseDirectory();
            if (selection != null) {
                workspace = selection;
            } else {
                throw new IllegalStateException("Canceled! No directory was chosen.");
            }
        }
        LOGGER.info("Selected workspace: " + workspace.getPath());
        if (!workspace.exists() || !workspace.isDirectory()) {
            throw new IllegalStateException(
                    "The workspace (" + workspace.getPath() + ") has to be an existing directory.");
        }
        Workspace.init(workspace);
    }

}
