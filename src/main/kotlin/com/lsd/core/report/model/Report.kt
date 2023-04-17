package com.lsd.core.report.model

data class Report(
    var title: String,
    var scenarios: Collection<ScenarioModel> = emptyList(),
    var status: String = "",
    var showContentsMenu: Boolean = true,
    var useLocalStaticFiles: Boolean = true,
)