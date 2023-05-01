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
    
    private lateinit var messagePopupData: String
    private lateinit var messageText: String

    @Test
    fun hasNoDiagramSectionWhenThereAreNoEvents() {
        givenAScenarioWithoutAnyCapturedEvents()

        whenReportIsRendered()

        thenNoDiagramSectionIsVisible()
    }

    @Test
    fun hasNoDiagramSectionWhenEventsAreCleared() {
        givenAScenarioHasCapturedEventsCleared()

        whenReportIsRendered()

        thenNoDiagramSectionIsVisible()
    }

    @Test
    fun showPopupWhenClickingOnMessageLabel() {
        givenAScenarioContainingMessage(withText = "Message 1", withPopupData = "some data shown in popup")

        whenReportIsRendered()
        andTheMessageLabelIsClicked()

        thenThePopupDataIsShown()
    }

    private fun givenAScenarioHasCapturedEventsCleared() {
        lsd.capture(messageBuilder().to("B").label("in").build())
        lsd.clearScenarioEvents()
        lsd.completeScenario("A scenario without events")
    }

    private fun givenAScenarioWithoutAnyCapturedEvents() {
        lsd.completeScenario("A scenario without events")
    }

    private fun givenAScenarioContainingMessage(withText: String, withPopupData: String) {
        messageText = withText
        messagePopupData = withPopupData
        lsd.capture(messageBuilder().from("A").to("B").label(withText).data(withPopupData).build())
        lsd.completeScenario("Scenario")
    }

    private fun whenReportIsRendered() {
        page.setContent(lsd.renderReport("Report"))
    }

    private fun andTheMessageLabelIsClicked() {
        assertThat(page.getByText(messageText).first()).isVisible()
        assertThat(page.getByText(messagePopupData)).not().isVisible()
    }

    private fun thenNoDiagramSectionIsVisible() {
        assertThat(page.locator("section.diagram")).not().isVisible()
    }

    private fun thenThePopupDataIsShown() {
        page.getByText(messageText).first().click()
        assertThat(page.getByText(messagePopupData)).isVisible()
    }
}