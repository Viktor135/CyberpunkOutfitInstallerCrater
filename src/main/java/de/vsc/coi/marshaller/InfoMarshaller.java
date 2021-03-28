package de.vsc.coi.marshaller;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import de.vsc.coi.Info;

public class InfoMarshaller extends BaseMarshaller<Info> {

    public InfoMarshaller(final File outDir) {
        super(Info.class, outDir);
    }

    @Override
    public Object validate() throws JAXBException, SAXException {
        return JAXBContext.newInstance(forClass).createUnmarshaller().unmarshal(outputFile);
    }

    @Override
    public String fileName() {
        return "info.xml";
    }

    @Override
    public String schema() {
        throw new UnsupportedOperationException("Currently no schema for the info.xml is available!");
    }
}
