package de.vsc.coi.crawlers;

import static de.vsc.coi.Config.config;
import static de.vsc.coi.Utils.nameEndsWith;
import static de.vsc.coi.Workspace.relativize;
import static de.vsc.coi.builder.FileDependencyState.MISSING;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Charsets;

import de.vsc.coi.Utils;
import de.vsc.coi.Workspace;
import de.vsc.coi.builder.InstallStepBuilder;
import de.vsc.coi.builder.PluginBuilder;

public class ArchiveDirectoryCrawler extends DirectoryCrawler {

    private static final Logger LOGGER = LogManager.getLogger(ArchiveDirectoryCrawler.class);

    public ArchiveDirectoryCrawler(final Queue<FileCrawler.Work> workQueue) {
        super(workQueue);
    }

    public static String gameArchiveFolder(final File file) {
        return config().getModFileDir() + file.getName();
    }

    @Override
    public Optional<InstallStepBuilder> createStep(final File dir) {
        final Optional<InstallStepBuilder> step = super.createStep(dir);

        final File markerFile = getReplacedItemMarker(dir);
        if (step.isPresent() && markerFile != null) {
            LOGGER.info("Replacement marker found. Adding it to step dependency. '{}'", relativize(markerFile));
            step.get()
                    .addFileDependency(markerFile.getName(), MISSING)
                    .newFileToCopy()
                    .setPriority(0)
                    .setSource(relativize(markerFile))
                    .setDestination(gameArchiveFolder(markerFile))
                    .build();
            if (Utils.readFile(markerFile).isEmpty()) {
                writeReplacesItemMarkerFileContend(markerFile);
            }
        }
        return step;
    }

    @Override
    protected List<File> getChildren(final File parent) {
        return Utils.streamChildren(parent).filter(x -> nameEndsWith(x, config().getModFileEnding())).collect(toList());
    }

    @Override
    protected void postProcessPlugin(final PluginBuilder builder, final File child) {
        builder.newFileSystemItem()
                .setSource(relativize(child))
                .setDestination(gameArchiveFolder(child))
                .setPriority(0);
    }

    protected File getReplacedItemMarker(final File dir) {
        return Utils.streamChildren(dir)
                .filter(x -> Utils.fileNameStartsWith(x, config().getReplacesItemMarkerFilePrefix()))
                .findFirst()
                .orElse(null);
    }

    protected void writeReplacesItemMarkerFileContend(final File markerFile) {
        LOGGER.info("Writing info to marker file: '{}'", relativize(markerFile));
        try {
            final URL resource = getClass().getClassLoader().getResource("replacesItemMarkerFileInfo.txt");
            Objects.requireNonNull(resource);
            final List<String> lines = FileUtils.readLines(new File(resource.toURI()), Charsets.UTF_8);
            lines.add("The replacing mod is: " + Workspace.modName());
            FileUtils.writeLines(markerFile, lines);
        } catch (final IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Logger logger() {
        return LOGGER;
    }
}
