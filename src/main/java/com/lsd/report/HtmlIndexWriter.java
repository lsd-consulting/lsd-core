package com.lsd.report;

import com.lsd.CapturedReport;
import com.lsd.properties.LsdProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.lsd.properties.LsdProperties.OUTPUT_DIR;

@Slf4j
public class HtmlIndexWriter {

    @SneakyThrows //TODO handle gracefully instead of propagating?
    public static Path writeToFile(List<CapturedReport> reports) {
        var outputDir = new File(LsdProperties.get(OUTPUT_DIR));
        outputDir.mkdir();
        Path path = new File(outputDir, "lsd-index.html").toPath();
        Files.write(path, new HtmlIndexRenderer().render(reports).getBytes());
        System.out.println("LSD Index:\nfile://" + path.toAbsolutePath());
        return path;
    }
}
