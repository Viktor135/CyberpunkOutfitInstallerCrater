package de.vsc.coi.marshaller;

import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import de.vsc.coi.builder.ModuleConfigurationBuilder;

public abstract class BaseMarshaller<T> {

    protected final Class<T> forClass;
    protected final File outputFile;

    public BaseMarshaller(final Class<T> forClass, final File outDir) {
        this.forClass = forClass;
        this.outputFile = new File(outDir, fileName());
    }

    public abstract String fileName();

    public void marshal(final Object toMarshall) throws JAXBException, IOException {
        outputFile.createNewFile();

        final Marshaller jaxbMarshaller = JAXBContext.newInstance(forClass).createMarshaller();
        jaxbMarshaller.setProperty(JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        jaxbMarshaller.marshal(toMarshall, outputFile);
    }

    public Object validate() throws JAXBException, SAXException {
        //Create Unmarshaller
        final Unmarshaller jaxbUnmarshaller = JAXBContext.newInstance(forClass).createUnmarshaller();

        //Setup schema validator
        final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        final Schema schema = sf.newSchema(getSchemaAsFile());
        jaxbUnmarshaller.setSchema(schema);

        //Unmarshal xml file
        return jaxbUnmarshaller.unmarshal(outputFile);
    }

    public URL getSchemaAsFile() {
        return Objects.requireNonNull(ModuleConfigurationBuilder.class.getClassLoader().getResource(schema()));
    }

    public abstract String schema();

    public File getOutputFile() {
        return outputFile;
    }
}
