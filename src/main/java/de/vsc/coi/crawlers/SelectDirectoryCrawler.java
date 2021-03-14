package de.vsc.coi.crawlers;

import static de.vsc.coi.Utils.childDirectories;

import java.io.File;
import java.util.List;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.vsc.coi.builder.PluginBuilder;
import de.vsc.coi.crawlers.FileCrawler.Work;

public class SelectDirectoryCrawler extends DirectoryCrawler {

    private static final Logger LOGGER = LogManager.getLogger(SelectDirectoryCrawler.class);

    public SelectDirectoryCrawler(final Queue<Work> workQueue) {
        super(workQueue);
    }

    @Override
    protected List<File> getChildren(final File parent) {
        return childDirectories(parent);
    }

    @Override
    protected void postProcessPlugin(final PluginBuilder builder, final File child) {
        final String flag = getUniqueFlag(child);
        builder.addConditionFlag(flag);
        addWork(child, flag);
    }

    @Override
    protected Logger logger() {
        return LOGGER;
    }
}
