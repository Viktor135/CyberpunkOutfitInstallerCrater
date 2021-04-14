package de.vsc.coi.zip;

import static lombok.AccessLevel.NONE;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.vsc.coi.zip.ZipAdapter.ZipException;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = NONE)
public class Zipper {

    private static final Logger LOGGER = LoggerFactory.getLogger(Zipper.class);
    private static final List<ZipAdapter> zipAdapter = List.of(new SevenZipAdapter(), new JavaZipAdapter());

    private static String getOs() {
        return System.getProperty("os.name").toLowerCase();
    }

    public static void zip(final File input, final File outputDir, final String zipName) throws ZipException {
        final String os = getOs();
        final ZipAdapter zipAdapter = Zipper.zipAdapter.stream()
                .filter(x -> x.operatingSystemMatches(os))
                .filter(ZipAdapter::toolIsAvailable)
                .findFirst()
                .orElseThrow(() -> new ZipException("No matching zip adapter found."));

        LOGGER.info("Using: " + zipAdapter.toolName());
        zipAdapter.execute(input, outputDir, zipName);
    }

}
