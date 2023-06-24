package com.lsd.core.report

import com.lsd.core.report.model.Report
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

internal class HtmlReportWriterTest {

    private val outputDirectory = File("build/reports/lsd/deeply/nested/directory")
    private val underTest = HtmlReportWriter(HtmlReportRenderer())
    private val aReport = Report("report title")

    @BeforeEach
    fun clearExistingFiles() {
        outputDirectory.deleteRecursively()
    }

    @Test
    fun writesFilesToOutputDirectory() {
        val reportFile = underTest.writeToFile(report = aReport, outputDir = outputDirectory, devMode = true).toFile()

        assertThat(reportFile).exists()
    }
}