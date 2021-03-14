package de.vsc.coi.crawlers;

import static de.vsc.coi.Config.config;
import static de.vsc.coi.Utils.nameEndsWith;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.util.List;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.vsc.coi.Config;
import de.vsc.coi.Utils;
import de.vsc.coi.Workspace;
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
    public List<File> getChildren(final File parent) {
        return Utils.streamChildren(parent)
                .filter(x -> nameEndsWith(x, config().getModFileEnding()))
                .collect(toList());
    }

    @Override
    public void postProcessPlugin(final PluginBuilder builder, final File child) {
        builder.newFileSystemItem()
                .setSource(Workspace.relativize(child))
                .setDestination(gameArchiveFolder(child))
                .setPriority(0);
    }

    @Override
    protected Logger logger() {
        return LOGGER;
    }
}
