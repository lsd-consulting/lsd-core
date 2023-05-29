package com.lsd.core.builders

import com.lsd.core.domain.Fact
import com.lsd.core.report.model.DataHolder
import com.lsd.core.report.model.Diagram
import com.lsd.core.report.model.Metrics
import com.lsd.core.report.model.ScenarioModel

class ScenarioModelBuilder {
    private var scenarioModel = ScenarioModel(id = "", title = "")

    fun id(id: String) = also { scenarioModel = scenarioModel.copy(id = id) }
    fun title(title: String) = also { scenarioModel = scenarioModel.copy(title = title) }
    fun status(cssClass: String) = also { scenarioModel = scenarioModel.copy(status = cssClass) }
    fun description(description: String) = also { scenarioModel = scenarioModel.copy(description = description) }
    fun facts(facts: List<Fact>) = also { scenarioModel = scenarioModel.copy(facts = facts) }
    fun metrics(metrics: Metrics?) = also { scenarioModel = scenarioModel.copy(metrics = metrics) }
    fun dataHolders(dataHolders: List<DataHolder>) = also { scenarioModel = scenarioModel.copy(dataHolders = dataHolders) }
    fun sequenceDiagram(diagram: Diagram?) = also { scenarioModel = scenarioModel.copy(sequenceDiagram = diagram) }
    fun componentDiagram(diagram: Diagram?) = also { scenarioModel = scenarioModel.copy(componentDiagram = diagram) }

    fun build(): ScenarioModel = scenarioModel

    companion object {
        @JvmStatic
        fun scenarioModelBuilder(): ScenarioModelBuilder = ScenarioModelBuilder()
    }
}