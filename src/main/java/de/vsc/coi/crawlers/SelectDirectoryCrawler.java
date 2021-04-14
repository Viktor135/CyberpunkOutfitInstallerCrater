package de.vsc.coi.crawlers;

import static de.vsc.coi.utils.DirectoryUtils.childDirectories;

import java.io.File;
import java.util.List;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.vsc.coi.builder.PluginBuilder;

public class SelectDirectoryCrawler extends DirectoryCrawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SelectDirectoryCrawler.class);

    public SelectDirectoryCrawler() {
        super();
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
