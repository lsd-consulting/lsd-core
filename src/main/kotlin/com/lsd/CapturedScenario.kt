package com.lsd

import com.lsd.events.SequenceEvent
import org.apache.commons.collections4.MultiValuedMap
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap

data class CapturedScenario(
    val title: String,
    val sequenceEvents: MutableList<SequenceEvent> = ArrayList(),
    val facts: MultiValuedMap<String, String> = ArrayListValuedHashMap(),
    val description: String = "",
    val outcomeStatus: OutcomeStatus
) {

    fun add(sequenceEvent: SequenceEvent?) {
        sequenceEvent?.let { sequenceEvents.add(it) }
    }

    fun clearEvents() {
        sequenceEvents.clear()
    }

    fun addFact(key: String, value: String) {
        facts.put(key, value)
    }
}