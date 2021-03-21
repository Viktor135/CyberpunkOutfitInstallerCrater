package de.vsc.coi.builder;

import static de.vsc.coi.builder.ObjectFactory.FACTORY;
import static de.vsc.coi.utils.Utils.getOrNew;

import fomod.Group;
import fomod.Plugin;

public class GroupBuilder {

    private final Group group;

    protected GroupBuilder() {
        group = new Group();
    }

    public static GroupBuilder builder() {
        return new GroupBuilder();
    }

    public Group build() {
        return group;
    }

    public GroupBuilder name(final String name) {
        group.setName(name);
        return this;
    }

    public GroupBuilder type(final SelectionMode type) {
        group.setType(type.getValue());
        return this;
    }

    public GroupBuilder addPlugin(final Plugin plugin) {
        getOrNew(group::getPlugins, group::setPlugins, FACTORY::createPluginList).getPlugin().add(plugin);
        return this;
    }

    public boolean hasNoPlugins() {
        return group.getPlugins().getPlugin().isEmpty();
    }

}
