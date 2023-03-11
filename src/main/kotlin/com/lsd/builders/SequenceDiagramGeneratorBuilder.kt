package com.lsd.builders

import com.lsd.IdGenerator
import com.lsd.diagram.SequenceDiagramGenerator
import com.lsd.events.SequenceEvent
import com.lsd.report.model.Participant

class SequenceDiagramGeneratorBuilder {
    private var instance = SequenceDiagramGenerator(idGenerator = IdGenerator())

    fun idGenerator(idGenerator: IdGenerator): SequenceDiagramGeneratorBuilder =
        also { instance = instance.copy(idGenerator = idGenerator) }

    fun events(events: List<SequenceEvent>): SequenceDiagramGeneratorBuilder =
        also { instance = instance.copy(events = events) }

    fun participants(participants: List<Participant>): SequenceDiagramGeneratorBuilder =
        also { instance = instance.copy(participants = participants) }

    fun includes(includes: List<String>): SequenceDiagramGeneratorBuilder =
        also { instance = instance.copy(includes = includes) }

    fun build(): SequenceDiagramGenerator = instance
}