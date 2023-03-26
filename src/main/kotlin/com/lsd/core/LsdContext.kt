package com.lsd.core

import com.lsd.core.adapter.parse.Parser
import com.lsd.core.adapter.parse.SynchronousMessageParser
import com.lsd.core.adapter.parse.SynchronousResponseParser
import com.lsd.core.builders.ScenarioBuilder
import com.lsd.core.builders.ScenarioModelBuilder.Companion.scenarioModelBuilder
import com.lsd.core.builders.SequenceDiagramGeneratorBuilder.Companion.sequenceDiagramGeneratorBuilder
import com.lsd.core.diagram.ComponentDiagramGenerator
import com.lsd.core.domain.*
import com.lsd.core.properties.LsdProperties.DETERMINISTIC_IDS
import com.lsd.core.properties.LsdProperties.MAX_EVENTS_PER_DIAGRAM
import com.lsd.core.properties.LsdProperties.getBoolean
import com.lsd.core.properties.LsdProperties.getInt
import com.lsd.core.report.ComponentPumlWriter
import com.lsd.core.report.HtmlIndexWriter.writeToFile
import com.lsd.core.report.HtmlReportRenderer
import com.lsd.core.report.HtmlReportWriter
import com.lsd.core.report.model.DataHolder
import com.lsd.core.report.model.Report
import com.lsd.core.report.model.ReportFile
import java.nio.file.Path

open class LsdContext {

    val idGenerator = IdGenerator(getBoolean(DETERMINISTIC_IDS))

    private val maxEventsPerDiagram = getInt(MAX_EVENTS_PER_DIAGRAM)
    private val parsers = parsers(idGenerator)
    private val htmlReportWriter = HtmlReportWriter(HtmlReportRenderer())
    private val scenarios: MutableList<Scenario> = ArrayList()
    private val reportFiles: MutableList<ReportFile> = ArrayList()
    private val participants: MutableList<Participant> = ArrayList()
    private val includes: MutableList<String> = ArrayList()
    private var currentScenario = ScenarioBuilder()
    private var combinedEvents = mutableSetOf<SequenceEvent>()

    fun addParticipants(additionalParticipants: List<Participant>) = participants.addAll(additionalParticipants)

    fun includeFiles(additionalIncludes: Set<String>) = includes.addAll(additionalIncludes)

    fun addFact(key: String, value: String = "") = currentScenario.addFact(key, value)

    /**
     * Capture a sequence event for the current scenario
     *
     * @param event The event to be captured on the sequence diagram for the current scenario.
     */
    open fun capture(event: SequenceEvent) {
        currentScenario.add(event)
        combinedEvents.add(event)
    }

    /**
     * Allow string representations of events to be interpreted. If none of the parsers match the pattern then nothing will be captured
     *
     * @param pattern The input string that represents some event to be captured on the sequence diagram
     * @param body    The extra data associated with the event.
     */
    open fun capture(pattern: String, body: String = "") {
        parsers.firstNotNullOfOrNull { it.parse(pattern, body) }
            .also {
                it?.let { capture(it) }
            }
    }

    fun completeScenario(title: String, description: String = "", status: Status = Status.SUCCESS) {
        currentScenario.title(title)
        currentScenario.description(description)
        currentScenario.status(status)
        scenarios.add(currentScenario.build())
        currentScenario = ScenarioBuilder()
    }

    fun completeReport(title: String): Path {
        storeCombinedComponentDiagramSource()
        val report = buildReport(title)
        return htmlReportWriter.writeToFile(report).also {
            reportFiles.add(ReportFile(filename = it.fileName.toString(), title = report.title, status = report.status))
            scenarios.clear()
            currentScenario = ScenarioBuilder()
        }
    }

    private fun storeCombinedComponentDiagramSource() {
        ComponentPumlWriter.writeToFile(
            ComponentDiagramGenerator(
                idGenerator = idGenerator,
                events = combinedEvents.toList(),
                participants = participants
            ).generateUml()
        )
    }

    fun generateReport(title: String): String = htmlReportWriter.writeToString(buildReport(title))

    fun createIndex(): Path = writeToFile(reportFiles)

    /**
     * This clears down the context - all scenarios, reports, events and ids will be reset.
     */
    fun clear() {
        idGenerator.reset()
        scenarios.clear()
        reportFiles.clear()
        participants.clear()
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
                                .participants(participants)
                                .includes(includes)
                                .build()
                                .diagram(maxEventsPerDiagram)
                        )
                        .componentDiagram(
                            ComponentDiagramGenerator(
                                idGenerator = idGenerator,
                                events = scenario.events,
                                participants = participants
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

    private fun parsers(idGenerator: IdGenerator): List<Parser> {
        return listOf(
            SynchronousMessageParser(idGenerator),
            SynchronousResponseParser(idGenerator)
        )
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