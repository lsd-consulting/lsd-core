package com.lsd.core

import com.lsd.core.domain.Status
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.io.path.absolute


class IndexUiTest {

    private val lsd = LsdContext()
    private val playwright = Playwright.create()
    private val chromium = playwright.chromium()
    private val browser = chromium.launch()
    private val page = browser.newPage()

    @Test
    fun createsIndex() {
        lsd.completeScenario("Scenario 1", "Error", Status.ERROR)
        lsd.completeScenario("Scenario 2", "Warn", Status.FAILURE)
        lsd.completeScenario("Scenario 3", "Success", Status.SUCCESS)
        lsd.completeReport("First Report (Error)")
        lsd.completeScenario("Scenario 1", "Warn", Status.FAILURE)
        lsd.completeScenario("Scenario 2", "Success", Status.SUCCESS)
        lsd.completeReport("Second Report (Warn)")
        lsd.completeScenario("Scenario 1", "Success", Status.SUCCESS)
        lsd.completeReport("Third Report")
        lsd.completeReport("Fourth Report")

        page.navigate("file://${lsd.createIndex().absolute()}")

        assertThat(page).hasTitle("Index")
        assertThat(page.locator("h1.logo")).isVisible()
        assertThat(page.locator("label.index")).hasText("Available Reports")

        page.getByText("First Report (Error)").let {
            assertThat(it).isVisible()
            assertThat(it).hasClass("error")
            assertThat(it).hasAttribute("href", "FirstReport(error).html")
        }

        page.getByText("Second Report (Warn)").let {
            assertThat(it).isVisible()
            assertThat(it).hasClass("warn")
            assertThat(it).hasAttribute("href", "SecondReport(warn).html")
        }

        page.getByText("Third Report").let {
            assertThat(it).isVisible()
            assertThat(it).hasClass("success")
            assertThat(it).hasAttribute("href", "ThirdReport.html")
        }

        page.getByText("Fourth Report").let {
            assertThat(it).isVisible()
            assertThat(it).hasClass("")
            assertThat(it).hasAttribute("href", "FourthReport.html")
        }
    }
}