package de.vsc.coi;

import static de.vsc.coi.config.Config.config;

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

import de.vsc.coi.config.Config;
import de.vsc.coi.config.Workspace;
import de.vsc.coi.crawlers.FileCrawler;
import de.vsc.coi.gui.Gui;
import de.vsc.coi.gui.GuiWrapper;
import de.vsc.coi.gui.GuiWrapper.Result;
import de.vsc.coi.marshaller.ConfigMarshaller;
import de.vsc.coi.marshaller.InfoMarshaller;
import fomod.ModuleConfiguration;

public class Application implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(Application.class);

    public final String[] args;
    private final Queue<Task> tasks;
    private final GuiWrapper guiWrapper;
    public boolean ERROR_STATE = false;
    public boolean AWAIT_COMPLETION = false;
    private ConfigMarshaller configMarshaller;
    private InfoMarshaller infoMarshaller;

    public Application(final String[] args) {
        this.args = args;
        this.tasks = new ArrayDeque<>();
        this.guiWrapper = GuiWrapper.getInstance();
    }

    public static void main(final String... args) {
        final Application application = new Application(args);
        GuiWrapper.createGui();
        ForkJoinPool.commonPool().execute(application);
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
                this::generateReport,
                this::finished));
        //@formatter:on
        proceed();
    }

    public void proceed() {
        ForkJoinPool.commonPool().execute(this::workOnTasks);
    }

    private void workOnTasks() {
        AWAIT_COMPLETION = false;
        while (!ERROR_STATE && !AWAIT_COMPLETION && !tasks.isEmpty()) {
            tasks.poll().process();
        }
    }

    public void initialise() {
        try {

            final Result<Void> result = guiWrapper.invoke(Gui::init);
            if (result.hadError()) {
                LOGGER.error("While starting GUI.", result.getException());
                ERROR_STATE = true;
                return;
            }
            guiWrapper.setGuiStatus("Initialising...");
            Config.init(args);
            Workspace.init();
        } catch (final IllegalStateException e) {
            LOGGER.error(e);
            error(e.getMessage());
            ERROR_STATE = true;
        }

    }

    public void determineWorkspace() {

        guiWrapper.setGuiStatus("Determine workspace...");
        final File fomodFolder = new File(Workspace.dir(), "fomod");
        if (!fomodFolder.exists()) {
            LOGGER.info("Creating output directory: " + fomodFolder.getPath());
            if (!fomodFolder.mkdirs()) {
                error("Could not create output directory: " + fomodFolder.getPath());
                ERROR_STATE = true;
                return;
            }
        }
        if (!fomodFolder.canWrite()) {
            error("The CPOIC have to have write access in the workspace!");
            ERROR_STATE = true;
        }
        this.infoMarshaller = new InfoMarshaller(fomodFolder);
        this.configMarshaller = new ConfigMarshaller(fomodFolder);
    }

    public void editModInfo() {
        guiWrapper.setGuiStatus("Pleas fill out the mod info.");
        try {
            final Info modInfo = (Info) infoMarshaller.validate();
            guiWrapper.call(gui -> gui.showInfoPanel(modInfo)).getValue().onNext(e -> this.proceed());
            AWAIT_COMPLETION = true;
        } catch (final JAXBException | SAXException | URISyntaxException e) {
            error("For details view the log file.");
            LOGGER.error("While unmarshalling info.xml:", e);
            ERROR_STATE = true;
        }
    }

    public void saveModInfo() {
        guiWrapper.setGuiStatus("Saving mod info.");
        final Info modInfo = guiWrapper.call(Gui::closeInfoPanel).getValue();

        if (modInfo == null) { // no changes were made
            return;
        }
        try {
            infoMarshaller.marshal(modInfo);
        } catch (final JAXBException | IOException e) {
            error("\n Could not write info to file.\n For details view the log file.");
            LOGGER.error("While marshalling info.xml:", e);
            ERROR_STATE = true;
        }
    }

    public void validateModInfo() {
        try {
            infoMarshaller.validate();
        } catch (final JAXBException | SAXException | URISyntaxException e) {
            error("For details view the log file.");
            LOGGER.error("While validating info.xml:", e);
            ERROR_STATE = true;
        }
    }

    public void createInstaller() {
        guiWrapper.setGuiStatus("Creating installer...");
        final ModuleConfiguration configuration = new FileCrawler().crawl();
        try {
            LOGGER.info("Marshalling...");
            configMarshaller.marshal(configuration);
        } catch (final JAXBException | IOException e) {
            error("For details view the log file.");
            LOGGER.error("While marshalling:", e);
            ERROR_STATE = true;
        }
    }

    public void validateInstaller() {
        try {
            LOGGER.info("Validating...");
            configMarshaller.validate();
        } catch (final JAXBException | URISyntaxException | SAXException e) {
            error("Validation of the created Installer ended with errors! Most likely, this is an hadError in the CpOIC. "
                    + "Pleas inform the mod author, then the bug will be fixed. For details view the log file.");
            LOGGER.error("While validation:", e);
            ERROR_STATE = true;
        }
    }

    public void generateReport() {
        if (config().isGenerateReport()) {
            try {
                LOGGER.info("Generating report...");
                new Reporting().generateReport(configMarshaller.getOutputFile()).writeToFile();
            } catch (final IOException e) {
                LOGGER.error("While generating report.", e);
            }
        }

    }

    public void finished() {
        guiWrapper.invoke(Gui::finished);
        LOGGER.info("Process ended successfully!");
    }

    public void error(final String errorMessage) {
        final String finalErrorMessage = "Process ended with errors!\n" + errorMessage;
        LOGGER.error(finalErrorMessage);
        guiWrapper.invoke(gui -> gui.displayError(finalErrorMessage));
    }

    public interface Task {

        void process();
    }
}
