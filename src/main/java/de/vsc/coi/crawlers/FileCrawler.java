package de.vsc.coi.crawlers;

import static de.vsc.coi.config.Config.config;
import static de.vsc.coi.crawlers.DirectoryCrawler.getImageFromDir;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.vsc.coi.config.Workspace;
import de.vsc.coi.builder.InstallStepBuilder;
import de.vsc.coi.builder.ModuleConfigurationBuilder;
import de.vsc.coi.builder.SubBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

public class FileCrawler {

    private static final Logger LOGGER = LogManager.getLogger(FileCrawler.class);

    private final List<DirectoryCrawler> crawlers;
    private final ModuleConfigurationBuilder configurationBuilder;
    private final Queue<Work> work = new ArrayDeque<>();

    public FileCrawler() {
        Workspace.checkIfInitialised();
        configurationBuilder = ModuleConfigurationBuilder.builder();
        crawlers = new ArrayList<>();
        crawlers.add(new ArchiveDirectoryCrawler(work));
        crawlers.add(new SelectDirectoryCrawler(work));
    }

    public ModuleConfigurationBuilder crawl() {
        LOGGER.info("Starting to crawl the workspace.");
        crawl(new Work(Workspace.dir(), null)); // The top directory has no flag conditions.
        while (!work.isEmpty()) {
            crawl(work.poll());
        }
        LOGGER.info("Crawling finished.");
        return configurationBuilder.name(Workspace.name())
                .image(getImageFromDir(Workspace.dir(), config().getModuleImageName()));
    }

    private void crawl(final Work work) {
        if (work.getDirectory().list() == null) {
            return;
        }
        Optional<InstallStepBuilder> step = Optional.empty();
        for (final DirectoryCrawler crawler : crawlers) {
            step = step.or(() -> crawler.createStep(work.getDirectory()));
        }
        step.ifPresent(work::addFlagDependencyIfPresent);
        step.map(SubBuilder::build).ifPresent(configurationBuilder::addStep);
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class Work {

        private final File directory;
        private final String conditionFlag;

        public void addFlagDependencyIfPresent(final InstallStepBuilder builder) {
            if (conditionFlag != null) {
                LOGGER.info("Adding Flag dependency to '{}'", conditionFlag);
                builder.addFlagDependency(conditionFlag);
            }
        }
    }
}
