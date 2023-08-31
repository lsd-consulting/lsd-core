package com.lsd.core.domain

import com.lsd.core.capitalise

data class ComponentName(val raw: String) {
    val normalisedName = convertToValidName(raw)
}

private val illegalChars = "[()/]".toRegex()

private fun convertToValidName(raw: String): String =
    raw.replace(illegalChars, " ")
        .replace("-", "_")
        .split(" ")
        .dropLastWhile(String::isEmpty)
        .toTypedArray()
        .joinToString(transform = String::capitalise, separator = "")
        .replace(" ", "")