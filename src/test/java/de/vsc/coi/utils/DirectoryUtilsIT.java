package de.vsc.coi.utils;

import static de.vsc.coi.FileCreator.newDir;
import static de.vsc.coi.FileCreator.newFile;
import static de.vsc.coi.utils.DirectoryUtils.childDirectories;
import static de.vsc.coi.utils.DirectoryUtils.streamChildImages;
import static de.vsc.coi.utils.DirectoryUtils.streamChildren;
import static de.vsc.coi.utils.Utils.toSingleton;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.Is.is;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.vsc.coi.config.Config;
import de.vsc.coi.FileCreator;
import de.vsc.coi.config.Workspace;

class DirectoryUtilsIT {

    static final List<String> ignores = new ArrayList<>();
    @TempDir
    static File directory;

    @BeforeAll
    static void init() throws IOException {
        FileCreator.init(directory);
        Config.init();
        Workspace.init(directory);

        newDir("first directory");
        newDir("second directory");
        newDir("third directory");
        newFile("first file.txt");
        newFile("second file.png");
        newFile("third file.archive");
        final File description = newFile("description.txt");
        FileUtils.writeLines(description, singletonList("SOme description"));

        newDir("first ignored directory");
        ignores.add("first ignored directory");
        newDir("second ignored directory");
        ignores.add("second ignored directory");
        newFile("ignored file");
        ignores.add("ignored file");
        newDir("fomod");// ignored due to Config#defaultIgnores

        final File ignoreFile = newFile("ignore.txt"); // ignored due to Config#ignoreFileName
        FileUtils.writeLines(ignoreFile, ignores);
    }

    @Test
    void should_stream_all_children() {
        final List<String> children = streamChildren(directory).map(File::getName).collect(Collectors.toList());
        assertThat(children,
                is(containsInAnyOrder("first directory", "second directory", "third directory", "first file.txt",
                        "second file.png", "third file.archive", "description.txt")));
    }

    @Test
    void should_get_all_images() {
        assertThat(streamChildImages(directory).map(File::getName).collect(toSingleton()), is("second file.png"));
    }

    @Test
    void should_get_all_dirs() {
        assertThat(childDirectories(directory).stream().map(File::getName).collect(Collectors.toList()),
                is(Matchers.containsInAnyOrder("first directory", "second directory", "third directory")));
    }

    @Test
    void should_get_description() {
        assertThat(DirectoryUtils.getDescription(directory), is("SOme description"));
    }
}