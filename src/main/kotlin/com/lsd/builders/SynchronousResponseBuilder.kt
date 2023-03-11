package com.lsd.builders

import com.lsd.events.ArrowType
import com.lsd.events.SynchronousResponse

class SynchronousResponseBuilder {
    private var instance = SynchronousResponse(id = "", from = "", to = "", label = "")

    fun id(id: String): SynchronousResponseBuilder =
        also { instance = instance.copy(id = id) }

    fun from(from: String): SynchronousResponseBuilder =
        also { instance = instance.copy(from = from) }

    fun to(to: String): SynchronousResponseBuilder =
        also { instance = instance.copy(to = to) }

    fun label(label: String): SynchronousResponseBuilder =
        also { instance = instance.copy(label = label) }

    fun data(data: Any): SynchronousResponseBuilder =
        also { instance = instance.copy(data = data) }

    fun colour(colour: String): SynchronousResponseBuilder =
        also { instance = instance.copy(colour = colour) }

    fun arrowType(arrowType: ArrowType): SynchronousResponseBuilder =
        also { instance = instance.copy(arrowType = arrowType) }

    fun build(): SynchronousResponse = instance
}