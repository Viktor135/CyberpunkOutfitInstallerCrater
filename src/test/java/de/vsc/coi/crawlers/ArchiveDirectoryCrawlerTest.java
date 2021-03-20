package de.vsc.coi.crawlers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.core.IsNot.not;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.vsc.coi.config.Workspace;
import de.vsc.coi.builder.FileSystemItemBuilder;
import fomod.FileSystemItem;

class ArchiveDirectoryCrawlerTest {

    @BeforeAll
    static void init() {
        Workspace.init(new File("abc"));
    }

    static FileSystemItem item(final String name) {
        return FileSystemItemBuilder.builder()
                .setSource(name)
                .setDestination("\\archive\\pc\\patch\\" + name)
                .setPriority(0)
                .build();
    }

    public static <K, V> Map<K, V> zipToMap(final List<K> keys, final List<V> values) {
        if (keys.size() != values.size()) {
            throw new IllegalArgumentException();
        }
        return IntStream.range(0, keys.size()).boxed().collect(Collectors.toMap(keys::get, values::get));
    }

    @Test
    void should_read_marker_file_info() {
        assertThat(ArchiveDirectoryCrawler.MARKER_FILE_INFO.get(), not(emptyIterable()));
    }

    @Test
    void should_read_item_codes() {
        assertThat(ArchiveDirectoryCrawler.ITEM_CODES.get(), not(emptyIterable()));
    }

}