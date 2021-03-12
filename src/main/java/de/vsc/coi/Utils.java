package de.vsc.coi;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.endsWithIgnoreCase;
import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;
import static org.apache.commons.lang3.StringUtils.removeEndIgnoreCase;
import static org.apache.commons.lang3.StringUtils.removeStartIgnoreCase;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {

    private Utils() {
    }

    public static boolean fileNameStartsWith(final File file, final String nameStart) {
        return startsWithIgnoreCase(file.getName(), nameStart);
    }

    public static boolean isImage(final File f) {
        //@formatter:off
        return f.getName().matches("(.*/)*.+\\.(png|jpg|gif|bmp|jpeg|PNG|JPG|GIF|BMP|JPEG)$");
        //@formatter:on
    }

    private static Stream<File> stream(final File[] files) {
        return Stream.ofNullable(files).flatMap(Arrays::stream);
    }

    public static Stream<File> streamChildren(final File dir) {
        return stream(dir.listFiles()).filter(f -> nameIsNoneOf(f, loadIgnores(dir)));
    }

    public static List<String> loadIgnores(final File dir){
        final List<String> ignore = new ArrayList<>();
        ignore.add("fomod");
        stream(dir.listFiles(nameStartsWith("ignore"))).findFirst().ifPresent(ignoreFile -> {
            try (final BufferedReader reader = new BufferedReader(new FileReader(ignoreFile))) {
                ignore.addAll(reader.lines().collect(Collectors.toList()));
            } catch (final IOException e) {
                e.printStackTrace();
            }
        });
        return ignore;
    }

    public static List<File> childArchives(final File file) {
        return Utils.streamChildren(file).filter(x -> nameEndsWith(x, ".archive")).collect(toList());
    }

    public static List<File> childDirectories(final File file) {
        return Utils.streamChildren(file).filter(File::isDirectory).collect(toList());
    }

    public static Stream<File> streamChildImages(final File file) {
        return Utils.streamChildren(file).filter(Utils::isImage);
    }

    public static FilenameFilter nameStartsWith(final String prefix) {
        return (dir, name) -> startsWithIgnoreCase(name, prefix);
    }

    public static boolean nameIsNoneOf(final File f, final List<String> names) {
        return names.stream().noneMatch(x -> equalsAnyIgnoreCase(x, f.getName()));
    }

    public static boolean nameEndsWith(final File f, final String postfix) {
        return endsWithIgnoreCase(f.getName(), postfix);
    }

    public static String formatName(final File file) {
        return Optional.of(file.getName())
                .map(name -> removeStartIgnoreCase(name, "basegame"))
                .map(name -> removeEndIgnoreCase(name, ".archive"))
                .map(name -> name.replaceAll("_", " "))
                .map(String::trim)
                .orElseThrow();
    }
}
