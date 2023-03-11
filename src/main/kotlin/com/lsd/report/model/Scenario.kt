package com.lsd.report.model

import org.apache.commons.collections4.MultiValuedMap
import org.apache.commons.collections4.multimap.HashSetValuedHashMap

/**
 * Beware that these names are use in the html template so be careful when renaming.
 */
data class Scenario(
    var id: String,
    var title: String,
    var description: String = "",
    var status: String = "",
    var sequenceDiagram: Diagram? = null,
    var componentDiagram: Diagram? = null,
    var dataHolders: List<DataHolder> = emptyList(),
    var facts: MultiValuedMap<String, String> = HashSetValuedHashMap(),
)