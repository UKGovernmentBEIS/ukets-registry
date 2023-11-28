package gov.uk.ets.reports.generator.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({"classpath:pdf_report.properties"})
@Getter
public class PdfReportConfig {

    @Value("${image.logo.path}")
    private String logoPath;

}
