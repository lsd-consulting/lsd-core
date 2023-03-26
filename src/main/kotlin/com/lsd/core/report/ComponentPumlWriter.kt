package com.lsd.core.report

import com.lsd.core.properties.LsdProperties
import com.lsd.core.properties.LsdProperties.OUTPUT_DIR
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

object ComponentPumlWriter {
    fun writeToFile(uml: String): Path {
        File(LsdProperties[OUTPUT_DIR]).mkdir()
        return File(File(LsdProperties[OUTPUT_DIR]), "combined-components.puml").toPath().also {
            Files.write(it, uml.toByteArray())
        }
    }
}
