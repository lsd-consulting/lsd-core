package com.lsd.report;

import com.lsd.properties.LsdProperties;
import com.lsd.report.model.Report;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;

import static com.lsd.properties.LsdProperties.OUTPUT_DIR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HtmlReportWriterTest {
    private final String outputDirectory = LsdProperties.get(OUTPUT_DIR);
    private final ReportRenderer mockRenderer = mock(ReportRenderer.class);
    private final HtmlReportWriter underTest = new HtmlReportWriter(mockRenderer);
    private final Report aReport = Report.builder().title("report title").build();

    @BeforeEach
    public void setup() {
        when(mockRenderer.render(any())).thenReturn("report content");
        when(mockRenderer.render(any(), any())).thenReturn("report content");
    }

    @Test
    void writesFileContainingRenderedContent() {
        underTest.writeToFile(aReport);

        assertThat(new File(outputDirectory, "reportTitle.html"))
                .exists()
                .hasContent("report content");
    }

    @Test
    void addCssFileToOutputDirectory() {
        underTest.writeToFile(aReport);

        assertThat(new File(LsdProperties.get(OUTPUT_DIR), "style.css")).exists();
    }
    
    @Test
    void addJavaScriptFileToOutputDirectory() {
        underTest.writeToFile(aReport);

        assertThat(new File(LsdProperties.get(OUTPUT_DIR), "custom.js")).exists();
    }

    @Test
    void writeToAWriter() {
        Writer writer = new StringWriter();

        String result = underTest.writeTo(aReport, writer);

        assertThat(result).contains("report content");
    }
}