package com.lsd.core.report

import com.lsd.core.properties.LsdProperties
import com.lsd.core.properties.LsdProperties.OUTPUT_DIR
import com.lsd.core.report.model.ReportFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

object HtmlIndexWriter {

    fun writeToFile(reports: List<ReportFile>): Path {
        File(LsdProperties[OUTPUT_DIR]).mkdir()
        return File(File(LsdProperties[OUTPUT_DIR]), "lsd-index.html").toPath().also {
            Files.write(it, HtmlIndexRenderer().render(reports).toByteArray())
            printLocationOfIndex(it)
        }
    }

    private fun printLocationOfIndex(path: Path) {
        println(
            """
        LSD Index:
        file://${path.toAbsolutePath()}
        """.trimIndent()
        )
    }
}