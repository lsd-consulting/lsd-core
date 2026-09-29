package com.lsd.core.report

import com.lsd.core.LsdContext
import com.lsd.core.ReportOptions
import com.lsd.core.builders.*
import com.lsd.core.domain.Status
import com.lsd.core.domain.LifelineAction.ACTIVATE
import com.lsd.core.domain.LifelineAction.DEACTIVATE
import com.lsd.core.domain.MessageType.SYNCHRONOUS_RESPONSE
import com.microsoft.playwright.Page.GetByRoleOptions
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.assertions.LocatorAssertions.IsVisibleOptions
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import com.microsoft.playwright.options.AriaRole.HEADING
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.io.path.absolute
import kotlin.time.Duration.Companion.seconds


class ReportUiTest {

    private val lsd = LsdContext()
    private val playwright = Playwright.create()
    private val chromium = playwright.chromium()
    private val browser = chromium.launch()
    private val page = browser.newPage()
    private var options = ReportOptions()

    private lateinit var messagePopupData: String
    private lateinit var messageText: String

    @AfterEach
    fun cleanup() {
        playwright.close()
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun hasNoDiagramSectionWhenThereAreNoEvents(isDevMode: Boolean) {
        givenAScenarioWithoutAnyCapturedEvents()

        whenReportIsRendered(isDevMode)

        thenNoDiagramSectionIsVisible()
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun hasNoDiagramSectionWhenEventsAreCleared(isDevMode: Boolean) {
        givenAScenarioHasCapturedEventsCleared()

        whenReportIsRendered(isDevMode)

        thenNoDiagramSectionIsVisible()
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun showPopupWhenClickingOnMessageLabel(isDevMode: Boolean) {
        givenAScenarioContainingMessage(withText = "Message 1", withPopupData = "some data shown in popup")

        whenReportIsRendered(isDevMode)
        andTheMessageLabelIsClicked()

        thenThePopupDataIsShown()
    }

    @Test
    fun generateExampleLsdReportWithMetrics() {
        givenMultipleCapturedMessages()
        givenMetricsAreEnabled()

        whenTheLSdReportIsGenerated(title = "LSD Report with metrics", screenshotName = "lsd_metrics_example_report")

        thenPageContains(title = "LSD Report with metrics", metricsVisible = true)
    }


    @Test
    fun hasViewportMetaAndStickyToolbarWhenMultipleScenarios() {
        givenMultipleScenariosWithDifferentStatuses()

        whenReportIsRendered(isDevMode = true)

        thenViewportMetaIsPresent()
        thenToolbarCanFilterByStatusAndSearch()
    }

    private fun givenMetricsAreEnabled() {
        options = options.copy(metricsEnabled = true)
    }

    private fun givenAScenarioHasCapturedEventsCleared() {
        lsd.capture("" messages "B" withLabel "in")
        lsd.clearScenarioEvents()
        lsd.completeScenario("A scenario without events")
    }

    private fun givenAScenarioWithoutAnyCapturedEvents() {
        lsd.completeScenario("A scenario without events")
    }

    private fun givenAScenarioContainingMessage(withText: String, withPopupData: String) {
        messageText = withText
        messagePopupData = withPopupData
        lsd.capture("A" messages "B" withLabel withText withData withPopupData)
        lsd.completeScenario("Scenario")
    }

    private fun givenMultipleCapturedMessages() {
        lsd.capture(
            "A" messages "B" withLabel "message 1",
            ACTIVATE lifeline "B" withColour "blue",
            "B" messages "C" withLabel "message 2",
            "C" messages "B" withLabel "OK" withColour "green" withType SYNCHRONOUS_RESPONSE withDuration 2.seconds,
            "B" messages "A" withLabel "OK" withColour "green" withType SYNCHRONOUS_RESPONSE withDuration 5.seconds,
            DEACTIVATE lifeline "B",
        )
        lsd.completeScenario("Example scenario")
    }

    private fun whenReportIsRendered(isDevMode: Boolean) {
        page.setContent(lsd.renderReport("Report", reportOptions = options.copy(devMode = isDevMode)))
    }

    private fun andTheMessageLabelIsClicked() {
        assertThat(page.getByTitle(messageText)).isVisible()
        assertThat(page.getByText(messagePopupData)).not().isVisible()
    }

    private fun whenTheLSdReportIsGenerated(title: String, screenshotName: String? = null) {
        page.navigate("file://${lsd.completeReport(title = title, options = options).absolute()}")
        if (!screenshotName.isNullOrBlank()) page.capture(name = screenshotName)
    }


    private fun givenMultipleScenariosWithDifferentStatuses() {
        lsd.capture("A" messages "B" withLabel "ok")
        lsd.completeScenario("Alpha success", status = Status.SUCCESS)
        lsd.capture("A" messages "B" withLabel "warn")
        lsd.completeScenario("Beta warning", status = Status.FAILURE)
        lsd.capture("A" messages "B" withLabel "err")
        lsd.completeScenario("Gamma error", status = Status.ERROR)
    }

    private fun thenViewportMetaIsPresent() {
        assertThat(page.locator("meta[name=viewport]")).hasAttribute("content", "width=device-width, initial-scale=1")
        assertThat(page.locator(".report-toolbar")).isVisible()
        assertThat(page.locator(".scenario-search")).isVisible()
    }

    private fun thenToolbarCanFilterByStatusAndSearch() {
        assertThat(page.locator("details.scenario")).hasCount(3)
        page.locator(".filter-chip[data-status=success]").click()
        assertThat(page.locator("details.scenario:not([hidden])")).hasCount(2)
        page.locator(".filter-chip[data-status=success]").click()
        page.locator(".scenario-search").fill("Gamma")
        assertThat(page.locator("details.scenario:not([hidden])")).hasCount(1)
        assertThat(page.locator("details.scenario:not([hidden]) summary h2")).hasText("Gamma error")
    }

    private fun thenNoDiagramSectionIsVisible() {
        assertThat(page.locator("section.diagram")).not().isVisible()
    }

    private fun thenThePopupDataIsShown() {
        page.getByText(messageText).first().click()
        assertThat(page.getByText(messagePopupData)).isVisible()
        page.capture(name = "popup_example", fullPage = false)
    }

    private fun thenPageContains(title: String, metricsVisible: Boolean) {
        assertThat(page).hasTitle(title)
        assertThat(page.getByRole(HEADING, GetByRoleOptions().setName("Metrics").setExact(true)))
            .isVisible(IsVisibleOptions().setVisible(metricsVisible))
        assertThat(page.locator("a.logo-link")).isVisible()
        assertThat(page.locator("section.diagram")).isVisible()
    }
}
