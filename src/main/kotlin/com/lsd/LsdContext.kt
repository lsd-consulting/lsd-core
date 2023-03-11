package com.lsd

import com.lsd.OutcomeStatus.SUCCESS
import com.lsd.builders.CapturedScenarioBuilder
import com.lsd.builders.ScenarioBuilder
import com.lsd.diagram.ComponentDiagramGenerator
import com.lsd.diagram.SequenceDiagramGenerator
import com.lsd.events.SequenceEvent
import com.lsd.parse.OutboundMessageParser
import com.lsd.parse.Parser
import com.lsd.parse.SynchronousResponseParser
import com.lsd.properties.LsdProperties.DETERMINISTIC_IDS
import com.lsd.properties.LsdProperties.MAX_EVENTS_PER_DIAGRAM
import com.lsd.properties.LsdProperties.getBoolean
import com.lsd.properties.LsdProperties.getInt
import com.lsd.report.HtmlIndexWriter.writeToFile
import com.lsd.report.HtmlReportRenderer
import com.lsd.report.HtmlReportWriter
import com.lsd.report.model.DataHolder
import com.lsd.report.model.Participant
import com.lsd.report.model.Report
import java.nio.file.Path

open class LsdContext {
    private val capturedScenarios: MutableList<CapturedScenario> = ArrayList()
    private val capturedReports: MutableList<CapturedReport> = ArrayList()
    private val participants: MutableList<Participant> = ArrayList()
    private val includes: MutableList<String> = ArrayList()
    val idGenerator = IdGenerator(getBoolean(DETERMINISTIC_IDS))
    private val parsers = parsers()
    private val htmlReportWriter = HtmlReportWriter(HtmlReportRenderer())
    private val maxEventsPerDiagram = getInt(MAX_EVENTS_PER_DIAGRAM)
    private var currentScenario = CapturedScenarioBuilder()


    fun addParticipants(additionalParticipants: List<Participant>) {
        participants.addAll(additionalParticipants)
    }

    fun includeFiles(additionalIncludes: Set<String>) {
        includes.addAll(additionalIncludes)
    }

    fun addFact(key: String, value: String = "") {
        currentScenario.addFact(key, value)
    }

    /**
     * Capture a sequence event for the current scenario 
     *
     * @param event The event to be captured on the sequence diagram for the current scenario.
     */
    open fun capture(event: SequenceEvent) {
        currentScenario.add(event)
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

    fun completeScenario(title: String, description: String = "", status: OutcomeStatus = SUCCESS) {
        currentScenario.title(title)
        currentScenario.description(description)
        currentScenario.outcomeStatus(status)
        capturedScenarios.add(currentScenario.build())
        currentScenario = CapturedScenarioBuilder()
    }

    fun completeReport(title: String): Path {
        val report = buildReport(title)
        return htmlReportWriter.writeToFile(report).also {
            capturedReports.add(CapturedReport(report.title, it.fileName.toString(), report.status))
            capturedScenarios.clear()
            currentScenario = CapturedScenarioBuilder()
        }
    }

    fun generateReport(title: String): String = htmlReportWriter.writeToString(buildReport(title))

    fun createIndex(): Path = writeToFile(capturedReports)

    /**
     * This clears down the context - all scenarios, reports, events and ids will be reset.
     */
    fun clear() {
        idGenerator.reset()
        capturedScenarios.clear()
        capturedReports.clear()
        currentScenario = CapturedScenarioBuilder()
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
            status = determineOverallStatus(capturedScenarios),
            scenarios = capturedScenarios
                .map {
                    ScenarioBuilder()
                        .id(idGenerator.next())
                        .title(it.title)
                        .status(it.outcomeStatus.cssClass)
                        .description(it.description)
                        .facts(it.facts)
                        .dataHolders(
                            it.sequenceEvents
                                .filter(DataHolder::class.java::isInstance)
                                .map(DataHolder::class.java::cast)
                        )
                        .sequenceDiagram(
                            SequenceDiagramGenerator.builder()
                                .idGenerator(idGenerator)
                                .events(it.sequenceEvents)
                                .participants(participants)
                                .includes(includes)
                                .build()
                                .diagram(maxEventsPerDiagram)
                        )
                        .componentDiagram(
                            ComponentDiagramGenerator(
                                idGenerator = idGenerator,
                                events = it.sequenceEvents,
                                participants = participants
                            ).diagram()
                        )
                        .build()
                })
    }

    private fun determineOverallStatus(capturedScenarios: List<CapturedScenario>): String {
        return capturedScenarios.stream()
            .map(CapturedScenario::outcomeStatus)
            .sorted()
            .findFirst()
            .map(OutcomeStatus::cssClass)
            .orElse("")
    }

    private fun parsers(): List<Parser> {
        return listOf(
            OutboundMessageParser(idGenerator),
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