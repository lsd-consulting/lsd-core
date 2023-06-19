package com.lsd.core.adapter.puml

import com.lsd.core.countMatches
import kotlinx.coroutines.*
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import java.io.ByteArrayOutputStream

fun convertToSvgAsync(markup: String): String = runBlocking {
    val deferredResults = mutableListOf<Deferred<String>>()
    val umlCount = markup.countMatches("@startuml")
    val sourceStringReader = SourceStringReader(markup)
    (0 until umlCount).forEach { i ->
        deferredResults += async(Dispatchers.Default) { createSvg(i, sourceStringReader) }
    }
    
    StringBuilder().apply { deferredResults.awaitAll().forEach(::append) }
        .toString()
}

private fun createSvg(pageNumber: Int, sourceStringReader: SourceStringReader): String {
    ByteArrayOutputStream().use { os ->
        sourceStringReader.outputImage(os, pageNumber, FileFormatOption(FileFormat.SVG, false))
        return os.toString()
    }
}
