package com.lsd.core

import com.lsd.core.builders.MessageBuilder.Companion.messageBuilder
import com.lsd.core.domain.MessageType.SHORT_INBOUND
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
        assertThat(page.locator("h1.logo")).isVisible()
        assertThat(page.locator("section.diagram")).not().isVisible()
    }

    @Test
    fun hasNoDiagramSectionWhenEventsAreCleared() {
        lsd.capture(
            messageBuilder().to("B").label("in").data("start another job").type(SHORT_INBOUND).build()
        )
        lsd.clearScenarioEvents()
        lsd.completeScenario("A scenario without events (cleared down)")

        page.setContent(lsd.renderReport("No Diagrams Report (cleared down)"))
        
        assertThat(page).hasTitle("No Diagrams Report (cleared down)")
        assertThat(page.locator("h1.logo")).isVisible()
        assertThat(page.locator("section.diagram")).not().isVisible()
    }
}