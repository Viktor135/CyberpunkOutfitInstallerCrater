package de.vsc.coi.marshaller;

import static de.vsc.coi.builder.ObjectFactory.FACTORY;
import static de.vsc.coi.utils.FileReaderUtils.readFile;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import de.vsc.coi.marshaller.BaseMarshaller;
import fomod.ModuleConfiguration;

public class ConfigMarshaller extends BaseMarshaller<ModuleConfiguration> {

    private static final String configTagReplacement =
            "<config xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                    + "xsi:noNamespaceSchemaLocation=\"http://qconsulting.ca/fo3/ModConfig5.0.xsd\">";

    public ConfigMarshaller(final File outDir) {
        super(ModuleConfiguration.class, outDir);
    }

    public void marshal(final ModuleConfiguration moduleConfiguration) throws JAXBException, IOException {
        marshal(FACTORY.createConfig(moduleConfiguration));
    }

    @Override
    public void marshal(final Object toMarshall) throws JAXBException, IOException {
        super.marshal(toMarshall);
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

    @Override
    public String schema() {
        return "fomod-schema/ModuleConfig.xsd";
    }

    @Override
    public String fileName() {
        return "ModuleConfig.xml";
    }
}
