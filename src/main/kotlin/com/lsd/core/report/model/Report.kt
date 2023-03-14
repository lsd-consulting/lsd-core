package com.lsd.core.report.model

class Report(
    var title: String,
    var scenarios: Collection<ScenarioModel> = emptyList(),
    var status: String = "",
)