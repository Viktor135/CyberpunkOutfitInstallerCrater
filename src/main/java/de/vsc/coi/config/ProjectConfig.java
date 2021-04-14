package de.vsc.coi.config;

import static lombok.AccessLevel.PRIVATE;

import javax.validation.constraints.NotBlank;

import com.tngtech.configbuilder.ConfigBuilder;
import com.tngtech.configbuilder.annotation.configuration.PropertyNamePrefix;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertiesFiles;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertyLocations;
import com.tngtech.configbuilder.annotation.valueextractor.PropertyValue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@PropertiesFiles("project")
@PropertyLocations(directories = "./", fromClassLoader = true)
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
@Getter
public class ProjectConfig {

    private static ProjectConfig projectConfig;

    @PropertyValue("project.version")
    @NotBlank
    private String version;
    @PropertyValue("project.lookup.url")
    @NotBlank
    private String lookupUrl;

    @PropertyValue("project.nexus.url")
    @NotBlank
    private String nexusUrl;

    public static ProjectConfig project() {
        if (projectConfig == null) {
            projectConfig = ConfigBuilder.on(ProjectConfig.class).build();
        }
        return projectConfig;
    }

}
