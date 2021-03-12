package de.vsc.coi;

import static java.util.Collections.singletonList;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import de.vsc.coi.FileCrawler.SelectionMode;
import de.vsc.coi.InstallStep.OptionalFileGroups.Group;
import de.vsc.coi.PluginList.Plugin.ConditionFlag;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "installStep")
public class InstallStep {

    @XmlAttribute
    public String name;

    @XmlElement(name = "visible")
    public Visible visible;

    @XmlElement(name = "optionalFileGroups")
    public List<OptionalFileGroups> optionalFileGroups;

    public InstallStep(final String name, final PluginList pluginList) {
        this.name = name;
        this.optionalFileGroups = singletonList(
                new InstallStep.OptionalFileGroups(singletonList(new Group(name, SelectionMode.DEFAULT.name(),
                        pluginList))));
    }

    public InstallStep(final String name, final PluginList pluginList,final SelectionMode mode) {
        this.name = name;
        this.optionalFileGroups = singletonList(
                new InstallStep.OptionalFileGroups(singletonList(new Group(name, mode.name(),
                        pluginList))));
    }

    public void addDependency(final ConditionFlag flag) {
        this.visible = new Visible(new Visible.Dependencies(new Visible.Dependencies.FlagDependency(flag)));
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "optionalFileGroups")
    public static class OptionalFileGroups  {

        @XmlAttribute
        public String order = "Explicit";

        @XmlElement
        public List<Group> group;

        public OptionalFileGroups(final List<Group> group) {
            this.group = group;
        }

        @NoArgsConstructor
        @AllArgsConstructor
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlRootElement(name = "group")
        public static class Group {

            @XmlAttribute
            public String name;

            @XmlAttribute
            public String type = "SelectAtMostOne";

            @XmlElement
            public PluginList plugins;

        }

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "visible")
    public static class Visible {

        @XmlElement
        public Dependencies dependencies;

        @NoArgsConstructor
        @AllArgsConstructor
        @ToString
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlRootElement(name = "dependencies")
        public static class Dependencies {

            @XmlAttribute
            public String operator = "AND";

            @XmlElement
            public FlagDependency flagDependency;

            public Dependencies(final FlagDependency flagDependency) {
                this.flagDependency = flagDependency;
            }

            @NoArgsConstructor
            @AllArgsConstructor
            @ToString
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlRootElement(name = "flagDependency")
            public static class FlagDependency {

                @XmlAttribute
                public String flag;

                @XmlAttribute
                public String value;

                public FlagDependency(final ConditionFlag flag) {
                    this.flag = flag.name;
                    this.value = flag.value;
                }
            }
        }

    }

}
