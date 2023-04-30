package com.lsd.core.report

import com.lsd.core.LsdContext
import com.lsd.core.builders.MessageBuilder.Companion.messageBuilder
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import org.junit.jupiter.api.Test

class ReportUiTest {

    private val lsd = LsdContext()
    private val playwright = Playwright.create()
    private val chromium = playwright.chromium()
    private val browser = chromium.launch()
    private val page = browser.newPage()

    @Test
    fun hasNoDiagramSectionWhenThereAreNoEvents() {
        lsd.completeScenario("A scenario without events")

        page.setContent(lsd.renderReport("No Diagrams Report"))

        assertThat(page).hasTitle("No Diagrams Report")
        assertThat(page.locator("section.diagram")).not().isVisible()
    }

    @Test
    fun hasNoDiagramSectionWhenEventsAreCleared() {
        lsd.capture(messageBuilder().to("B").label("in").build())
        lsd.clearScenarioEvents()
        lsd.completeScenario("A scenario without events")

        page.setContent(lsd.renderReport("No Diagrams Report"))

        assertThat(page).hasTitle("No Diagrams Report")
        assertThat(page.locator("section.diagram")).not().isVisible()
    }
}