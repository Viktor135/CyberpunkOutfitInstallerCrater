package de.vsc.coi;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.util.VisibleForTesting;

import com.tngtech.configbuilder.ConfigBuilder;
import com.tngtech.configbuilder.annotation.validation.Validation;
import com.tngtech.configbuilder.annotation.valueextractor.CommandLineValue;

import de.vsc.coi.utils.DirectoryUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class Workspace {

    private static final Logger LOGGER = LogManager.getLogger(Workspace.class);

    private static File workspaceDir;

    private Workspace() {
    }

    @VisibleForTesting
    public static void init(final File workspace) {
        workspaceDir = workspace;
    }

    public static void init(final String... args) {
        Initializer.init(args);
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

    @EqualsAndHashCode
    @ToString
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private static class Initializer {

        @CommandLineValue(shortOpt = "w", longOpt = "workspace", hasArg = true)
        protected String workspacePath;

        public static void init(final String... args) {
            final Initializer initializer = ConfigBuilder.on(Initializer.class)
                    .withCommandLineArgs(args)
                    .build();

            final File workspace;
            if (initializer.workspacePath != null) {
                workspace = new File(initializer.workspacePath);
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

        @Validation
        protected void validate() {
            if (workspacePath != null) {
                if (StringUtils.isBlank(workspacePath)) {
                    throw new IllegalStateException("The configured workspace has to be not not blank.");
                }
            }
        }

    }

}
