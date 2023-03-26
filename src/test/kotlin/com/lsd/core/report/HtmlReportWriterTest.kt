package com.lsd.core.report

import com.lsd.core.properties.LsdProperties
import com.lsd.core.properties.LsdProperties.OUTPUT_DIR
import com.lsd.core.report.model.Report
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junitpioneer.jupiter.SetSystemProperty
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.io.File

@SetSystemProperty(key = "lsd.core.report.outputDir", value = "build/reports/lsd/deeply/nested/directory")
internal class HtmlReportWriterTest {

    private val mockRenderer = mock<ReportRenderer> {
        on { render(any()) } doReturn "report content"
    }
    
    private val outputDirectory = LsdProperties[OUTPUT_DIR]
    private val underTest = HtmlReportWriter(mockRenderer)
    private val aReport = Report("report title", emptyList(), "")

    @Test
    fun writesFileContainingRenderedContent() {
        underTest.writeToFile(aReport)
        assertThat(File(outputDirectory, "reportTitle.html"))
            .exists()
            .hasContent("report content")
    }

    @Test
    fun addCssFileToOutputDirectory() {
        underTest.writeToFile(aReport)
        assertThat(File(outputDirectory, "style.css")).exists()
    }

    @Test
    fun addJavaScriptFileToOutputDirectory() {
        underTest.writeToFile(aReport)
        assertThat(File(outputDirectory, "custom.js")).exists()
    }

    @Test
    fun writeToString() {
        val result = underTest.writeToString(aReport)
        assertThat(result).contains("report content")
    }
}