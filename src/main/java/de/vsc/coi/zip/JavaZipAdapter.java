package de.vsc.coi.zip;

import static java.util.zip.Deflater.BEST_COMPRESSION;

import java.io.File;

import org.zeroturnaround.zip.ZipUtil;

public class JavaZipAdapter extends ZipAdapter {

    @Override
    String toolName() {
        return "Java zip";
    }

    @Override
    String fileExtension() {
        return ".zip";
    }

    @Override
    boolean operatingSystemMatches(final String osName) {
        return true;
    }

    @Override
    public boolean toolIsAvailable() {
        return true;
    }

    @Override
    public void execute(final File input, final File outputDir, final String zipName) throws ZipException {
        ZipUtil.pack(input, outFile(outputDir, zipName), BEST_COMPRESSION);
    }

    @Override
    String helpCommand() {
        throw new UnsupportedOperationException();
    }

    @Override
    String zipCommand(final String inputPath, final String outputPath) {
        throw new UnsupportedOperationException();
    }
}
