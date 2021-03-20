package de.vsc.coi;

import static lombok.AccessLevel.PRIVATE;

import java.util.Set;

import javax.validation.constraints.NotBlank;

import com.tngtech.configbuilder.ConfigBuilder;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertiesFiles;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertyLocations;
import com.tngtech.configbuilder.annotation.validation.Validation;
import com.tngtech.configbuilder.annotation.valueextractor.CommandLineValue;
import com.tngtech.configbuilder.annotation.valueextractor.DefaultValue;
import com.tngtech.configbuilder.annotation.valueextractor.PropertyValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * How it works? See: https://github.com/TNG/config-builder#how-to-import-an-existing-config
 */
@PropertiesFiles("config")
@PropertyLocations(directories = "./")
@Getter
@Builder(builderClassName = "AppConfigBuilder")
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
@EqualsAndHashCode
@ToString
@SuppressWarnings("unused")
public class Config {

    private static Config config;

    @PropertyValue("ignores")
    @DefaultValue("fomod")
    private Set<String> defaultIgnores;

    @NotBlank
    @DefaultValue(".archive")
    @PropertyValue("modFileEnding")
    @CommandLineValue(shortOpt = "mfe", longOpt = "modFileEnding", hasArg = true)
    private String modFileEnding;

    @NotBlank
    @DefaultValue("\\archive\\pc\\patch\\")
    @PropertyValue("modFileDir")
    @CommandLineValue(shortOpt = "mfd", longOpt = "modFileDir", hasArg = true)
    private String modFileDir;

    @NotBlank
    @DefaultValue("common")
    @PropertyValue("commonImageName")
    @CommandLineValue(shortOpt = "cin", longOpt = "commonImageName", hasArg = true)
    private String commonImageName;

    @NotBlank
    @DefaultValue("module")
    @PropertyValue("moduleImageName")
    @CommandLineValue(shortOpt = "min", longOpt = "moduleImageName", hasArg = true)
    private String moduleImageName;

    @NotBlank
    @DefaultValue("ON")
    @PropertyValue("flagDependencyValue")
    private String flagDependencyValue;

    @NotBlank
    @DefaultValue("ignore")
    @PropertyValue("ignoreFileName")
    private String ignoreFileName;

    public static Config config() {
        if (config == null) {
            throw new IllegalStateException("The config has to be initialised before usage.");
        }
        return config;
    }

    public static Config init(final String... args) {
        config = ConfigBuilder.on(Config.class).withCommandLineArgs(args).build();
        return config;
    }

    @Validation
    private void validate() {
        defaultIgnores.add("fomod");
        defaultIgnores.add(ignoreFileName);
    }
}