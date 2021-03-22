package de.vsc.coi;

import static de.vsc.coi.config.Config.config;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.vsc.coi.config.Config;
import de.vsc.coi.config.Workspace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class Reporting {

    private String report;

    public Reporting generateReport(final File moduleConfig) throws IOException {
        this.report = Report.builder()
                .workspace(crawl(Workspace.dir()))
                .log(readLog())
                .config(config())
                .moduleConfiguration(readModuleConfig(moduleConfig))
                .build()
                .toJson();
        return this;
    }

    public void writeToFile() throws IOException {
        final String name =
                "report-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")) + ".json";
        final File reportFile = new File("./reports", name);
        reportFile.getParentFile().mkdirs();
        reportFile.createNewFile();
        FileUtils.writeStringToFile(reportFile, this.report, UTF_8);
    }

    public DummyFile crawl(final File dir) {
        final DummyFile dummyFile = new DummyFile(dir.getName());
        Stream.ofNullable(dir.listFiles()).flatMap(Arrays::stream).map(this::crawl).forEach(dummyFile::addChild);
        return dummyFile;
    }

    public String readLog() throws IOException {
        return FileUtils.readFileToString(new File("./logs/CPOIC.log"), UTF_8);
    }

    public String readModuleConfig(final File config) throws IOException {
        if (config == null) {
            return "";
        }
        return FileUtils.readFileToString(config, UTF_8);
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class DummyFile {

        public final List<DummyFile> children = new ArrayList<>();
        private final String name;

        public void addChild(final DummyFile file) {
            children.add(file);
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Report {

        private final DummyFile workspace;
        private final String log;
        private final Config config;
        private final String moduleConfiguration;

        public String toJson() throws JsonProcessingException {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        }

    }

}
