package com.lsd.builders

import com.lsd.report.model.DataHolder
import com.lsd.report.model.Diagram
import com.lsd.report.model.Scenario
import org.apache.commons.collections4.MultiValuedMap

class ScenarioBuilder {
    private var instance = Scenario(id = "", title = "")

    fun id(id: String): ScenarioBuilder = 
        also { instance = instance.copy(id = id) }
    
    fun title(title: String): ScenarioBuilder = 
        also { instance = instance.copy(title = title) }
    
    fun status(cssClass: String): ScenarioBuilder = 
        also { instance = instance.copy(status = cssClass) }
    
    fun description(description: String): ScenarioBuilder = 
        also { instance = instance.copy(description = description) }
    
    fun facts(facts: MultiValuedMap<String, String>): ScenarioBuilder = 
        also { instance = instance.copy(facts = facts) }
    
    fun dataHolders(dataHolders: List<DataHolder>): ScenarioBuilder = 
        also { instance = instance.copy(dataHolders = dataHolders) }
    
    fun sequenceDiagram(diagram: Diagram?): ScenarioBuilder = 
        also { instance = instance.copy(sequenceDiagram = diagram) }
    
    fun componentDiagram(diagram: Diagram?): ScenarioBuilder = 
        also { instance = instance.copy(componentDiagram = diagram) }
    
    fun build(): Scenario = instance
}