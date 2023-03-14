package com.lsd.core.report.model

import com.lsd.core.domain.Fact

/**
 * Beware that these names are use in the html template so be careful when renaming.
 */
data class ScenarioModel(
    var id: String,
    var title: String,
    var description: String = "",
    var status: String = "",
    var sequenceDiagram: Diagram? = null,
    var componentDiagram: Diagram? = null,
    var dataHolders: List<DataHolder> = emptyList(),
    var facts: List<Fact> = ArrayList(),
)