package com.lsd.core.report

import com.lsd.core.report.model.Report

interface ReportRenderer {
    fun render(report: Report): String
}