package de.vsc.coi;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import de.vsc.coi.config.Config;
import de.vsc.coi.config.Workspace;
import de.vsc.coi.crawlers.FileCrawler;

public class Application {

    private static final Logger LOGGER = LogManager.getLogger(Application.class);

    public static void main(final String... args) {
        try {
            Config.init(args);
            Workspace.init();
        } catch (final IllegalStateException e) {
            error(e.getMessage());
        }

        final File fomod = new File(Workspace.dir(), "fomod");
        if (!fomod.exists()) {
            LOGGER.info("Creating output directory: " + fomod.getPath());
            if (!fomod.mkdirs()) {
                error("Could not create output directory: " + fomod.getPath());
                return;
            }
        }
        if (!fomod.canWrite()) {
            error("The CPOIC have to have write access in the workspace!");
            return;
        }
        try {
            new FileCrawler().crawl().marshal(fomod);
            LOGGER.info("Process ended successfully!");
            JOptionPane.showMessageDialog(null, "Process ended successfully!");
        } catch (final JAXBException | IOException | URISyntaxException | SAXException e) {
            LOGGER.error("While marshalling:", e);
            error("Process ended with Errors! For details view the log file.");
        }
    }

    public static void error(final String errorMessage) {
        JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
        LOGGER.error(errorMessage);
    }
}
