package de.vsc.coi.builder;

import static de.vsc.coi.builder.ObjectFactory.FACTORY;
import static de.vsc.coi.utils.FIleReaderUtils.readFile;
import static de.vsc.coi.utils.Utils.getOrNew;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

import de.vsc.coi.ConfigMarshaller;
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
