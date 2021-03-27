package de.vsc.coi.zip;

import static de.vsc.coi.config.Config.config;

public class SevenZipAdapter extends ZipAdapter {

    @Override
    String toolName() {
        return "7zip";
    }

    @Override
    String fileExtension() {
        return ".7z";
    }

    @Override
    boolean operatingSystemMatches(final String osName) {
        return osName.startsWith("windows");
    }

    @Override
    String zipCommand(final String inputPath, final String outputPath) {
        final String zipExe = enclose(config().getSevenZipPath());
        final String out = enclose(outputPath);
        final String in = enclose(inputPath + "/*");
        return enclose(String.format("%s a %s %s", zipExe, out, in));
    }

    @Override
    String helpCommand() {
        return enclose(config().getSevenZipPath());
    }
}
