package de.vsc.coi.crawlers;

import static de.vsc.coi.config.Config.config;
import static de.vsc.coi.crawlers.DirectoryCrawler.getImageFromDir;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.vsc.coi.builder.InstallStepBuilder;
import de.vsc.coi.builder.ModuleConfigurationBuilder;
import de.vsc.coi.config.Workspace;
import fomod.ModuleConfiguration;

public class FileCrawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileCrawler.class);

    private final List<DirectoryCrawler> crawlers;
    private final ModuleConfigurationBuilder configurationBuilder;

    public FileCrawler() {
        Workspace.checkIfInitialised();
        configurationBuilder = ModuleConfigurationBuilder.builder();
        crawlers = new ArrayList<>();
        crawlers.add(new ArchiveDirectoryCrawler());
        crawlers.add(new SelectDirectoryCrawler());
    }

    public ModuleConfiguration crawl() {
        LOGGER.info("Starting to crawl the workspace.");
        crawl(new Work(Workspace.dir(), null));// The top directory has no flag conditions.
        LOGGER.info("Crawling finished.");
        return configurationBuilder.name(Workspace.name())
                .image(getImageFromDir(Workspace.dir(), config().getModuleImageName()))
                .build();
    }

    private void crawl(final Work work) {
        if (work.getDirectory().list() == null) {
            return;
        }
        Optional<InstallStepBuilder> step = Optional.empty();
        for (final DirectoryCrawler crawler : crawlers) {
            step = step.or(() -> crawler.createStep(work));
        }
        step.ifPresent(work::addFlagDependencyIfPresent);
        step.map(InstallStepBuilder::build).ifPresent(configurationBuilder::addStep);
        Optional.ofNullable(work.next()).ifPresent(this::crawl);
    }

}
