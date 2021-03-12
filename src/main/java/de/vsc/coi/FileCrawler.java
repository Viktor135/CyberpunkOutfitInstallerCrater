package de.vsc.coi;

import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

import org.apache.commons.lang3.tuple.Pair;

import de.vsc.coi.PluginList.Plugin;
import de.vsc.coi.PluginList.Plugin.ConditionFlag;
import de.vsc.coi.PluginList.Plugin.FileToCopy;

public class FileCrawler {

    private final InstallSteps installSteps = new InstallSteps();
    private final Queue<Pair<File, ConditionFlag>> work = new ArrayDeque<>();
    private final File workspace;

    public enum SelectionMode {
        SelectExactlyOne,
        SelectAny,
        SelectAtMostOne,
        SelectAtLeastOne,
        SelectAll;

        public static final SelectionMode DEFAULT = SelectAtMostOne;

        public static Optional<SelectionMode> fromName(final String name) {
            return Arrays.stream(values()).filter(x -> equalsAnyIgnoreCase(x.name(), name)).findFirst();
        }
    }

    public FileCrawler(final File workspace) {
        this.workspace = workspace;
    }

    public ModuleConfig crawl() {
        work.add(Pair.of(workspace, null));
        while (!work.isEmpty()) {
            final Pair<File, ConditionFlag> next = work.poll();
            crawl(next.getLeft(), next.getRight());
        }
        crawl(workspace, null);
        return new ModuleConfig(workspace.getName(), moduleImage(workspace), installSteps);
    }

    private void crawl(final File file, final ConditionFlag condition) {
        if (file.list() == null) {
            return;
        }
        createArchiveStep(file).or(() -> createSelectStep(file)).map(step -> {
            if (condition != null) {
                step.addDependency(condition);
            }
            return step;
        }).ifPresent(step -> installSteps.installStep.add(step));
    }

    public Optional<InstallStep> createArchiveStep(final File file) {
        //This is a folder with archives and at most one image.
        final List<File> archives = Utils.childArchives(file);
        if (archives.isEmpty()) {
            return Optional.empty();
        }
        final PluginList pluginList = new PluginList();
        final Optional<Image> commonImage = commonImageForStep(file);
        for (final File archive : archives) {
            final Plugin plugin = createPlugin(file, archive, commonImage);
            plugin.filesToCopie.add(fileToCopy(archive));
            pluginList.plugins.add(plugin);
        }
        return Optional.of(new InstallStep(Utils.formatName(file), pluginList, selectionModeFor(file)));
    }

    public Optional<InstallStep> createSelectStep(final File file) {
        // Each recursive created step depends on the selection in this step.
        final List<File> directories = Utils.childDirectories(file);
        if (directories.isEmpty()) {
            return Optional.empty();
        }
        final PluginList pluginList = new PluginList();
        final Optional<Image> commonImage = commonImageForStep(file);
        for (final File dir : directories) {
            final Plugin plugin = createPlugin(file, dir, commonImage);
            final ConditionFlag flag = plugin.addNameBasedCondition();
            pluginList.plugins.add(plugin);
            work.add(Pair.of(dir, flag));
        }
        return Optional.of(new InstallStep(Utils.formatName(file), pluginList, selectionModeFor(file)));
    }

    public SelectionMode selectionModeFor(final File dir) {
        return Utils.streamChildren(dir)
                .map(File::getName)
                .map(SelectionMode::fromName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElse(SelectionMode.DEFAULT);
    }

    public Plugin createPlugin(final File parent, final File child, final Optional<Image> commonImage) {
        final Plugin plugin = new Plugin();

        plugin.name = Utils.formatName(child);
        imageForPlugin(parent, child.getName()).or(() -> commonImage).ifPresent(x -> plugin.image = x);
        return plugin;
    }

    public String relativize(final File f) {
        return workspace.toPath().relativize(f.toPath()).toString();
    }

    public Optional<Image> commonImageForStep(final File dir) {
        return Utils.streamChildImages(dir)
                .filter(x -> Utils.fileNameStartsWith(x, "common"))
                .findFirst()
                .map(x -> new Image(relativize(x)));
    }

    public ModuleImage moduleImage(final File dir) {
        return new ModuleImage(Utils.streamChildImages(dir)
                .filter(x -> Utils.fileNameStartsWith(x, "module"))
                .findFirst()
                .map(this::relativize)
                .orElse(""));
    }

    public Optional<Image> imageForPlugin(final File dir, final String pluginName) {
        return Utils.streamChildImages(dir)
                .filter(x -> Utils.fileNameStartsWith(x, pluginName))
                .findFirst()
                .map(x -> new Image(relativize(x)));
    }

    public FileToCopy fileToCopy(final File file) {
        return new FileToCopy(relativize(file), "\\archive\\pc\\patch\\" + file.getName(), "0");
    }

}
