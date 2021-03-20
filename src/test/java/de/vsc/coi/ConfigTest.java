package de.vsc.coi;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.tngtech.configbuilder.ConfigBuilder;
import com.tngtech.configbuilder.exception.ValidatorException;

class ConfigTest {

    @Test
    void should_contain_the_defaults() {
        final Config expectedConfig = Config.builder()
                .defaultIgnores(Set.of("fomod"))
                .modFileEnding(".archive")
                .modFileDir("\\archive\\pc\\patch\\")
                .commonImageName("common")
                .moduleImageName("module")
                .build();
        assertThat(Config.init(), is(expectedConfig));
    }

    @Test
    void should_correctly_read_the_config_file() {
        final Config expectedConfig = Config.builder()
                .defaultIgnores(Set.of("ignore1", "some stupid file", "some other", "fomod"))
                .modFileEnding(".notAnArchive")
                .modFileDir("/archive/xbox/patch/")
                .commonImageName("not so common")
                .moduleImageName("some other module image")
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
    void should_throw_if_not_initialised() {
        assertThrows(IllegalStateException.class, Config::config);
    }

}