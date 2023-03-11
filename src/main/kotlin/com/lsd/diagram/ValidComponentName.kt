package com.lsd.diagram

import org.apache.commons.text.WordUtils
import java.util.*
import java.util.stream.Collectors.joining

object ValidComponentName {

    private val illegalChars = "[()/]".toRegex()

    @JvmStatic
    fun of(name: String): String {
        return Arrays.stream(
            name
                .replace(illegalChars, " ")
                .split(" ")
                .dropLastWhile { it.isEmpty() }.toTypedArray()
        ).map { str: String? -> WordUtils.capitalize(str) }
            .collect(joining())
            .replace(" ", "")
    }
}