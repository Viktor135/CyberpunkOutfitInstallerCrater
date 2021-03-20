package de.vsc.coi.crawlers;

import static de.vsc.coi.FileCreator.newFile;
import static de.vsc.coi.crawlers.ArchiveDirectoryCrawlerTest.zipToMap;
import static de.vsc.coi.utils.Utils.toSingleton;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsMapWithSize.aMapWithSize;
import static org.hamcrest.core.Is.is;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.bind.JAXBElement;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.vsc.coi.ArgumentCaptor;
import de.vsc.coi.config.Config;
import de.vsc.coi.FileCreator;
import de.vsc.coi.config.Workspace;
import de.vsc.coi.builder.InstallStepBuilder;
import fomod.FileList;
import fomod.FileSystemItem;
import fomod.Group;
import fomod.Image;
import fomod.InstallStep;
import fomod.OrderEnum;
import fomod.Plugin;

class ArchiveDirectoryCrawlerIT {

    final static List<File> makers = new ArrayList<>();
    @TempDir
    static File folder;
    static File workspace;
    static String expectedDescription;
    static InstallStep step;

    @BeforeAll
    public static void initTempDir() throws IOException {
        workspace = new File(folder, "Mod Name");
        workspace.mkdir();
        Workspace.init(workspace);
        FileCreator.init(workspace);
        Config.init();

        newFile("basegame_blue.archive");
        newFile("basegame_red.archive");
        newFile("basegame_yellow.archive");
        newFile("basegame_green.archive");

        newFile("basegame_blue.png");
        newFile("basegame_red.png");
        newFile("common.png");

        makers.add(newFile("Items.Q005_Johnny_Pants"));
        makers.add(newFile("Items.Preset_Omaha_Neon"));

        final File desc = newFile("description.txt");
        expectedDescription = "Some description!!";
        FileUtils.writeLines(desc, singleton(expectedDescription));

        final Optional<InstallStepBuilder> optStep = new ArchiveDirectoryCrawler(new ArrayDeque<>()).createStep(
                workspace);

        assertThat(optStep.isPresent(), is(true));
        step = optStep.get().build();

    }

    public static Matcher<Map<? extends String, ? extends FileSystemItem>> itemNameMatcher(final String name,
            final String file) {
        return Matchers.hasEntry(is(name), samePropertyValuesAs(ArchiveDirectoryCrawlerTest.item(file)));
    }

    @Test
    void should_have_Correct_step_attributes() {
        assertThat(step.getName(), is("Mod Name"));
        assertThat(step.getOptionalFileGroups().getOrder(), is(OrderEnum.EXPLICIT));
    }

    @Test
    void should_have_Correct_group_attributes() {
        final List<Group> groups = step.getOptionalFileGroups().getGroup();
        assertThat(groups, hasSize(1));
        final Group group = groups.get(0);
        assertThat(group.getName(), is("Mod Name"));
    }

    @Test
    void the_group_should_have_3_plugins() {
        for (final Plugin plugin : plugins()) {
            assertThat(plugin.getContent(), hasSize(3));
        }
    }

    @Test
    void the_plugin_name_should_fit_the_archive() {
        final List<String> pluginNames = new ArrayList<>();
        final ArgumentCaptor<FileSystemItem> captor = new ArgumentCaptor<>(FileSystemItem.class);

        final List<FileSystemItem> mandatoryItems = new ArrayList<>();
        mandatoryItems.add(ArchiveDirectoryCrawlerTest.item("Items.Q005_Johnny_Pants"));
        mandatoryItems.add(ArchiveDirectoryCrawlerTest.item("Items.Preset_Omaha_Neon"));

        for (final Plugin plugin : plugins()) {
            pluginNames.add(plugin.getName());
            assertThat(getFileSystemItems(plugin), containsInAnyOrder(samePropertyValuesAs(mandatoryItems.get(0)),
                    samePropertyValuesAs(mandatoryItems.get(1)), captor));
        }

        //@formatter:off
        assertThat(
                zipToMap(pluginNames, captor.getCaptures()),
                allOf(
                        aMapWithSize(4),
                        itemNameMatcher("blue", "basegame_blue.archive"),
                        itemNameMatcher("red", "basegame_red.archive"),
                        itemNameMatcher("yellow", "basegame_yellow.archive"),
                        itemNameMatcher("green", "basegame_green.archive")));
        //@formatter:on
    }

    @Test
    void the_plugin_should_have_the_expected_description() {
        for (final Plugin plugin : plugins()) {
            final String description = plugin.getContent()
                    .stream()
                    .map(JAXBElement::getValue)
                    .filter(x -> x instanceof String)
                    .map(x -> (String) x)
                    .collect(toSingleton());
            assertThat(description, is(expectedDescription));
        }
    }

    @Test
    void the_plugin_should_have_the_correct_image() {

        final List<String> pluginNames = new ArrayList<>();
        final List<String> imagePaths = new ArrayList<>();

        for (final Plugin plugin : plugins()) {
            pluginNames.add(plugin.getName());
            imagePaths.add(plugin.getContent()
                    .stream()
                    .map(JAXBElement::getValue)
                    .filter(x -> x instanceof Image)
                    .map(x -> (Image) x)
                    .map(Image::getPath)
                    .collect(toSingleton()));

        }
        //@formatter:off
            assertThat(
                zipToMap(pluginNames,imagePaths),
                allOf(
                        aMapWithSize(4),
                        hasEntry("blue", "basegame_blue.png"),
                        hasEntry("red", "basegame_red.png"),
                        hasEntry("yellow", "common.png"),
                        hasEntry("green", "common.png"))
        );
        //@formatter:on
    }

    @Test
    void should_write_info_to_markers() throws IOException {
        for (final File maker : makers) {
            final List<String> lines = FileUtils.readLines(maker, UTF_8);
            assertThat(lines,is(hasSize(5)));
        }
    }

    private Group group() {
        return step.getOptionalFileGroups().getGroup().get(0);
    }

    private List<Plugin> plugins() {
        return group().getPlugins().getPlugin();
    }

    private List<FileSystemItem> getFileSystemItems(final Plugin plugin) {
        return plugin.getContent()
                .stream()
                .map(JAXBElement::getValue)
                .filter(x -> x instanceof FileList)
                .map(x -> ((FileList) x))
                .collect(toSingleton())
                .getFileOrFolder()
                .stream()
                .map(JAXBElement::getValue)
                .collect(toList());
    }

}
