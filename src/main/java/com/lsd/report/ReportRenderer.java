package com.lsd.report;

import com.lsd.report.model.Report;

import java.io.Writer;

public interface ReportRenderer {
    String render(Report report);
    String render(Report report, Writer writer);
}
