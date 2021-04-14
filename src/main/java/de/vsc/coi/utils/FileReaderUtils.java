package de.vsc.coi.utils;

import static java.util.Collections.emptyList;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.io.FileUtils.readLines;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;

import de.vsc.coi.crawlers.ArchiveDirectoryCrawler;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class FileReaderUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileReaderUtils.class);

    public static List<String> readFile(final File file) {
        try {
            return readLines(file, Charsets.UTF_8);
        } catch (final IOException e) {
            LOGGER.error("Can not read file '{}'", file.getPath(), e);
        }
        return emptyList();
    }

    public static List<String> readLinesOfResource(final String resourcePath) {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream(resourcePath)))) {
            return reader.lines().collect(Collectors.toList());
        } catch (final IOException e) {
            LOGGER.error("Could not load class resource '{}'.", resourcePath, e);
        }
        return null;
    }

    public static InputStream resourceAsStream(final String path) {
        return ArchiveDirectoryCrawler.class.getClassLoader().getResourceAsStream(path);
    }
}
