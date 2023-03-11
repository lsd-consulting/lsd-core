package com.lsd.diagram

import lsd.format.xml.XmlPrettyPrinter
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import org.apache.commons.lang3.StringUtils
import java.io.ByteArrayOutputStream

fun convertToSvg(markup: String): String {
    val result = StringBuilder()
    val umlCount = StringUtils.countMatches(markup, "@startuml")
    for (i in 0 until umlCount) {
        val svg = createSvg(markup, i)
        result.append(XmlPrettyPrinter.indentXml(svg).orElse(svg))
    }
    return result.toString()
}

private fun createSvg(plantUmlMarkup: String, pageNumber: Int): String {
    ByteArrayOutputStream().use { os ->
        val reader = SourceStringReader(plantUmlMarkup)
        reader.outputImage(os, pageNumber, FileFormatOption(FileFormat.SVG, false))
        return os.toString()
    }
}