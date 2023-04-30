package com.lsd.core.report

import com.lsd.core.LsdContext
import com.lsd.core.domain.Status
import com.microsoft.playwright.Page
import com.microsoft.playwright.Page.*
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import com.microsoft.playwright.options.ScreenshotAnimations.*
import org.junit.jupiter.api.Test
import java.nio.file.Paths
import kotlin.io.path.absolute


class IndexUiTest {

    private val lsd = LsdContext()
    private val playwright = Playwright.create()
    private val chromium = playwright.chromium()
    private val browser = chromium.launch()
    private val page = browser.newPage()

    @Test
    fun createsIndex() {
        givenAReportWithSuccessWarnAndErrorScenarios(title = "First Report")
        andAReportWithSuccessAndWarnScenarios(title = "Second Report")
        andAReportWithSuccessScenario(title = "Third Report")
        andAReportWithNoScenarios(title = "Fourth Report")

        whenTheIndexIsGenerated()

        thenTheIndexPageContainsLinksToAllReports()
        page.screenshot(path = "docs/example_index.png")
    }

    private fun givenAReportWithSuccessWarnAndErrorScenarios(title: String) {
        lsd.completeScenario("Scenario 1", "Error", Status.ERROR)
        lsd.completeScenario("Scenario 2", "Warn", Status.FAILURE)
        lsd.completeScenario("Scenario 3", "Success", Status.SUCCESS)
        lsd.completeReport(title)
    }

    private fun andAReportWithNoScenarios(title: String) {
        lsd.completeReport(title)
    }

    private fun andAReportWithSuccessScenario(title: String) {
        lsd.completeScenario("Scenario 1", "Success", Status.SUCCESS)
        lsd.completeReport(title)
    }

    private fun andAReportWithSuccessAndWarnScenarios(title: String) {
        lsd.completeScenario("Scenario 1", "Warn", Status.FAILURE)
        lsd.completeScenario("Scenario 2", "Success", Status.SUCCESS)
        lsd.completeReport(title)
    }

    private fun whenTheIndexIsGenerated() {
        page.navigate("file://${lsd.createIndex().absolute()}")
    }

    private fun thenTheIndexPageContainsLinksToAllReports() {
        assertThat(page).hasTitle("Index")
        assertThat(page.locator("h1.logo")).isVisible()
        assertThat(page.locator("label.index")).hasText("Available Reports")

        assertHyperlinkCreated(linkText = "First Report", cssClass = "error", href = "FirstReport.html")
        assertHyperlinkCreated(linkText = "Second Report", cssClass = "warn", href = "SecondReport.html")
        assertHyperlinkCreated(linkText = "Third Report", cssClass = "success", href = "ThirdReport.html")
        assertHyperlinkCreated(linkText = "Fourth Report", cssClass = "", href = "FourthReport.html")
    }

    private fun assertHyperlinkCreated(linkText: String, cssClass: String, href: String) {
        page.getByText(linkText).let {
            assertThat(it).isVisible()
            assertThat(it).hasClass(cssClass)
            assertThat(it).hasAttribute("href", href)
        }
    }
}