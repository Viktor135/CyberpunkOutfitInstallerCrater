package de.vsc.coi.utils;

import static java.util.Collections.emptyList;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.io.FileUtils.readLines;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Charsets;

import de.vsc.coi.crawlers.ArchiveDirectoryCrawler;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class FIleReaderUtils {

    private static final Logger LOGGER = LogManager.getLogger(FIleReaderUtils.class);

    public static List<String> readFile(final File file) {
        try {
            return readLines(file, Charsets.UTF_8);
        } catch (final IOException e) {
            LOGGER.error("Can not read file '{}'", file.getPath(), e);
        }
        return emptyList();
    }

    public static List<String> readLinesOfResource(final String resourceName) {
        try {
            final URL resource = ArchiveDirectoryCrawler.class.getClassLoader().getResource(resourceName);
            Objects.requireNonNull(resource);
            return readLines(new File(resource.toURI()), Charsets.UTF_8);
        } catch (final URISyntaxException | IOException e) {
            LOGGER.error("Could not load class resource '{}'.", resourceName, e);
        }
        return null;
    }
}
