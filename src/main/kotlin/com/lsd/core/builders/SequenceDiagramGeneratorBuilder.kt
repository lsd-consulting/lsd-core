package com.lsd.core.builders

import com.lsd.core.IdGenerator
import com.lsd.core.diagram.SequenceDiagramGenerator
import com.lsd.core.domain.Participant
import com.lsd.core.domain.SequenceEvent

class SequenceDiagramGeneratorBuilder {
    private var instance = SequenceDiagramGenerator(idGenerator = IdGenerator())

    fun idGenerator(idGenerator: IdGenerator) = also { instance = instance.copy(idGenerator = idGenerator) }
    fun events(events: List<SequenceEvent>) = also { instance = instance.copy(events = events) }
    fun participants(participants: List<Participant>) = also { instance = instance.copy(participants = participants) }
    fun includes(includes: List<String>) = also { instance = instance.copy(includes = includes) }

    fun build(): SequenceDiagramGenerator = instance

    companion object {
        fun sequenceDiagramGeneratorBuilder(): SequenceDiagramGeneratorBuilder = SequenceDiagramGeneratorBuilder()
    }
}