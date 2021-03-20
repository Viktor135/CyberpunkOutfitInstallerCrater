package de.vsc.coi.builder;

import static de.vsc.coi.builder.ObjectFactory.FACTORY;
import static de.vsc.coi.utils.Utils.getOrNew;

import java.util.Collection;

import javax.xml.bind.JAXBElement;

import fomod.CompositeDependency;
import fomod.FileList;
import fomod.Group;
import fomod.InstallStep;
import fomod.Plugin;
import fomod.PluginList;

public class InstallStepBuilder extends SubBuilder<ModuleConfigurationBuilder, InstallStep> {

    private final InstallStep step;

    private final ModuleConfigurationBuilder parent;
    private CompositeDependency dependencies;

    protected InstallStepBuilder(final ModuleConfigurationBuilder parent) {
        super(parent);
        this.parent = parent;
        step = FACTORY.createInstallStep();
    }

    public static InstallStepBuilder builder() {
        return new InstallStepBuilder(null);
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

    public GroupBuilder newGroup() {
        final GroupBuilder builder = new GroupBuilder(this);
        addGroup(builder.getEntity());
        return builder;
    }

    public FileSystemItemBuilder newFileToCopy() {
        final FileSystemItemBuilder file = FileSystemItemBuilder.builder();
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
                .forEach(list -> list.add(FACTORY.createFileListFile(file.getEntity())));

        return file;
    }

    @Override
    protected InstallStep getEntity() {
        return step;
    }

}
