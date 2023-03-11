package com.lsd.report

import com.lsd.CapturedReport
import com.lsd.properties.LsdProperties
import com.lsd.properties.LsdProperties.OUTPUT_DIR
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

object HtmlIndexWriter {

    fun writeToFile(reports: List<CapturedReport>): Path {
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