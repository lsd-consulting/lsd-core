package com.lsd.core

import com.lsd.core.properties.LsdProperties
import com.lsd.core.properties.LsdProperties.LABEL_MAX_WIDTH


/**
 * @see String.sanitiseMarkup()
 *
 * @return The sanitised version of the String
 */
fun String?.sanitise() = this?.sanitiseMarkup() ?: ""

/**
 * Remove markup that doesn't render nicely outside of context (e.g. plantUml placeholders
 * <pre>{@code <$..> }</pre> from being displayed on the html).
 *
 * @return The sanitised version of the String
 */
fun String.sanitiseMarkup(): String = replace("<\\$.*?>".toRegex(), "").trim()

val maxLabelWidth = LsdProperties.getInt(LABEL_MAX_WIDTH)
const val ellipsis = "..."

/**
 * @return An abbreviation of the String if the length exceeds the configured maximum width (with an ellipsis added to the end)
 */
fun String.abbreviate(): String {
    val trimmed = trim()
    return when {
        trimmed.isBlank() ->
            trimmed

        trimmed.length <= maxLabelWidth ->
            trimmed

        ellipsis.length >= maxLabelWidth ->
            trimmed.subSequence(startIndex = 0, endIndex = maxLabelWidth).toString()

        else ->
            trimmed.subSequence(startIndex = 0, endIndex = maxLabelWidth - ellipsis.length).toString() + ellipsis
    }
}

fun String.capitalise(): String = split(" ").joinToString(separator = "") { it.replaceFirstChar(Char::uppercase) }

fun String.capitalizeFully(): String = lowercase().capitalise()

fun String.countMatches(token: String) = token.toRegex().findAll(this).count()