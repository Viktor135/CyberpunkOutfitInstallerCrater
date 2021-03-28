package de.vsc.coi;

import static java.lang.String.join;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@XmlRootElement(name = "fomod")
public class Info {

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

    public String getGroupsAsString() {
        if (this.getGroups() == null) {
            return "";
        }
        return join(", ", this.getGroups());
    }

    public void setGroupsFromString(final String x) {
        this.setGroups(Arrays.stream(x.split(",")).map(String::trim).collect(Collectors.toList()));
    }

}
