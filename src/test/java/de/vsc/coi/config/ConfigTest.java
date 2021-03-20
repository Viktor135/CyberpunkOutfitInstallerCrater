package de.vsc.coi.config;

import static de.vsc.coi.config.Config.config;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.tngtech.configbuilder.ConfigBuilder;
import com.tngtech.configbuilder.exception.ValidatorException;

class ConfigTest {

    @TempDir
    File dir;

    @Test
    void should_throw_if_not_initialised() {
        Config.clean();
        assertThrows(IllegalStateException.class, Config::config);
    }

    @Test
    void should_contain_the_defaults() {
        final Config expectedConfig = Config.builder()
                .defaultIgnores(Set.of("fomod","ignore"))
                .modFileEnding(".archive")
                .modFileDir("\\archive\\pc\\patch\\")
                .commonImageName("common")
                .moduleImageName("module")
                .flagDependencyValue("ON")
                .ignoreFileName("ignore")
                .build();
        assertThat(Config.init(), is(expectedConfig));
    }

    @Test
    void should_correctly_read_the_config_file() {
        final Config expectedConfig = Config.builder()
                .defaultIgnores(Set.of("ignore1", "some stupid file", "some other", "fomod","ignore"))
                .modFileEnding(".notAnArchive")
                .modFileDir("/archive/xbox/patch/")
                .commonImageName("not so common")
                .moduleImageName("some other module image")
                .flagDependencyValue("ON")
                .ignoreFileName("ignore")
                .build();
        final Config actualConfig = ConfigBuilder.on(Config.class).withPropertyLocations("src/test/resources").build();
        assertThat(actualConfig, is(expectedConfig));
    }

    @Test
    void workspace_path_should_not_be_blank() {
        final String[] args = new String[] {"-w", "\n\t"};
        assertThrows(ValidatorException.class, () -> Config.init(args));
    }

    @Test
    void should_correctly_read_the_workspace_from_args() {
        final File workspace = new File(dir, "CP_Workspace");
        workspace.mkdirs();
        final String[] args = new String[] {"-w", workspace.getPath()};
        Config.init(args);

        assertThat(config().getWorkspacePath(), is(workspace.getPath()));
    }

    @Test
    void should_not_accept_blank_workspace() {
        final String[] args = new String[] {"-w", "   "};
        assertThrows(ValidatorException.class, () -> Config.init(args));
    }
}