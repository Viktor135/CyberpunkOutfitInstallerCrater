package de.vsc.coi;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.vsc.coi.crawlers.FileCrawler;

public class Application {

    private static final Logger LOGGER = LogManager.getLogger(Application.class);

    public static void main(final String... args) {
        final Config config = Config.init(args);

        final File workspace;
        if (config.getWorkspacePath() != null) {
            workspace = new File(config.getWorkspacePath());
        } else {
            final File selection = chooseFile();
            if (selection != null) {
                workspace = selection;
            } else {
                error("Canceled! No directory was chosen.");
                return;
            }
        }
        LOGGER.info("Selected workspace: " + workspace.getPath());
        if (!workspace.exists() || !workspace.isDirectory()) {
            error("The workspace (" + workspace.getPath() + ") has to be an existing directory.");
            return;
        }
        Workspace.init(workspace);

        final File out = new File(workspace, "fomod");
        if (!out.exists()) {
            LOGGER.info("Creating output directory: " + out.getPath());
            if (!out.mkdirs()) {
                error("Could not create output directory: " + out.getPath());
                return;
            }
        }
        if (!out.canWrite()) {
            error("The CPOIC have to have write access in the workspace!");
            return;
        }
        try {
            new FileCrawler().crawl().marshal(out);
            LOGGER.info("Process ended successfully!");
            JOptionPane.showMessageDialog(null, "Process ended successfully!");
        } catch (final JAXBException | IOException e) {
            LOGGER.error("While marshalling:", e);
            error("Process ended with Errors! For details view the log file.");
        }
    }

    private static File chooseFile() {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Pleas select the workspace to operate on.");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    public static void error(final String errorMessage) {
        JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
        LOGGER.error(errorMessage);
    }
}
