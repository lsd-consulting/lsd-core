package com.lsd.core.report

import com.lsd.core.LsdContext
import com.lsd.core.builders.message
import com.lsd.core.domain.Participant
import com.lsd.core.domain.ParticipantType.*
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import org.junit.jupiter.api.AfterEach
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

    @AfterEach
    fun cleanup() {
        playwright.close()
    }

    @Test
    fun generateCombinedComponentDiagramSource() {
        givenMultipleScenariosAndReportsWithParticipants(arnie, bettie, cat, dan)

        whenTheCompleteComponentsReportIsGenerated()

        thenPageContainsParticipantNames(arnie, bettie, cat, dan, title = "Components")
    }

    private fun givenMultipleScenariosAndReportsWithParticipants(
        participantA: Participant,
        participantB: Participant,
        participantC: Participant,
        participantD: Participant
    ) {
        with(lsd) {
            capture(participantA to participantB message "message1")
            completeScenario("Scenario 1.1")
            capture(participantB to participantC message "message2")
            completeScenario("Scenario 1.2")
            completeReport("Report 1")

            capture(participantC to participantD message "message3")
            completeScenario("Scenario 2.1")
            completeReport("Report 2")

            capture(participantD to participantA message "message4")
            completeScenario("Scenario 3.1")
            completeReport("Report 3")
        }
    }

    private fun whenTheCompleteComponentsReportIsGenerated() {
        page.navigate("file://${lsd.completeComponentsReport("Relationships").absolute()}")
    }

    private fun thenPageContainsParticipantNames(vararg participants: Participant, title: String) {
        assertThat(page).hasTitle(title)
        assertThat(page.locator("h1.logo")).isVisible()
        assertThat(page.locator("section.diagram").first()).isVisible()
        page.capture(name = "combine_components_report")

        participants.forEach { participant ->
            val expectedName =
                if (participant.alias?.isNotBlank() == true) participant.alias else participant.componentName.normalisedName
            page.getByText(expectedName).first().let { assertThat(it).isVisible() }
        }
    }
}