package de.vsc.coi.crawlers;

import static java.util.Optional.ofNullable;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.vsc.coi.builder.InstallStepBuilder;
import lombok.Getter;
import lombok.ToString;

@ToString
public class Work {

    private static final Logger LOGGER = LogManager.getLogger(Work.class);

    @Getter
    private final File directory;
    @Getter
    private final String conditionFlag;

    private final Queue<Work> subWork = new ArrayDeque<>();
    private final Work parent;

    private Work(final File directory, final String conditionFlag, final Work parent) {
        this.directory = directory;
        this.conditionFlag = conditionFlag;
        this.parent = parent;
    }

    public Work(final File directory, final String conditionFlag) {
        this(directory, conditionFlag, null);
    }

    public void addFlagDependencyIfPresent(final InstallStepBuilder builder) {
        if (conditionFlag != null) {
            LOGGER.info("Adding Flag dependency to '{}'", conditionFlag);
            builder.addFlagDependency(conditionFlag);
        }
    }

    public void addWork(final File directory, final String conditionFlag) {
        this.subWork.add(new Work(directory, conditionFlag, this));
    }

    public Work next() {
        final Work nextWork = subWork.poll();
        if (nextWork != null) {
            return nextWork;
        }
        if(parent == null){
            return null;
        }
        return parent.next();
    }
}
