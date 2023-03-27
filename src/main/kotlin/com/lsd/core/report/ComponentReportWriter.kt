package com.lsd.core.report

import com.lsd.core.properties.LsdProperties
import com.lsd.core.properties.LsdProperties.OUTPUT_DIR
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

object ComponentReportWriter {
    fun writeToFile(content: String, fileName: String): Path {
        val outputDir = File(LsdProperties[OUTPUT_DIR])
        outputDir.mkdir()
        return File(outputDir, fileName).toPath().also {
            Files.write(it, content.toByteArray())
            println("LSD Component Report: file://${it.toAbsolutePath()}")
        }
    }
}
