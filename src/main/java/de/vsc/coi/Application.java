package de.vsc.coi;

import static de.vsc.coi.config.Config.config;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import de.vsc.coi.config.Config;
import de.vsc.coi.config.Workspace;
import de.vsc.coi.crawlers.FileCrawler;
import fomod.ModuleConfiguration;

public class Application {

    private static final Logger LOGGER = LogManager.getLogger(Application.class);
    private final Gui gui = new Gui();

    public static void main(final String... args) {
        final Application application = new Application();
        application.start(args);
        application.finalise();
    }

    public void finalise() {
        gui.close();
    }

    public void start(final String... args) {
        try {
            Config.init(args);
            gui.init();
            gui.setStatus("Initialising...");
            Workspace.init();
        } catch (final IllegalStateException e) {
            error(e.getMessage());
            return;
        } catch (final IOException | URISyntaxException e) {
            LOGGER.error("While starting GUI.", e);
            return;
        }
        gui.setStatus("Determine workspace...");
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
        gui.setStatus("Creating installer...");

        final ModuleConfiguration configuration = new FileCrawler().crawl();
        final ConfigMarshaller marshaller = new ConfigMarshaller(configuration, fomod);
        try {
            LOGGER.info("Marshalling...");
            marshaller.marshal();
        } catch (final JAXBException | IOException e) {
            error("Process ended with Errors! For details view the log file.");
            LOGGER.error("While marshalling:", e);
            return;
        }
        try {
            LOGGER.info("Validating...");
            marshaller.validate();
        } catch (final JAXBException | URISyntaxException | SAXException e) {
            error("Validation of created Installer ended with errors! Most likely, this is an error in the CpOIC. "
                    + "Pleas inform the mod author, then the bug will be fixed. For details view the log file.");
            LOGGER.error("While validation:", e);
            return;
        }
        if (config().isGenerateReport()) {
            try {
                LOGGER.info("Generating report...");
                new Reporting().generateReport(marshaller.getOutputFile()).writeToFile();
            } catch (final IOException e) {
                LOGGER.error("While generating report.", e);
            }
        }
        gui.setStatus("Finished!");
        gui.displayMessage("Process ended successfully!");
        LOGGER.info("Process ended successfully!");
    }

    public void error(final String errorMessage) {
        gui.displayError(errorMessage);
        LOGGER.error(errorMessage);
    }
}
