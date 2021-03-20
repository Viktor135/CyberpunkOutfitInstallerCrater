package de.vsc.coi.utils;

import static de.vsc.coi.utils.FileNameUtils.fileNameIs;
import static de.vsc.coi.utils.FileNameUtils.nameIsNoneOf;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

import lombok.AllArgsConstructor;
import lombok.ToString;

class FileNameUtilsTest {

    @Test
    void should_correctly_match_the_file_names() {
        final List<FileMatchItem<String>> items = asList(new FileMatchItem<>("some file.txt", "some FILE", true),
                new FileMatchItem<>("some dir", "soMe Dir", true),
                new FileMatchItem<>("some file.jpeg", "soMe FILE", true),
                new FileMatchItem<>("some file.jpeg", "soMe FILE.txt", true),
                new FileMatchItem<>("some file but another.txt", "some FILE", false),
                new FileMatchItem<>("some other some file.txt", "some FILE", false));

        items.forEach(item -> assertThat("For " + item, fileNameIs(item.file, item.toMatch), is(item.matches)));
    }

    @Test
    void should_correctly_match_is_none_of() {
        final List<FileMatchItem<List<String>>> items = asList(
                new FileMatchItem<>("some file.txt", asList("some other file", "some other file.txt"), true),
                new FileMatchItem<>("some fi le.txt", asList("some file", "some other file.txt"), true),
                new FileMatchItem<>("some file.txt", asList("some file.png", "some other file.txt"), false),
                new FileMatchItem<>("some file.txt", asList("some file", "some other file.txt"), false));

        items.forEach(item -> assertThat("For " + item, nameIsNoneOf(item.file, item.toMatch), is(item.matches)));
    }

    @AllArgsConstructor
    @ToString
    private static class FileMatchItem<T> {

        File file;
        T toMatch;
        boolean matches;

        public FileMatchItem(final String fileName, final T toMatch, final boolean matches) {
            this.file = new File(fileName);
            this.toMatch = toMatch;
            this.matches = matches;
        }
    }

}