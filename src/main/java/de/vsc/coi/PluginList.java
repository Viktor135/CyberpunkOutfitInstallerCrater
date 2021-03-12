package de.vsc.coi;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "plugins")
public class PluginList {

    @XmlAttribute
    public String order = "Explicit";

    @XmlElement(name = "plugin")
    public List<Plugin> plugins = new ArrayList<>();

    @AllArgsConstructor
    @ToString
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "plugin")
    public static class Plugin {

        @XmlAttribute
        public String name;

        @XmlElement
        public String description;
        @XmlElement
        public Image image;

        @XmlElementWrapper(name = "conditionFlags")
        @XmlElement(name = "flag")
         public List<ConditionFlag> conditionFlags;

        @XmlElementWrapper(name = "files")
        @XmlElement(name = "file")
         public List<FileToCopy> filesToCopie;

        @XmlElementWrapper(name = "typeDescriptor")
        @XmlElement(name = "type")
         public List<TypeDescriptor> typeDescriptors;

        public Plugin(){
            conditionFlags= new ArrayList<>();
            typeDescriptors = new ArrayList<>();
            typeDescriptors.add(new TypeDescriptor());
            filesToCopie = new ArrayList<>();
        }

        public ConditionFlag addNameBasedCondition(){
            Objects.requireNonNull(name);
            final ConditionFlag flag = new ConditionFlag(name,"ON");
            this.conditionFlags.add(flag);
            return flag;
        }

        @NoArgsConstructor
        @AllArgsConstructor
        @ToString
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlRootElement(name = "flag")
         public static class ConditionFlag{

            @XmlAttribute
            public String name;

            @XmlValue
            public String value;

         }


        @NoArgsConstructor
        @AllArgsConstructor
        @ToString
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlRootElement(name = "file")
         public static class FileToCopy{
            @XmlAttribute
            public String source;

            @XmlAttribute
            public String destination;

            @XmlAttribute
            public String priority = "0";
         }


        @NoArgsConstructor
        @AllArgsConstructor
        @ToString
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlRootElement(name = "file")
        public static class TypeDescriptor{

            @XmlAttribute
            public String name= "Optional";

        }

    }

}
