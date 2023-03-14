package com.lsd.core.builders

import com.lsd.core.domain.Fact
import com.lsd.core.domain.Scenario
import com.lsd.core.domain.Status
import com.lsd.core.domain.SequenceEvent

class ScenarioBuilder {
    private var instance = Scenario(
        title = "",
        description = "",
        facts = ArrayList(),
        events = ArrayList(),
        status = Status.SUCCESS
    )

    fun title(title: String) = also { instance = instance.copy(title = title) }
    fun description(description: String) = also { instance = instance.copy(description = description) }
    fun status(status: Status) = also { instance = instance.copy(status = status) }

    fun clearEvents() = instance.events.clear()
    fun addFact(key: String, value: String) = instance.facts.add(Fact(key, value))
    fun add(event: SequenceEvent) = instance.events.add(event)

    fun build(): Scenario = instance.copy()
    
    companion object {
        @JvmStatic
        fun scenarioBuilder(): ScenarioBuilder = ScenarioBuilder()
    }
}