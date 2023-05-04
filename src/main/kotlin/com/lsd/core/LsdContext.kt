package com.lsd.core

import com.lsd.core.builders.ScenarioBuilder
import com.lsd.core.builders.ScenarioModelBuilder.Companion.scenarioModelBuilder
import com.lsd.core.builders.SequenceDiagramGeneratorBuilder.Companion.sequenceDiagramGeneratorBuilder
import com.lsd.core.diagram.ComponentDiagramGenerator
import com.lsd.core.domain.*
import com.lsd.core.properties.LsdProperties.DETERMINISTIC_IDS
import com.lsd.core.properties.LsdProperties.MAX_EVENTS_PER_DIAGRAM
import com.lsd.core.properties.LsdProperties.getBoolean
import com.lsd.core.properties.LsdProperties.getInt
import com.lsd.core.report.*
import com.lsd.core.report.HtmlIndexWriter.writeToFile
import com.lsd.core.report.model.DataHolder
import com.lsd.core.report.model.Report
import com.lsd.core.report.model.ReportFile
import java.nio.file.Path

open class LsdContext {

    val idGenerator = IdGenerator(getBoolean(DETERMINISTIC_IDS))

    private val maxEventsPerDiagram = getInt(MAX_EVENTS_PER_DIAGRAM)
    private val htmlReportWriter = HtmlReportWriter(HtmlReportRenderer())
    private val scenarios: MutableList<Scenario> = ArrayList()
    private val reportFiles: MutableList<ReportFile> = ArrayList()
    private val participants = linkedMapOf<String, Participant>()
    private val includes = linkedSetOf<String>()
    private var currentScenario = ScenarioBuilder()
    private var combinedEvents = linkedSetOf<SequenceEvent>()

    fun addParticipants(vararg participants: Participant) = addParticipants(participants.toList())

    fun addParticipants(additionalParticipants: List<Participant>) =
        additionalParticipants.forEach { participants[it.componentName.normalisedName] = it }

    fun includeFiles(additionalIncludes: Set<String>) = includes.addAll(additionalIncludes)

    fun addFact(key: String, value: String = "") = currentScenario.addFact(key, value)

    /**
     * Capture a sequence event for the current scenario
     *
     * @param events The events to be captured on the sequence diagram for the current scenario.
     */
    open fun capture(vararg events: SequenceEvent) {
        events.forEach { event ->
            currentScenario.add(event)
            combinedEvents.add(event)
        }
    }

    @JvmOverloads
    fun completeScenario(title: String, description: String? = "", status: Status = Status.SUCCESS) {
        currentScenario.title(title)
        currentScenario.description(description)
        currentScenario.status(status)
        scenarios.add(currentScenario.build())
        currentScenario = ScenarioBuilder()
    }

    fun completeReport(title: String): Path {
        val report = buildReport(title)
        return htmlReportWriter.writeToFile(report).also {
            reportFiles.add(ReportFile(filename = it.fileName.toString(), title = report.title, status = report.status))
            scenarios.clear()
            currentScenario = ScenarioBuilder()
        }
    }

    /**
     * Generates a html report with a component diagram of all the events that have been seen up until this point.
     * Note that the events will also be cleared after invoking this function.
     */
    fun completeComponentsReport(title: String): Path {
        return ComponentReportWriter.writeToFile(
            content = renderComponentReport(title),
            fileName = "components-report.html"
        ).also {
            combinedEvents.clear()
        }
    }

    private fun renderComponentReport(title: String): String {
        return ComponentDiagramGenerator(
            idGenerator = idGenerator,
            events = combinedEvents.toList(),
            participants = participants.values.toList()
        ).diagram()?.let {
            ComponentReportRenderer().render(Model(title = title, uml = it.uml, svg = it.svg))
        } ?: ""
    }

    @Deprecated(message = "To be removed. User renderReport(title) instead.")
    fun generateReport(title: String): String = htmlReportWriter.renderReport(buildReport(title))

    fun renderReport(title: String): String =
        htmlReportWriter.renderReport(buildReport(title))

    fun createIndex(): Path = writeToFile(reportFiles)

    /**
     * This clears down the context - all scenarios, reports, events and ids will be reset.
     */
    fun clear() {
        idGenerator.reset()
        scenarios.clear()
        reportFiles.clear()
        includes.clear()
        currentScenario = ScenarioBuilder()
    }

    /**
     * Clears the captured sequence events for the current scenario only.
     *
     * This may be useful for removing events captured during test-setup for example.
     */
    fun clearScenarioEvents() {
        currentScenario.clearEvents()
    }

    private fun buildReport(title: String): Report {
        return Report(
            title = title,
            status = determineOverallStatus(scenarios),
            showContentsMenu = scenarios.size > 1,
            scenarios = scenarios
                .map { scenario ->
                    scenarioModelBuilder()
                        .id(idGenerator.next())
                        .title(scenario.title)
                        .status(scenario.status.toCssClass())
                        .description(scenario.description)
                        .facts(scenario.facts)
                        .dataHolders(
                            scenario.events
                                .filterIsInstance<Message>()
                                .map {
                                    DataHolder(
                                        id = it.id,
                                        abbreviatedLabel = it.label.abbreviate(),
                                        data = it.data
                                    )
                                }
                        )
                        .sequenceDiagram(
                            sequenceDiagramGeneratorBuilder()
                                .idGenerator(idGenerator)
                                .events(scenario.events)
                                .participants(participants.values.toList())
                                .includes(includes.toList())
                                .build()
                                .diagram(maxEventsPerDiagram)
                        )
                        .componentDiagram(
                            ComponentDiagramGenerator(
                                idGenerator = idGenerator,
                                events = scenario.events,
                                participants = participants.values.toList()
                            ).diagram()
                        )
                        .build()
                })
    }

    private fun determineOverallStatus(scenarios: List<Scenario>): String {
        return scenarios.map { it.status }.sortedWith(compareBy {
            when (it) {
                Status.ERROR -> 0
                Status.FAILURE -> 1
                Status.SUCCESS -> 2
            }
        }).firstOrNull()?.toCssClass() ?: ""
    }

    companion object {
        /**
         * Convenience method to create a singleton instance.
         * If you prefer to use other means to share a single instance or
         * want to create multiple instances then use the default constructor instead.
         *
         * @return A singleton instance of LsdContext
         */
        @JvmStatic
        val instance = LsdContext()
    }
}

fun Status.toCssClass(): String = when (this) {
    Status.SUCCESS -> "success"
    Status.ERROR -> "error"
    Status.FAILURE -> "warn"
}