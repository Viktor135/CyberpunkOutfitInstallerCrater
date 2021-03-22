package de.vsc.coi.builder;

import static de.vsc.coi.builder.ObjectFactory.FACTORY;
import static de.vsc.coi.utils.Utils.getOrNew;

import java.util.Optional;

import fomod.InstallStep;
import fomod.ModuleConfiguration;
import fomod.StepList;

public class ModuleConfigurationBuilder {


    private final ModuleConfiguration moduleConfig;

    private ModuleConfigurationBuilder() {
        moduleConfig = FACTORY.createModuleConfiguration();
    }

    public static ModuleConfigurationBuilder builder() {
        return new ModuleConfigurationBuilder();
    }

    public ModuleConfigurationBuilder name(final String name) {
        getOrNew(moduleConfig::getModuleName, moduleConfig::setModuleName, FACTORY::createModuleTitle).setValue(name);
        return this;
    }

    public ModuleConfigurationBuilder image(final Optional<String> path) {
        path.ifPresent(this::image);
        return this;
    }

    public ModuleConfigurationBuilder image(final String path) {
        getOrNew(moduleConfig::getModuleImage, moduleConfig::setModuleImage, FACTORY::createHeaderImage).setPath(path);
        return this;
    }

    public ModuleConfigurationBuilder addStep(final InstallStep step) {
        final StepList list = getOrNew(moduleConfig::getInstallSteps, moduleConfig::setInstallSteps,
                FACTORY::createStepList);
        list.getInstallStep().add(step);
        return this;
    }

    public ModuleConfiguration build() {
        return moduleConfig;
    }
}
