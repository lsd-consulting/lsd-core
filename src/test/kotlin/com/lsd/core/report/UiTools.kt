package com.lsd.core.report

import com.microsoft.playwright.Page
import com.microsoft.playwright.Page.*
import com.microsoft.playwright.options.ScreenshotAnimations
import java.nio.file.Paths

fun Page.screenshot(path: String) {
    screenshot(ScreenshotOptions().setAnimations(ScreenshotAnimations.DISABLED).setPath(Paths.get(path)))
}