package de.vsc.coi.builder;

import static de.vsc.coi.Utils.getOrNew;
import static de.vsc.coi.builder.ObjectFactory.FACTORY;

import fomod.Group;
import fomod.InstallStep;

public class InstallStepBuilder extends SubBuilder<ModuleConfigurationBuilder, InstallStep> {

    private final InstallStep step;

    private final ModuleConfigurationBuilder parent;

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

    public InstallStepBuilder addFlagDependency(final String flag) {
        if (flag != null) {
            //@formatter:off
            getOrNew(step::getVisible, step::setVisible,FACTORY::createAndDependency)
                    .getFileDependencyOrFlagDependencyOrGameDependency()
                    .add(FACTORY.createCDFlagOn(flag));
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

    public ModuleConfigurationBuilder parent() {
        return parent;
    }

    @Override
    protected InstallStep getEntity() {
        return step;
    }

}
