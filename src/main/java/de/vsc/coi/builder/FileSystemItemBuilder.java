package de.vsc.coi.builder;

import static de.vsc.coi.config.Workspace.relativize;
import static de.vsc.coi.crawlers.ArchiveDirectoryCrawler.gameArchiveFolder;

import java.io.File;
import java.math.BigInteger;

import fomod.FileSystemItem;

public class FileSystemItemBuilder {

    private final FileSystemItem item;

    private FileSystemItemBuilder() {
        item = new FileSystemItem();
    }

    public static FileSystemItemBuilder builder() {
        return new FileSystemItemBuilder();
    }

    public static FileSystemItem newFileToCopy(final File file) {
        return FileSystemItemBuilder.builder()
                .setPriority(0)
                .setSource(relativize(file))
                .setDestination(gameArchiveFolder(file))
                .build();
    }

    public FileSystemItemBuilder setSource(final String source) {
        this.item.setSource(source);
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

    public FileSystemItem build() {
        return item;
    }
}
