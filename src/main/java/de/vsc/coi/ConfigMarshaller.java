package de.vsc.coi;

import static de.vsc.coi.builder.ObjectFactory.FACTORY;
import static de.vsc.coi.utils.FileReaderUtils.readFile;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

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

import de.vsc.coi.builder.ModuleConfigurationBuilder;
import fomod.ModuleConfiguration;

public class ConfigMarshaller {

    public static final String FILE_NAME = "ModuleConfig.xml";
    private static final String configTagReplacement =
            "<config xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                    + "xsi:noNamespaceSchemaLocation=\"http://qconsulting.ca/fo3/ModConfig5.0.xsd\">";

    private final ModuleConfiguration configuration;
    private final File outputFile;

    public ConfigMarshaller(final ModuleConfiguration configuration, final File outDir) {
        this.configuration = configuration;
        this.outputFile = new File(outDir, FILE_NAME);
    }

    public void marshal() throws JAXBException, IOException {
        outputFile.createNewFile();

        final JAXBElement<ModuleConfiguration> config = FACTORY.createConfig(configuration);
        final Marshaller jaxbMarshaller = JAXBContext.newInstance(ModuleConfiguration.class).createMarshaller();
        jaxbMarshaller.setProperty(JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        jaxbMarshaller.marshal(config, outputFile);
        postprocessMarshalling();// Awful post processing...
    }

    private void postprocessMarshalling() throws IOException {
        final List<String> lines = readFile(outputFile);
        boolean found = false;
        for (int i = 0; !found && i < lines.size(); i++) {
            if (containsIgnoreCase(lines.get(i), "<config>")) {
                lines.set(i, StringUtils.replaceIgnoreCase(lines.get(i), "<config>", configTagReplacement));
                found = true;
            }
        }
        FileUtils.writeLines(outputFile, lines);
    }

    public void validate() throws JAXBException, SAXException, URISyntaxException {
        //Create Unmarshaller
        final Unmarshaller jaxbUnmarshaller = JAXBContext.newInstance(ModuleConfiguration.class).createUnmarshaller();

        //Setup schema validator
        final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        final Schema employeeSchema = sf.newSchema(getSchema());
        jaxbUnmarshaller.setSchema(employeeSchema);

        //Unmarshal xml file
        jaxbUnmarshaller.unmarshal(outputFile);
    }

    public File getSchema() throws URISyntaxException {
        return new File(Objects.requireNonNull(
                ModuleConfigurationBuilder.class.getClassLoader().getResource("fomod-schema/ModuleConfig.xsd"))
                .toURI());
    }

    public File getOutputFile() {
        return outputFile;
    }
}
