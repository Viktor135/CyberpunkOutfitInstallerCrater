package de.vsc.coi;

import static de.vsc.coi.config.Config.config;
import static de.vsc.coi.utils.DirectoryUtils.chooseDirectory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ForkJoinPool;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import com.tngtech.configbuilder.exception.ConfigBuilderException;

import de.vsc.coi.config.Config;
import de.vsc.coi.config.Workspace;
import de.vsc.coi.crawlers.FileCrawler;
import de.vsc.coi.gui.Gui;
import de.vsc.coi.marshaller.ConfigMarshaller;
import de.vsc.coi.marshaller.InfoMarshaller;
import de.vsc.coi.zip.ZipAdapter.ZipException;
import de.vsc.coi.zip.Zipper;
import fomod.ModuleConfiguration;

public class Application implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(Application.class);

    public final String[] args;
    private final Queue<Task> tasks;
    private final Queue<Task> errorTasks;
    private final Gui gui;
    private boolean errorState = false;
    private boolean awaitCompletion = false;
    private Info modInfo;
    private ConfigMarshaller configMarshaller;
    private InfoMarshaller infoMarshaller;

    public Application(final String[] args) {
        this.args = args;
        this.tasks = new ArrayDeque<>();
        this.errorTasks = new ArrayDeque<>();
        this.gui = new Gui();
    }

    public static void main(final String... args) {
        new Application(args).run();
    }

    public void run() {
        //@formatter:off
        tasks.addAll(List.of(
                this::initialise,
                this::determineWorkspace,
                this::editModInfo,
                // AWAIT_COMPLETION here we wait for user input
                this::saveModInfo,
                this::validateModInfo,
                this::createInstaller,
                this::validateInstaller,
                this::askForZip,
                // AWAIT_COMPLETION here we wait for user input
                this::createZip,
                this::finished));

        errorTasks.addAll(List.of(
                this::askForReport,
                this::generateReport,
                this::finishedWithErrors
        ));
        //@formatter:on
        proceed();
    }

    public void proceed() {
        ForkJoinPool.commonPool().execute(this::workOnTasks);
    }

    private void workOnTasks() {
        awaitCompletion = false;
        while (!errorState && !awaitCompletion && !tasks.isEmpty()) {
            tasks.poll().process();
        }
        // If an error occurred, we perform the error tasks.
        while (errorState && !awaitCompletion && !errorTasks.isEmpty()) {
            errorTasks.poll().process();
        }
    }

    public void initialise() {
        try {
            gui.init();
            gui.setStatus("Initialising...");
            Config.init(args);
            Workspace.init();
        } catch (final IllegalStateException | ConfigBuilderException e) {
            LOGGER.error("While Initialising:", e);
            error(e.getMessage());
        } catch (final IOException | URISyntaxException e) {
            LOGGER.error("While starting GUI.", e);
            errorState = true;
        }

    }

    public void determineWorkspace() {

        gui.setStatus("Determine workspace...");
        final File fomodFolder = new File(Workspace.dir(), "fomod");
        if (!fomodFolder.exists()) {
            LOGGER.info("Creating output directory: " + fomodFolder.getPath());
            if (!fomodFolder.mkdirs()) {
                error("Could not create output directory: " + fomodFolder.getPath());
                return;
            }
        }
        if (!fomodFolder.canWrite()) {
            error("The CPOIC have to have write access in the workspace!");
        }
        this.infoMarshaller = new InfoMarshaller(fomodFolder);
        this.configMarshaller = new ConfigMarshaller(fomodFolder);
    }

    public void editModInfo() {
        gui.setStatus("Pleas fill out the mod info.");
        try {
            if (infoMarshaller.getOutputFile().exists()) {
                modInfo = (Info) infoMarshaller.validate();
            } else {
                modInfo = new Info();
            }
            gui.showInfoPanel(modInfo).onNext(e -> this.proceed());
            awaitCompletion = true;
        } catch (final JAXBException | SAXException e) {
            error("For details view the log file.");
            LOGGER.error("While unmarshalling info.xml:", e);
        }
    }

    public void saveModInfo() {
        final Info newModInfo = gui.closeInfoPanel();

        if (newModInfo == null) { // no changes were made
            LOGGER.info("No changes in mod info.");
            return;
        }
        gui.setStatus("Saving mod info.");
        try {
            this.modInfo = newModInfo;
            infoMarshaller.marshal(this.modInfo);
        } catch (final JAXBException | IOException e) {
            error("Could not write info to file.<br> For details view the log file.");
            LOGGER.error("While marshalling info.xml:", e);
        }
    }

    public void validateModInfo() {
        try {
            infoMarshaller.validate();
        } catch (final JAXBException | SAXException e) {
            error("For details view the log file.");
            LOGGER.error("While validating info.xml:", e);
        }
    }

    public void createInstaller() {
        gui.setStatus("Creating installer...");
        final ModuleConfiguration configuration = new FileCrawler().crawl();
        try {
            LOGGER.info("Marshalling...");
            configMarshaller.marshal(configuration);
        } catch (final JAXBException | IOException e) {
            error("For details view the log file.");
            LOGGER.error("While marshalling:", e);
        }
    }

    public void validateInstaller() {
        try {
            LOGGER.info("Validating...");
            configMarshaller.validate();
        } catch (final JAXBException | SAXException e) {
            error("Validation of the created Installer ended with errors! Most likely, this is an hadError in the CpOIC. "
                    + "Pleas inform the mod author, then the bug will be fixed. For details view the log file.");
            LOGGER.error("While validation:", e);
        }
    }

    public void askForZip() {
        gui.openYesNoQuestion("Do you want to create the mod zip?", e -> this.proceed());
        this.awaitCompletion = true;
    }

    public void createZip() {
        if (gui.getQuestionAnswer()) {
            gui.setStatus("Creating Zip file...");
            try {
                final File directory = chooseDirectory("Pleas select the output folder for the zip file.");
                Zipper.zip(Workspace.dir(), directory, Workspace.modName() + " - " + modInfo.getVersion());
            } catch (final ZipException e) {
                LOGGER.error("Zipping failed.", e);
                error("Zipping failed.");
            }
        } else {
            LOGGER.info("No zip could be created.");
        }
    }

    public void finished() {
        gui.finished();
        LOGGER.info("Process ended successfully!");
    }

    public void askForReport() {
        if (!config().isGenerateReport()) {
            gui.openYesNoQuestion("Do you want to create an report?", e -> this.proceed());
            this.awaitCompletion = true;
        }
    }

    public void generateReport() {
        if (config().isGenerateReport() || gui.getQuestionAnswer()) {
            try {
                LOGGER.info("Generating report...");
                new Reporting().generateReport(configMarshaller.getOutputFile()).writeToFile();
                LOGGER.info("Report was generated...");
            } catch (final IOException e) {
                LOGGER.error("While generating report.", e);
            }
        }
    }

    public void finishedWithErrors() {
        gui.finished();
        LOGGER.info("Process ended with errors!");
    }

    public void error(final String errorMessage) {
        this.errorState = true;
        this.awaitCompletion = true;
        final String finalErrorMessage = "Process ended with errors!<br>" + errorMessage;
        LOGGER.error(finalErrorMessage);
        gui.displayError(finalErrorMessage, e -> this.proceed());
    }

    public interface Task {

        void process();
    }
}
