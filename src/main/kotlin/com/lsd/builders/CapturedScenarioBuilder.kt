package com.lsd.builders

import com.lsd.CapturedScenario
import com.lsd.OutcomeStatus
import com.lsd.events.SequenceEvent
import org.apache.commons.collections4.MultiValuedMap
import org.apache.commons.collections4.multimap.HashSetValuedHashMap

class CapturedScenarioBuilder {
    private var instance = CapturedScenario(
        title = "",
        description = "",
        facts = HashSetValuedHashMap(),
        sequenceEvents = ArrayList(),
        outcomeStatus = OutcomeStatus.SUCCESS
    )

    fun title(title: String): CapturedScenarioBuilder = also { instance = instance.copy(title = title) }

    fun description(description: String): CapturedScenarioBuilder =
        also { instance = instance.copy(description = description) }

    fun facts(facts: MultiValuedMap<String, String>): CapturedScenarioBuilder =
        also { instance = instance.copy(facts = facts) }

    fun sequenceEvents(sequenceEvents: MutableList<SequenceEvent>): CapturedScenarioBuilder =
        also { instance = instance.copy(sequenceEvents = sequenceEvents) }

    fun outcomeStatus(outcomeStatus: OutcomeStatus): CapturedScenarioBuilder =
        also { instance = instance.copy(outcomeStatus = outcomeStatus) }

    fun clearEvents() = instance.clearEvents()

    fun addFact(key: String, value: String) = instance.addFact(key, value)

    fun add(event: SequenceEvent) = instance.add(event)
    
    fun build(): CapturedScenario = instance.copy()
}