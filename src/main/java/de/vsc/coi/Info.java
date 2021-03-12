package de.vsc.coi;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "fomod")
public class Info {

    public static final String FILE_NAME = "info.xml";

    @XmlElement
    public String Name;
    @XmlElement
    public String Author;
    @XmlElement
    public String Version;
    @XmlElement
    public String Website;
    @XmlElement
    public String Description;

    @XmlElementWrapper(name = "Groups")
    @XmlElement(name = "element")
    public List<String> Groups;

    public void marshal(final String path) throws JAXBException, IOException {
        final Marshaller jaxbMarshaller = JAXBContext.newInstance(Info.class).createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        final File f = new File(path, FILE_NAME);
        f.createNewFile();
        jaxbMarshaller.marshal(this, f);
    }

}
