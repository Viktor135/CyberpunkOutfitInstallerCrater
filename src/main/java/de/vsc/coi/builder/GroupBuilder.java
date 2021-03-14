package de.vsc.coi.builder;

import static de.vsc.coi.Utils.getOrNew;
import static de.vsc.coi.builder.ObjectFactory.FACTORY;

import de.vsc.coi.Utils;
import fomod.Group;
import fomod.Plugin;

public class GroupBuilder extends SubBuilder<InstallStepBuilder,Group>{

    private final Group group;

    protected GroupBuilder(final InstallStepBuilder parent) {
        super(parent);
        group = new Group();
    }

    @Override
    protected Group getEntity() {
        return group;
    }

    public static GroupBuilder builder(){
        return new GroupBuilder(null);
    }

    public GroupBuilder name(final String name){
        group.setName(name);
        return this;
    }

    public GroupBuilder type(final SelectionMode type){
        group.setType(type.getValue());
        return this;
    }

    public GroupBuilder addPlugin(final Plugin plugin){
        getOrNew(group::getPlugins,group::setPlugins, FACTORY::createPluginList)
                .getPlugin().add(plugin);
        return this;
    }

    public PluginBuilder newPlugin(){
        final PluginBuilder builder = new PluginBuilder(this);
        addPlugin(builder.getEntity());
        return builder;
    }

    public boolean hasNoPlugins(){
        return group.getPlugins().getPlugin().isEmpty();
    }


}
