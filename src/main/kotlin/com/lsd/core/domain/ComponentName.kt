package com.lsd.core.domain

import com.lsd.core.capitalise

data class ComponentName(val raw: String) {
    val name = convertToValidName(raw)
}


private val illegalChars = "[()/]".toRegex()

private fun convertToValidName(raw: String): String =
    raw.replace(illegalChars, " ")
        .split(" ")
        .dropLastWhile(String::isEmpty)
        .toTypedArray()
        .joinToString(transform = String::capitalise, separator = "")
        .replace(" ", "")