package com.lsd.core.domain

data class Scenario
@JvmOverloads constructor(
    val title: String,
    val events: MutableList<SequenceEvent> = ArrayList(),
    val facts: MutableList<Fact> = ArrayList(),
    val description: String = "",
    val status: Status
)

enum class Status { ERROR, FAILURE, SUCCESS; }