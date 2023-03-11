package com.lsd

import java.util.*

// Order matters for sorting by importance
enum class OutcomeStatus {
    ERROR, WARN, SUCCESS;

    val cssClass: String
        get() = name.lowercase(Locale.getDefault())
}