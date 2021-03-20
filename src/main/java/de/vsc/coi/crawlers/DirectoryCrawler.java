package de.vsc.coi.crawlers;

import static de.vsc.coi.Config.config;
import static de.vsc.coi.utils.FileNameUtils.fileNameIs;
import static de.vsc.coi.utils.FileNameUtils.formatName;
import static de.vsc.coi.utils.Utils.toUniqueOptional;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;

import de.vsc.coi.Workspace;
import de.vsc.coi.builder.GroupBuilder;
import de.vsc.coi.builder.InstallStepBuilder;
import de.vsc.coi.builder.PluginBuilder;
import de.vsc.coi.builder.SelectionMode;
import de.vsc.coi.crawlers.FileCrawler.Work;
import de.vsc.coi.utils.DirectoryUtils;

public abstract class DirectoryCrawler {

    private static int id = 0;
    protected final Queue<Work> workQueue;

    protected DirectoryCrawler(final Queue<Work> workQueue) {
        this.workQueue = workQueue;
    }

    public static String getUniqueFlag(final File file) {
        return formatName(file) + "_" + (id++);
    }

    public static Optional<String> getImageFromDir(final File dir, final String name) {
        return DirectoryUtils.streamChildImages(dir)
                .filter(x -> fileNameIs(x, name))
                .collect(toUniqueOptional("image with name: " + name, dir))
                .map(Workspace::relativize);
    }

    public static SelectionMode selectionModeFor(final File dir) {
        return DirectoryUtils.streamChildren(dir)
                .map(File::getName)
                .map(SelectionMode::fromName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toUniqueOptional("SelectionMode file", dir))
                .orElse(SelectionMode.DEFAULT);
    }

    public Optional<InstallStepBuilder> createStep(final File dir) {
        logger().info("Starting to crawl: '{}'", Workspace.relativize(dir));
        final List<File> children = getChildren(dir);
        if (children.isEmpty()) {
            logger().info("No suitable files found in the directory.");
            return Optional.empty();
        }
        logger().info("Suitable files found: '{}'",
                children.stream().map(File::getName).collect(Collectors.joining(",")));
        final String fileName = formatName(dir);
        final Optional<String> commonImage = getImageFromDir(dir, config().getCommonImageName());
        final String stepDescription = DirectoryUtils.getDescription(dir);
        final GroupBuilder groupBuilder = InstallStepBuilder.builder()
                .name(fileName)
                .newGroup()
                .type(selectionModeFor(dir))
                .name(fileName);
        for (final File child : children) {
            final String pluginName = formatName(child);
            logger().info("Adding new plugin: '{}'", pluginName);

            final PluginBuilder pluginBuilder = groupBuilder.newPlugin()
                    .name(pluginName)
                    .description(stepDescription)
                    .setImageIfNotPresent(getImageFromDir(dir, child.getName()))
                    .setImageIfNotPresent(commonImage);
            postProcessPlugin(pluginBuilder, child);
        }
        logger().info("Adding new step: '{}'", fileName);
        return Optional.of(groupBuilder.parent());
    }

    protected abstract List<File> getChildren(final File parent);

    protected abstract void postProcessPlugin(final PluginBuilder builder, final File child);

    protected void addWork(final File file, final String flag) {
        workQueue.add(new Work(file, flag));
    }

    protected abstract Logger logger();

}
