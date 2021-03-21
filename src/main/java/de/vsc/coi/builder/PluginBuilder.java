package de.vsc.coi.builder;

import static de.vsc.coi.builder.ObjectFactory.FACTORY;
import static de.vsc.coi.config.Config.config;

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

public class PluginBuilder {

    private String name;
    private JAXBElement<String> description;
    private JAXBElement<Image> image;
    private JAXBElement<FileList> files;
    private JAXBElement<ConditionFlagList> conditionFlags;
    private JAXBElement<PluginTypeDescriptor> typeDescriptor;

    protected PluginBuilder() {
    }

    public static PluginBuilder builder() {
        return new PluginBuilder();
    }

    public PluginBuilder name(final String name) {
        this.name = name;
        return this;
    }

    public PluginBuilder description(final String desc) {
        if (this.description == null) {
            this.description = FACTORY.createPluginDescription(desc);
        } else {
            this.description.setValue(desc);
        }
        return this;
    }

    public PluginBuilder image(final String path) {
        if (this.image == null) {
            this.image = FACTORY.createPluginImage(path);
        } else {
            this.image.getValue().setPath(path);
        }
        return this;
    }

    public PluginBuilder setImageIfNotPresent(final Optional<String> path) {
        if (image == null && path.isPresent()) {
            image(path.get());
        }
        return this;
    }

    private void addFileSystemItem(final JAXBElement<FileSystemItem> item) {
        if (this.files == null) {
            this.files = FACTORY.createPluginFiles(FACTORY.createFileList());
        }
        this.files.getValue().getFileOrFolder().add(item);

    }

    public PluginBuilder addFile(final FileSystemItem file) {
        addFileSystemItem(FACTORY.createFileListFile(file));
        return this;
    }

    public PluginBuilder addFolder(final FileSystemItem folder) {
        addFileSystemItem(FACTORY.createFileListFolder(folder));
        return this;
    }

    public PluginBuilder addConditionFlag(final String name, final String value) {
        if (conditionFlags == null) {
            conditionFlags = FACTORY.createPluginConditionFlags(FACTORY.createConditionFlagList());
        }
        conditionFlags.getValue().getFlag().add(FACTORY.createSetConditionFlag(name, value));
        return this;
    }

    public PluginBuilder addConditionFlag(final String name) {
        return addConditionFlag(name, config().getFlagDependencyValue());
    }

    public PluginBuilder typeOptional() {
        final PluginTypeDescriptor typeDescriptor = FACTORY.createPluginTypeDescriptor();
        final PluginType type = FACTORY.createPluginType();
        type.setName(PluginTypeEnum.OPTIONAL);
        typeDescriptor.setType(type);
        return pluginType(typeDescriptor);
    }

    public PluginBuilder pluginType(final PluginTypeDescriptor type) {
        if (typeDescriptor == null) {
            typeDescriptor = FACTORY.createPluginTypeDescriptor(type);
        } else {
            typeDescriptor.setValue(type);
        }
        return this;
    }

    public Plugin build() {
        final Plugin plugin = FACTORY.createPlugin();
        if (typeDescriptor == null) {
            typeOptional();
        }

        plugin.setName(name);
        // The order is important !!!
        Optional.ofNullable(description).ifPresent(x -> plugin.getContent().add(x));
        Optional.ofNullable(image).ifPresent(x -> plugin.getContent().add(x));
        Optional.ofNullable(files).ifPresent(x -> plugin.getContent().add(x));
        Optional.ofNullable(conditionFlags).ifPresent(x -> plugin.getContent().add(x));
        plugin.getContent().add(typeDescriptor);

        typeOptional();
        return plugin;
    }
}
