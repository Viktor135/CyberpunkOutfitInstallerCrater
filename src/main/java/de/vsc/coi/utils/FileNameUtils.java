package de.vsc.coi.utils;

import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.endsWithIgnoreCase;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.removeStartIgnoreCase;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class FileNameUtils {

    public static boolean nameIsNoneOf(final File f, final List<String> names) {
        return names.stream().noneMatch(name -> fileNameIs(f, name));
    }

    public static boolean nameEndsWith(final File f, final String postfix) {
        return endsWithIgnoreCase(f.getName(), postfix);
    }

    public static String formatName(final File file) {
        return Optional.of(file.getName())
                .map(name -> removeStartIgnoreCase(name, "basegame"))
                .map(FilenameUtils::removeExtension)
                .map(name -> name.replaceAll("_", " "))
                .map(String::trim)
                .orElseThrow();
    }

    /**
     * Ignores case, leading and tailing whitespaces and file extensions.
     *
     * @param file
     *         the file to check
     * @param name
     *         the name to match
     *
     * @return if the file name is the given name
     */
    public static boolean fileNameIs(final File file, final String name) {
        final String n = FilenameUtils.removeExtension(name).trim();
        return Optional.ofNullable(file)
                .map(File::getName)
                .map(FilenameUtils::removeExtension)
                .map(String::trim)
                .map(x -> equalsIgnoreCase(x, n))
                .orElse(false);
    }

    public static boolean isImage(final File f) {
        return f.getName().matches("(.*/)*.+\\.(png|jpg|gif|bmp|jpeg|PNG|JPG|GIF|BMP|JPEG)$");
    }
}
