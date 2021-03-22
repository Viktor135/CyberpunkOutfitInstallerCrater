package de.vsc.coi.crawlers;

import static de.vsc.coi.AutoInit.newAutoInit;
import static de.vsc.coi.AutoInit.newResourceReader;
import static de.vsc.coi.builder.FileDependencyState.MISSING;
import static de.vsc.coi.builder.FileSystemItemBuilder.newFileToCopy;
import static de.vsc.coi.config.Config.config;
import static de.vsc.coi.config.Workspace.relativize;
import static de.vsc.coi.utils.FileNameUtils.nameEndsWith;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FileUtils.writeLines;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.vsc.coi.AutoInit;
import de.vsc.coi.builder.InstallStepBuilder;
import de.vsc.coi.builder.PluginBuilder;
import de.vsc.coi.config.Workspace;
import de.vsc.coi.utils.DirectoryUtils;
import de.vsc.coi.utils.FileReaderUtils;

public class ArchiveDirectoryCrawler extends DirectoryCrawler {

    private static final Logger LOGGER = LogManager.getLogger(ArchiveDirectoryCrawler.class);

    public static final AutoInit<List<String>> MARKER_FILE_INFO = newAutoInit(() -> {
        final List<String> lines = FileReaderUtils.readLinesOfResource("replacesItemMarkerFileInfo.txt");
        requireNonNull(lines).add("The replacing mod is: " + Workspace.modName());
        return lines;
    });
    public static final AutoInit<List<String>> ITEM_CODES = newResourceReader("Console Item List/ItemListRAW.txt");

    public ArchiveDirectoryCrawler() {
        super();
    }

    public static String gameArchiveFolder(final File file) {
        return config().getModFileDir() + file.getName();
    }

    @Override
    public Optional<InstallStepBuilder> createStep(final Work work) {
        final Optional<InstallStepBuilder> step = super.createStep(work);

        final List<File> markerFiles = getReplacedItemMarker(work.getDirectory());
        if (step.isPresent() && !markerFiles.isEmpty()) {
            for (final File markerFile : markerFiles) {
                LOGGER.info("Replacement marker found. Adding it to step dependency. '{}'", relativize(markerFile));
                step.get().addFileDependency(markerFile.getName(), MISSING).addFileToCopy(newFileToCopy(markerFile));

                if (FileReaderUtils.readFile(markerFile).isEmpty()) {
                    writeReplacesItemMarkerFileContend(markerFile);
                }
            }
        }
        return step;
    }

    @Override
    protected List<File> getChildren(final File parent) {
        return DirectoryUtils.streamChildren(parent)
                .filter(x -> nameEndsWith(x, config().getModFileEnding()))
                .collect(toList());
    }

    @Override
    protected void postProcessPlugin(final PluginBuilder builder, final File child) {
        builder.addFile(newFileToCopy(child));
    }

    protected List<File> getReplacedItemMarker(final File dir) {
        return DirectoryUtils.streamChildren(dir).filter(File::isFile).filter(this::isValidItemCode).collect(toList());
    }

    protected void writeReplacesItemMarkerFileContend(final File markerFile) {
        LOGGER.info("Writing info to marker file: '{}'", relativize(markerFile));
        try {
            writeLines(markerFile, MARKER_FILE_INFO.get());
        } catch (final IOException e) {
            LOGGER.error("Could not write info to replacement marker file.", e);
        }
    }

    public boolean isValidItemCode(final File file) {
        return isValidItemCode(file.getName());
    }

    public boolean isValidItemCode(final String code) {
        if (!startsWithIgnoreCase(code, "Items.")) {
            return false;
        }
        final boolean valid = ITEM_CODES.get().contains(code);
        if (!valid) {
            LOGGER.warn("Found file starting with 'Items.' but which is not a valid item code. This should not be the"
                    + " case. Pleas rename the file.");
        }
        return valid;
    }

    @Override
    protected Logger logger() {
        return LOGGER;
    }
}
