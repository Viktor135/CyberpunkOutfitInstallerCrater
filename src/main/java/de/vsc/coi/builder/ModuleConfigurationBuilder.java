package de.vsc.coi.builder;

import static de.vsc.coi.Utils.getOrNew;
import static de.vsc.coi.builder.ObjectFactory.FACTORY;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import fomod.InstallStep;
import fomod.ModuleConfiguration;
import fomod.StepList;

public class ModuleConfigurationBuilder {

    public static final String FILE_NAME = "ModuleConfig.xml";
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

    public InstallStepBuilder newStep() {
        final InstallStepBuilder builder = new InstallStepBuilder(this);
        addStep(builder.getEntity());
        return builder;
    }

    public ModuleConfiguration build() {
        return moduleConfig;
    }

    public void marshal(final File outDir) throws JAXBException, IOException {
        final JAXBElement<ModuleConfiguration> config = FACTORY.createConfig(build());
        final Marshaller jaxbMarshaller = JAXBContext.newInstance(ModuleConfiguration.class).createMarshaller();
        jaxbMarshaller.setProperty(JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        final File f = new File(outDir, FILE_NAME);
        f.createNewFile();
        jaxbMarshaller.marshal(config, f);
    }
}
