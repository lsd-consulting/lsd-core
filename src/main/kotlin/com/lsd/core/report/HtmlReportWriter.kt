package com.lsd.core.report

import com.lsd.core.capitalizeFully
import com.lsd.core.report.model.Report
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class HtmlReportWriter(private val reportRenderer: ReportRenderer) {

    private val extension = ".html"

    fun writeToFile(report: Report, outputDir: File, devMode: Boolean): Path {
        outputDir.mkdirs()
        val reportFileName = generateFileName(report.title)
        val outputPath = File(outputDir, reportFileName).toPath()
        val reportContent = reportRenderer.render(report, devMode)
        writeFileSafely(outputPath, reportContent)
        return outputPath
    }

    fun renderReport(report: Report, devMode: Boolean): String = reportRenderer.render(report, devMode)

    private fun writeFileSafely(outputPath: Path, reportContent: String) {
        try {
            Files.write(outputPath, reportContent.toByteArray())
            println("LSD Report: file://${outputPath.toAbsolutePath()}")
        } catch (e: IOException) {
            System.err.println("Failed to write LSD report to output path: $outputPath , error message: ${e.message}")
        }
    }

    private fun generateFileName(title: String): String =
        title.capitalizeFully().replace(" ", "") + extension
}