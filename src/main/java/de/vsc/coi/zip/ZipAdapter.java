package de.vsc.coi.zip;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ZipAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZipAdapter.class);

    public static String enclose(final String in) {
        return "\"" + in + "\"";
    }

    abstract String toolName();

    abstract String fileExtension();

    abstract boolean operatingSystemMatches(final String osName);

    abstract String zipCommand(final String inputPath, final String outputPath);

    abstract String helpCommand();

    public boolean toolIsAvailable() {
        try {
            return Runtime.getRuntime().exec("cmd.exe /c " + helpCommand()).waitFor() == 0;
        } catch (final InterruptedException | IOException e) {
            LOGGER.error("While checking if " + toolName() + " exists.", e);
        }
        return false;
    }

    File outFile(final File outputDir, final String zipName) {
        return new File(outputDir, zipName + fileExtension());
    }

    public void execute(final File input, final File outputDir, final String zipName) throws ZipException {
        final String outPath = outFile(outputDir, zipName).getPath();
        final String inPath = input.getPath();
        final String command = zipCommand(inPath, outPath);

        LOGGER.info("Executing: " + command);
        try {
            final Process process = Runtime.getRuntime().exec("cmd.exe /c " + command);

            final StreamGobbler error = StreamGobbler.newAndStart(process.getErrorStream());
            final StreamGobbler output = StreamGobbler.newAndStart(process.getInputStream());

            final int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new ZipException("While zipping: Error code: " + exitCode + " Message: " + error.get());
            } else {
                LOGGER.info("Zipped successfully: " + output.get());
            }
        } catch (final InterruptedException | IOException e) {
            throw new ZipException("While zipping with 7zip.", e);
        }
    }

    public static class StreamGobbler implements Runnable {

        private final InputStream inputStream;
        private final StringBuffer buffer;

        public StreamGobbler(final InputStream inputStream) {
            this.inputStream = inputStream;
            this.buffer = new StringBuffer();
        }

        public static StreamGobbler newAndStart(final InputStream inputStream) {
            final StreamGobbler streamGobbler = new StreamGobbler(inputStream);
            ForkJoinPool.commonPool().execute(streamGobbler);
            return streamGobbler;
        }

        public String get() {
            return buffer.toString();
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(buffer::append);
        }
    }

    public static class ZipException extends IOException {

        public ZipException(final String message) {
            super(message);
        }

        public ZipException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }

}
