package de.vsc.coi.builder;

import static de.vsc.coi.builder.ObjectFactory.FACTORY;
import static de.vsc.coi.config.Workspace.relativize;
import static de.vsc.coi.crawlers.ArchiveDirectoryCrawler.gameArchiveFolder;
import static de.vsc.coi.utils.Utils.getOrNew;

import java.io.File;
import java.util.Collection;

import javax.xml.bind.JAXBElement;

import fomod.CompositeDependency;
import fomod.FileList;
import fomod.FileSystemItem;
import fomod.Group;
import fomod.InstallStep;
import fomod.Plugin;
import fomod.PluginList;

public class InstallStepBuilder {

    private final InstallStep step;
    private CompositeDependency dependencies;

    protected InstallStepBuilder() {
        step = FACTORY.createInstallStep();
    }

    public static InstallStepBuilder builder() {
        return new InstallStepBuilder();
    }

    public InstallStepBuilder name(final String name) {
        step.setName(name);
        return this;
    }

    private void addDependency(final JAXBElement<?> toAdd) {
        if (dependencies == null) {
            dependencies = FACTORY.createAndDependency();
            getOrNew(step::getVisible, step::setVisible,
                    FACTORY::createAndDependency).getFileDependencyOrFlagDependencyOrGameDependency()
                    .add(FACTORY.createCompositeDependencyDependencies(dependencies));
        }
        dependencies.getFileDependencyOrFlagDependencyOrGameDependency().add(toAdd);
    }

    public InstallStepBuilder addFlagDependency(final String flag) {
        if (flag != null) {
            addDependency(FACTORY.createCDFlag(flag));
        }
        return this;
    }

    public InstallStepBuilder addFileDependency(final String flag, final FileDependencyState state) {
        if (flag != null) {
            //@formatter:off
            addDependency(FACTORY.createCDFile(flag,state));
            //@formatter:on
        }
        return this;
    }

    public InstallStepBuilder addGroup(final Group group) {
        getOrNew(step::getOptionalFileGroups, step::setOptionalFileGroups, FACTORY::createGroupList).getGroup()
                .add(group);
        return this;
    }

    public InstallStepBuilder addFileToCopy(final FileSystemItem file) {
        step.getOptionalFileGroups()
                .getGroup()
                .stream()
                .map(Group::getPlugins)
                .map(PluginList::getPlugin)
                .flatMap(Collection::stream)
                .map(Plugin::getContent)
                .flatMap(Collection::stream)
                .map(JAXBElement::getValue)
                .filter(x -> x instanceof FileList)
                .map(x -> (FileList) x)
                .map(FileList::getFileOrFolder)
                .forEach(list -> list.add(FACTORY.createFileListFile(file)));

        return this;
    }

    public InstallStep build() {
        return step;
    }

}
