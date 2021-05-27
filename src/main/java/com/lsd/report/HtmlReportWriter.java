package com.lsd.report;

import com.lsd.properties.LsdProperties;
import com.lsd.report.HtmlReportRenderer;
import com.lsd.report.model.Report;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.lsd.properties.LsdProperties.OUTPUT_DIR;
import static org.apache.commons.text.WordUtils.capitalizeFully;

@Slf4j
public class HtmlReportWriter {

    @SneakyThrows //TODO handle gracefully instead of propagating?
    public static Path writeToFile(Report report) {
        File outputDir = outputDir();
        outputDir.mkdir();
        Path path = new File(outputDir, createFileName(report.getTitle())).toPath();
        Files.write(path, new HtmlReportRenderer().render(report).getBytes());
        System.out.println("LSD Report:\nfile://" + path);
        return path;
    }

    private static String createFileName(String title) {
        return capitalizeFully(title).
                replaceAll(" ", "")
                .concat(".html");
    }

    private static File outputDir() {
        return new File(LsdProperties.get(OUTPUT_DIR));
    }
}
