package de.vsc.coi.config;

import static lombok.AccessLevel.PRIVATE;

import java.util.Set;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.VisibleForTesting;

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
 * How it works? See: https://github.com/TNG/config-builder
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

    @CommandLineValue(shortOpt = "r", longOpt = "generateReport", hasArg = false)
    @DefaultValue("false")
    private boolean generateReport;

    @CommandLineValue(shortOpt = "w", longOpt = "workspace", hasArg = true)
    private String workspacePath;

    @NotBlank
    @DefaultValue("C:\\Program Files\\7-Zip\\7z.exe")
    @PropertyValue("7zip")
    private String sevenZipPath;

    public static Config config() {
        if (config == null) {
            throw new IllegalStateException("The config has to be initialised before usage.");
        }
        return config;
    }

    @VisibleForTesting
    protected static void clean(){
        config = null;
    }

    public static Config init(final String... args) {
        config = ConfigBuilder.on(Config.class).withCommandLineArgs(args).build();
        return config;
    }

    /**
     * Should only be used in {@link Workspace}.
     */
    protected String getWorkspacePath() {
        return workspacePath;
    }

    @Validation
    private void validate() {
        defaultIgnores.add("fomod");
        defaultIgnores.add(ignoreFileName);

        if (workspacePath != null) {
            if (StringUtils.isBlank(workspacePath)) {
                throw new IllegalStateException("The configured workspace has to be not not blank.");
            }
        }
    }
}