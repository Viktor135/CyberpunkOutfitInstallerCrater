package de.vsc.coi;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "config")
public class ModuleConfig {

    public static final String FILE_NAME= "ModuleConfig.xml";

    @XmlElement(name = "moduleName")
    public String moduleName;

    @XmlElement(name = "moduleImage")
    public ModuleImage moduleImage;
    @XmlElement(name = "installSteps")
    public InstallSteps installSteps;


    public void marshal(final String path) throws JAXBException, IOException {
        final Marshaller jaxbMarshaller = JAXBContext.newInstance(ModuleConfig.class).createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        final File f = new File(path, FILE_NAME);
        f.createNewFile();
        jaxbMarshaller.marshal(this, f);
    }
}
