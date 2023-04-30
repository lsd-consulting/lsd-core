package com.lsd.core.report

import com.lsd.core.LsdContext
import com.lsd.core.builders.MessageBuilder.Companion.messageBuilder
import com.lsd.core.domain.Participant
import com.lsd.core.domain.ParticipantType.*
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.io.path.absolute

class CombinedComponentReportUiTest {

    private val lsd = LsdContext()
    private val playwright = Playwright.create()
    private val chromium = playwright.chromium()
    private val browser = chromium.launch()
    private val page = browser.newPage()

    private val arnie = ACTOR.called("A", "Arnie")
    private val bettie = BOUNDARY.called("Bettie")
    private val cat = CONTROL.called("Cat")
    private val dan = CONTROL.called("Dan")

    @Test
    fun generateCombinedComponentDiagramSource() {
        givenMultipleScenariosAndReportsWithParticipants(arnie, bettie, cat, dan)

        whenTheCompleteComponentsReportIsGenerated()

        assertThat(page).hasTitle("Components")
        assertThat(page.locator("h1.logo")).isVisible()
        assertThat(page.locator("section.diagram")).isVisible()
        pageContainsParticipantNames(arnie, bettie, cat, dan)
    }

    private fun givenMultipleScenariosAndReportsWithParticipants(
        participantA: Participant,
        participantB: Participant,
        participantC: Participant,
        participantD: Participant
    ) {
        lsd.capture(messageBuilder().from(participantA).to(participantB).label("message1").build())
        lsd.completeScenario("Scenario 1.1")
        lsd.capture(messageBuilder().from(participantB).to(participantC).label("message2").build())
        lsd.completeScenario("Scenario 1.2")
        lsd.completeReport("Report 1")

        lsd.capture(messageBuilder().from(participantC).to(participantD).label("message3").build())
        lsd.completeScenario("Scenario 2.1")
        lsd.completeReport("Report 2")

        lsd.capture(messageBuilder().from(participantD).to(participantA).label("message4").build())
        lsd.completeScenario("Scenario 3.1")
        lsd.completeReport("Report 3")
    }

    private fun pageContainsParticipantNames(vararg participants: Participant) {
        participants.forEach { participant ->
            val expectedName =
                if (participant.alias?.isNotBlank() == true) participant.alias else participant.componentName.normalisedName
            page.getByText(expectedName).first().let { assertThat(it).isVisible() }
        }
    }

    private fun whenTheCompleteComponentsReportIsGenerated() {
        page.navigate("file://${lsd.completeComponentsReport("Relationships").absolute()}")
    }
}