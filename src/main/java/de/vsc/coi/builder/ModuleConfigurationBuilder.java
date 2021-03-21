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

import fomod.InstallStep;
import fomod.ModuleConfiguration;
import fomod.StepList;

public class ModuleConfigurationBuilder {

    public static final String FILE_NAME = "ModuleConfig.xml";
    private static final String configTagReplacement =
            "<config xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                    + "xsi:noNamespaceSchemaLocation=\"http://qconsulting.ca/fo3/ModConfig5.0.xsd\">";
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

    public void marshal(final File outDir) throws JAXBException, IOException, URISyntaxException, SAXException {
        final JAXBElement<ModuleConfiguration> config = FACTORY.createConfig(build());
        final JAXBContext jaxbContext = JAXBContext.newInstance(ModuleConfiguration.class);
        final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        final File outputFile = new File(outDir, FILE_NAME);
        outputFile.createNewFile();
        jaxbMarshaller.marshal(config, outputFile);

        // Awful post processing...
        final List<String> lines = readFile(outputFile);
        boolean found = false;
        for (int i = 0; !found && i < lines.size(); i++) {
            if (containsIgnoreCase(lines.get(i), "<config>")) {
                lines.set(i, StringUtils.replaceIgnoreCase(lines.get(i), "<config>", configTagReplacement));
                found = true;
            }
        }
        FileUtils.writeLines(outputFile, lines);

        //Validation:
        //Create Unmarshaller
        final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        //Setup schema validator
        final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        final File schema = new File(
                ModuleConfigurationBuilder.class.getClassLoader().getResource("fomod-schema/ModuleConfig.xsd").toURI());
        final Schema employeeSchema = sf.newSchema(schema);
        jaxbUnmarshaller.setSchema(employeeSchema);

        //Unmarshal xml file
        final Object obj = jaxbUnmarshaller.unmarshal(outputFile);

    }
}
