package com.lsd


fun sanitise(input: String?): String = input?.trim()?.replace("<\\$.*?>".toRegex(), "") ?: ""

/**
 * Remove markup that doesn't render nicely outside of context (e.g. plantUml placeholders such as
 * <pre>
 * {@code
 * <$..>
 * }
 * </pre>
 * being displayed on the html.
 *
 * @return The sanitised version of the input text, if the input value is null an empty string is returned.
 */
fun String.sanitiseMarkup(): String = trim().replace("<\\$.*?>".toRegex(), "")
