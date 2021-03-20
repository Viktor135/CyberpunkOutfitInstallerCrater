package de.vsc.coi.builder;

import static de.vsc.coi.config.Config.config;
import static de.vsc.coi.builder.ObjectFactory.FACTORY;

import java.util.Optional;

import javax.xml.bind.JAXBElement;

import fomod.ConditionFlagList;
import fomod.FileList;
import fomod.FileSystemItem;
import fomod.Image;
import fomod.Plugin;
import fomod.PluginType;
import fomod.PluginTypeDescriptor;
import fomod.PluginTypeEnum;

public class PluginBuilder extends SubBuilder<GroupBuilder, Plugin> {

    private final Plugin plugin;

    private JAXBElement<String> description;
    private JAXBElement<Image> image;
    private JAXBElement<FileList> files;
    private JAXBElement<ConditionFlagList> conditionFlags;
    private JAXBElement<PluginTypeDescriptor> typeDescriptor;

    protected PluginBuilder(final GroupBuilder parent) {
        super(parent);
        this.plugin = new Plugin();
    }

    public static PluginBuilder builder() {
        return new PluginBuilder(null);
    }

    @Override
    protected Plugin getEntity() {
        return plugin;
    }

    public PluginBuilder name(final String name) {
        plugin.setName(name);
        return this;
    }

    public PluginBuilder description(final String desc) {
        if (this.description == null) {
            this.description = FACTORY.createPluginDescription(desc);
            plugin.getContent().add(description);
        } else {
            description.setValue(desc);
        }
        return this;
    }

    public PluginBuilder image(final String path) {
        if (image == null) {
            image = FACTORY.createPluginImage(path);
            plugin.getContent().add(image);
        } else {
            image.getValue().setPath(path);
        }
        return this;
    }

    public PluginBuilder setImageIfNotPresent(final Optional<String> path){
        if(image == null && path.isPresent()){
            image(path.get());
        }
        return this;
    }

    private void addFileSystemItem(final JAXBElement<FileSystemItem> item) {
        if (files == null) {
            files = FACTORY.createPluginFiles(FACTORY.createFileList());
            this.plugin.getContent().add(files);
        }
        files.getValue().getFileOrFolder().add(item);
    }

    public PluginBuilder addFile(final FileSystemItem file) {
        addFileSystemItem(FACTORY.createFileListFile(file));
        return this;
    }

    public PluginBuilder addFolder(final FileSystemItem folder) {
        addFileSystemItem(FACTORY.createFileListFolder(folder));
        return this;
    }

    public FileSystemItemBuilder newFileSystemItem(){
        final FileSystemItemBuilder builder = new FileSystemItemBuilder(this);
        addFile(builder.getEntity());
        return builder;
    }

    public FileSystemItemBuilder newFolder(){
        final FileSystemItemBuilder builder = new FileSystemItemBuilder(this);
        addFolder(builder.getEntity());
        return builder;
    }

    public PluginBuilder addConditionFlag(final String name, final String value) {
        if (conditionFlags == null) {
            conditionFlags = FACTORY.createPluginConditionFlags(FACTORY.createConditionFlagList());
            this.plugin.getContent().add(conditionFlags);
        }
        conditionFlags.getValue().getFlag().add(FACTORY.createSetConditionFlag(name, value));
        return this;
    }

    public PluginBuilder addConditionFlag(final String name) {
       return addConditionFlag(name, config().getFlagDependencyValue());
    }

    public PluginBuilder typeOptional(){
        final PluginTypeDescriptor typeDescriptor = FACTORY.createPluginTypeDescriptor();
        final PluginType type = FACTORY.createPluginType();
        type.setName(PluginTypeEnum.OPTIONAL);
        typeDescriptor.setType(type);
        return pluginType(typeDescriptor);
    }

    public PluginBuilder pluginType(final PluginTypeDescriptor type) {
        if (typeDescriptor == null) {
            typeDescriptor = FACTORY.createPluginTypeDescriptor(type);
            plugin.getContent().add(typeDescriptor);
        } else {
            typeDescriptor.setValue(type);
        }
        return this;
    }

    @Override
    public Plugin build() {
        typeOptional();
        return super.build();
    }
}
