package com.lsd.core.report

import com.lsd.core.LsdContext
import com.lsd.core.ReportOptions
import com.lsd.core.builders.messages
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

    private fun givenMetricsAreEnabled() {
        options = options.copy(metricsEnabled = true)
    }

    private fun givenAScenarioHasCapturedEventsCleared() {
        lsd.capture(("" messages "B") { label("in") })
        lsd.clearScenarioEvents()
        lsd.completeScenario("A scenario without events")
    }

    private fun givenAScenarioWithoutAnyCapturedEvents() {
        lsd.completeScenario("A scenario without events")
    }

    private fun givenAScenarioContainingMessage(withText: String, withPopupData: String) {
        messageText = withText
        messagePopupData = withPopupData
        lsd.capture(("A" messages "B") {
            label(withText)
            data(withPopupData)
        })
        lsd.completeScenario("Scenario")
    }

    private fun givenMultipleCapturedMessages() {
        lsd.capture(
            ("A" messages "B") { label("message1") },
            ("B" messages "C") { label("message2") },
            ("C" messages "B") { label("OK"); duration(2.seconds) },
            ("B" messages "A") { label("OK"); duration(5.seconds) }
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
        assertThat(page.locator("h1.logo")).isVisible()
        assertThat(page.locator("section.diagram")).isVisible()
    }
}
