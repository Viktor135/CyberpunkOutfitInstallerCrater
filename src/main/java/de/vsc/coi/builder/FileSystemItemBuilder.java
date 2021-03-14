package de.vsc.coi.builder;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAttribute;

import fomod.FileSystemItem;

public class FileSystemItemBuilder extends SubBuilder<PluginBuilder, FileSystemItem>{

   private final FileSystemItem item;

   public static FileSystemItemBuilder builder(){
       return new FileSystemItemBuilder(null);
   }

    protected FileSystemItemBuilder(final PluginBuilder parent) {
        super(parent);
        item = new FileSystemItem();
    }

    public FileSystemItemBuilder setSource(final String source) {
        this.item.setSource(source);;
        return this;
    }

    public FileSystemItemBuilder setDestination(final String destination) {
        this.item.setDestination(destination);
        return this;
    }

    public FileSystemItemBuilder setAlwaysInstall(final boolean alwaysInstall) {
        this.item.setAlwaysInstall(alwaysInstall);
        return this;
    }

    public FileSystemItemBuilder setInstallIfUsable(final boolean installIfUsable) {
        this.item.setInstallIfUsable(installIfUsable);
        return this;
    }

    public FileSystemItemBuilder setPriority(final int priority) {
        this.item.setPriority(BigInteger.valueOf(priority));
        return this;
    }

    @Override
    protected FileSystemItem getEntity() {
        return item;
    }
}
