package de.vsc.coi.builder;

import static de.vsc.coi.Config.config;
import static fomod.OrderEnum.EXPLICIT;

import javax.xml.bind.JAXBElement;

import de.vsc.coi.Config;
import fomod.CompositeDependency;
import fomod.FlagDependency;
import fomod.GroupList;
import fomod.Image;
import fomod.PluginList;
import fomod.SetConditionFlag;
import fomod.StepList;

public class ObjectFactory extends fomod.ObjectFactory {

    public static final ObjectFactory FACTORY = new ObjectFactory();

    @Override
    public StepList createStepList() {
        final StepList list = super.createStepList();
        list.setOrder(EXPLICIT);
        return list;
    }

    public FlagDependency createFlag(final String flag) {
        final FlagDependency flagDependency = super.createFlagDependency();
        flagDependency.setFlag(flag);
        flagDependency.setValue(config().getFlagDependencyValue());
        return flagDependency;
    }

    public JAXBElement<FlagDependency> createCDFlagOn(final String flag) {
        return super.createCompositeDependencyFlagDependency(createFlag(flag));
    }

    public CompositeDependency createAndDependency() {
        final CompositeDependency dep = super.createCompositeDependency();
        dep.setOperator("AND");
        return dep;
    }

    @Override
    public GroupList createGroupList() {
        final GroupList list = super.createGroupList();
        list.setOrder(EXPLICIT);
        return list;
    }

    @Override
    public PluginList createPluginList() {
        final PluginList list = super.createPluginList();
        list.setOrder(EXPLICIT);
        return list;
    }

    public Image createImage(final String path) {
        final Image image = super.createImage();
        image.setPath(path);
        return image;
    }

    public JAXBElement<Image> createPluginImage(final String path) {
        return super.createPluginImage(createImage(path));
    }

    public SetConditionFlag createSetConditionFlag(final String name, final String value) {
        final SetConditionFlag setConditionFlag = super.createSetConditionFlag();
        setConditionFlag.setName(name);
        setConditionFlag.setValue(value);
        return setConditionFlag;
    }
}
