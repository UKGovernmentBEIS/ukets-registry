package gov.uk.ets.reports.generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"gov.uk.ets.reports.generator", "gov.uk.ets.commons"})
public class UkEtsAppReportsGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(UkEtsAppReportsGeneratorApplication.class, args);
    }

}
