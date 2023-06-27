package com.lsd.core.report

import com.lsd.core.LsdContext
import com.lsd.core.ReportOptions
import com.lsd.core.builders.MessageBuilder.Companion.messageBuilder
import com.microsoft.playwright.Page.GetByRoleOptions
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.assertions.LocatorAssertions.IsVisibleOptions
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import com.microsoft.playwright.options.AriaRole.HEADING
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.time.Duration
import kotlin.io.path.absolute


class ReportUiTest {

    private val lsd = LsdContext()
    private val playwright = Playwright.create()
    private val chromium = playwright.chromium()
    private val browser = chromium.launch()
    private val page = browser.newPage()
    private var options = ReportOptions()

    private lateinit var messagePopupData: String
    private lateinit var messageText: String

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

    private fun givenMultipleCapturedMessages() {
        lsd.capture(messageBuilder().from("A").to("B").label("message1").build())
        lsd.capture(messageBuilder().from("B").to("C").label("message2").build())
        lsd.capture(messageBuilder().from("C").to("B").label("OK").duration(Duration.ofSeconds(2)).build())
        lsd.capture(messageBuilder().from("B").to("A").label("OK").duration(Duration.ofSeconds(5)).build())
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
        if (!screenshotName.isNullOrBlank()) page.capture(name=screenshotName)
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
