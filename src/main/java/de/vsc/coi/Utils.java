package de.vsc.coi;

import static de.vsc.coi.Config.config;
import static java.nio.charset.Charset.defaultCharset;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.endsWithIgnoreCase;
import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.removeStartIgnoreCase;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Utils {

    private static final Logger LOGGER = LogManager.getLogger(Utils.class);

    private Utils() {
    }

    public static boolean fileNameStartsWith(final File file, final String nameStart) {
        return startsWithIgnoreCase(file.getName(), nameStart);
    }

    public static FilenameFilter nameStartsWith(final String prefix) {
        return (dir, name) -> startsWithIgnoreCase(name, prefix);
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

    public static List<String> loadIgnores(final File dir) {
        final List<String> ignore = new ArrayList<>(config().getDefaultIgnores());
        stream(dir.listFiles(nameStartsWith("ignore"))).findFirst()
                .ifPresent(ignoreFile -> ignore.addAll(readFile(ignoreFile)));
        return ignore;
    }

    public static List<String> readFile(final File file) {
        try {
            return FileUtils.readLines(file, defaultCharset());
        } catch (final IOException e) {
            LOGGER.error("Can not read file {}", file.getPath(), e);
        }
        return emptyList();
    }

    public static List<File> childDirectories(final File file) {
        return Utils.streamChildren(file).filter(File::isDirectory).collect(toList());
    }

    public static Stream<File> streamChildImages(final File file) {
        return Utils.streamChildren(file).filter(Utils::isImage);
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
                .map(FilenameUtils::removeExtension)
                .map(name -> name.replaceAll("_", " "))
                .map(String::trim)
                .orElseThrow();
    }

    public static <O> O getOrNew(final Supplier<O> getter, final Consumer<O> setter, final Supplier<O> newObj) {
        O obj = getter.get();
        if (obj == null) {
            obj = newObj.get();
            setter.accept(obj);
        }
        return obj;
    }

    /**
     * Ignores case, leading and tailing whitespaces and file endings.
     *
     * @param file
     *         the file to check
     * @param name
     *         the name to match
     *
     * @return if the file name is the given name
     */
    public static boolean fileNameIs(final File file, final String name) {
        return Optional.ofNullable(file)
                .map(File::getName)
                .map(FilenameUtils::removeExtension)
                .map(String::trim)
                .map(x -> equalsIgnoreCase(x, name.trim()))
                .orElse(false);
    }

    public static String getDescription(final File dir) {
        return Utils.streamChildren(dir)
                .filter(x -> Utils.fileNameIs(x, "description"))
                .findFirst()
                .map(Utils::readFile)
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.joining());
    }
}
