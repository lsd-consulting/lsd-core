package com.lsd.report;

import com.lsd.properties.LsdProperties;
import com.lsd.report.model.Report;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static com.lsd.properties.LsdProperties.OUTPUT_DIR;
import static org.apache.commons.text.WordUtils.capitalizeFully;

@Slf4j
@RequiredArgsConstructor
public class HtmlReportWriter {
    private final File outputDir = prepareOutputDirectory(LsdProperties.get(OUTPUT_DIR));
    private final String extension = ".html";

    private final ReportRenderer reportRenderer;

    public Path writeToFile(Report report) {
        var reportFileName = generateFileName(report.getTitle());
        var outputPath = new File(outputDir, reportFileName).toPath();
        var reportContent = reportRenderer.render(report);

        writeFileSafely(outputPath, reportContent);
        writeFileIfMissing("static/style.css", new File(outputDir, "style.css"));
        return outputPath;
    }

    private void writeFileSafely(Path outputPath, String reportContent) {
        try {
            Files.write(outputPath, reportContent.getBytes());
            System.out.println("LSD Report:\nfile://" + outputPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to write LSD report to output path: " + outputPath + ", error message: " + e.getMessage());
        }
    }

    private File prepareOutputDirectory(String dir) {
        var outputDir = new File(dir);
        outputDir.mkdir();
        return outputDir;
    }

    private void writeFileIfMissing(String resourceName, File targetFile) {
        if (!targetFile.isFile()) {
            var classLoader = HtmlReportWriter.class.getClassLoader();
            var resourceUrl = Optional.ofNullable(classLoader.getResource(resourceName));
            resourceUrl.ifPresent(url -> copySafely(url, targetFile));
        }
    }

    private void copySafely(URL contentUrl, File targetFile) {
        try (var inputStream = contentUrl.openStream()) {
            Files.copy(inputStream, targetFile.toPath());
        } catch (IOException e) {
            System.err.println("Failed to copy file from source to target: " + contentUrl + ", target: " + targetFile);
        }
    }

    private String generateFileName(String title) {
        return capitalizeFully(title).
                replaceAll(" ", "")
                .concat(extension);
    }
}
