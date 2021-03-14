package de.vsc.coi.builder;

import static de.vsc.coi.Config.config;
import static fomod.OrderEnum.EXPLICIT;

import javax.xml.bind.JAXBElement;

import fomod.CompositeDependency;
import fomod.FileDependency;
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

    public FlagDependency createFlagDependency(final String flag) {
        final FlagDependency flagDependency = super.createFlagDependency();
        flagDependency.setFlag(flag);
        flagDependency.setValue(config().getFlagDependencyValue());
        return flagDependency;
    }

    public JAXBElement<FlagDependency> createCDFlag(final String flag) {
        return super.createCompositeDependencyFlagDependency(createFlagDependency(flag));
    }

    public JAXBElement<FileDependency> createCDFile(final String file,final FileDependencyState state) {
        return super.createCompositeDependencyFileDependency(createFileDependency(file,state));
    }

    public FileDependency createFileDependency(final String file,final FileDependencyState state) {
        final FileDependency fileDependency = super.createFileDependency();
        fileDependency.setFile(file);
        fileDependency.setState(state.getValue());
        return fileDependency;
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
