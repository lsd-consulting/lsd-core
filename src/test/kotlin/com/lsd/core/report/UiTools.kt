package com.lsd.core.report

import com.microsoft.playwright.Page
import com.microsoft.playwright.Page.*
import com.microsoft.playwright.options.ScreenshotAnimations
import java.nio.file.Paths

fun Page.capture(name: String, fullPage: Boolean = true) {
    screenshot(ScreenshotOptions()
        .setAnimations(ScreenshotAnimations.DISABLED)
        .setPath(Paths.get("docs/$name.png"))
        .setFullPage(fullPage)
    )
}