package com.lsd.report.model

class Report(
    var title: String,
    var scenarios: Collection<Scenario> = emptyList(),
    var status: String = "",
)