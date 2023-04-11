package com.lsd.core.report

import com.lsd.core.report.model.Report
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

internal class HtmlReportWriterTest {

    private val outputDirectory = File("build/reports/lsd/deeply/nested/directory")
    private val customJsFile = File(outputDirectory, "custom.js")
    private val styleCssFile = File(outputDirectory, "style.css")

    private val underTest = HtmlReportWriter(HtmlReportRenderer())
    private val aReport = Report("report title")

    @BeforeEach
    fun clearExistingFiles() {
        outputDirectory.deleteRecursively()
    }

    @Test
    fun writesFilesToOutputDirectory() {
        val reportFile = underTest.writeToFile(report = aReport, outputDir = outputDirectory).toFile()

        assertThat(reportFile).exists()
        assertThat(customJsFile).exists()
        assertThat(styleCssFile).exists()
        assertThat(reportFile.readText())
            .contains("""<link rel="stylesheet" href="style.css">""")
            .contains("""<script src="custom.js"></script>""")
    }

    @Test
    fun staticFilesNotAddedWhenLocalForStaticContentIsFalse() {
        val reportFile = underTest.writeToFile(
            report = aReport.copy(useLocalStaticFiles = false),
            outputDir = outputDirectory
        ).toFile()

        assertThat(reportFile).exists()
        assertThat(styleCssFile).doesNotExist()
        assertThat(customJsFile).doesNotExist()
        assertThat(reportFile.readText())
            .contains("""<link rel="stylesheet" href="https://lsd-consulting.github.io/lsd-core/src/main/resources/static/style.css">""")
            .contains("""<script src="https://lsd-consulting.github.io/lsd-core/src/main/resources/static/custom.js"></script>""")
    }
}