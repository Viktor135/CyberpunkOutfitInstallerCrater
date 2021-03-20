package de.vsc.coi.utils;

import static de.vsc.coi.Config.config;
import static de.vsc.coi.utils.FileNameUtils.fileNameIs;
import static de.vsc.coi.utils.FileNameUtils.nameIsNoneOf;
import static de.vsc.coi.utils.Utils.toUniqueOptional;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JFileChooser;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class DirectoryUtils {

    private static Stream<File> stream(final File[] files) {
        return Stream.ofNullable(files).flatMap(Arrays::stream);
    }

    /**
     * Returns a stream of child files and directories of the given dir,
     * while ignoring files and directories which are specified as to be ignored.
     *
     * @param dir
     *         the directory to search in
     *
     * @return a stream
     */
    public static Stream<File> streamChildren(final File dir) {
        return stream(dir.listFiles()).filter(f -> nameIsNoneOf(f, loadIgnores(dir)));
    }

    public static List<String> loadIgnores(final File dir) {
        final List<String> ignore = new ArrayList<>(config().getDefaultIgnores());
        stream(dir.listFiles((x) -> fileNameIs(x, config().getIgnoreFileName()))).collect(
                toUniqueOptional(config().getIgnoreFileName(), dir))
                .ifPresent(ignoreFile -> ignore.addAll(FIleReaderUtils.readFile(ignoreFile)));
        return ignore;
    }

    public static List<File> childDirectories(final File dir) {
        return DirectoryUtils.streamChildren(dir).filter(File::isDirectory).collect(toList());
    }

    public static Stream<File> streamChildImages(final File file) {
        return DirectoryUtils.streamChildren(file).filter(FileNameUtils::isImage);
    }

    public static String getDescription(final File dir) {
        return DirectoryUtils.streamChildren(dir)
                .filter(x -> fileNameIs(x, "description"))
                .collect(toUniqueOptional("description.txt", dir))
                .map(FIleReaderUtils::readFile)
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.joining());
    }

    public static File chooseDirectory() {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Pleas select the workspace to operate on.");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }
}
