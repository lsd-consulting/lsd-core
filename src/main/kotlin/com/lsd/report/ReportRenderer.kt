package com.lsd.report

import com.lsd.report.model.Report

interface ReportRenderer {
    fun render(report: Report): String
}