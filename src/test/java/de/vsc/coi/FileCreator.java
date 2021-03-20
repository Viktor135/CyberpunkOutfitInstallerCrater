package de.vsc.coi;

import static lombok.AccessLevel.PRIVATE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.io.File;
import java.io.IOException;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class FileCreator {

    private static File parentDir;

    public static File newFile(final String name) throws IOException {
        final File file = new File(parentDir, name);
        assertThat(file.createNewFile(), is(true));
        return file;
    }

    public static File newDir(final String name) {
        final File file = new File(parentDir, name);
        assertThat(file.mkdir(), is(true));
        return file;
    }

    public static void init(final File dir) {
        parentDir = dir;
    }

}
