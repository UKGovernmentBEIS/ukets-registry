package gov.uk.ets.reports.generator.export;

import static org.assertj.core.api.Assertions.assertThat;

import gov.uk.ets.reports.model.ReportType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ReportFileGeneratorHelperTest {


    @Test
    public void shouldGenerateFileName() {
        LocalDateTime date = LocalDateTime.of(2021, 1, 1, 14, 22, 33, 678000000);

        String fileName = ReportFileGeneratorHelper.generateFileName(ReportType.R0001, null, date);

        assertThat(fileName).isEqualTo("uk_ets_" + ReportType.R0001.getName() + "_" + "20210101142233678.xlsx");
    }
    
    @Test
    public void shouldGenerateFileNameForOha() {
        LocalDateTime date = LocalDateTime.of(2021, 1, 1, 14, 22, 33, 678000000);
        int year = LocalDate.now().getYear();
        String fileName = ReportFileGeneratorHelper.generateFileName(ReportType.R0013, (long) year, date);

        assertThat(fileName).isEqualTo("uk_ets_" + ReportType.R0013.getName() + year + "_" + "20210101142233678.xlsx");
    }
    
    @Test
    public void shouldGenerateFileNameForTransactionDetails() {
        LocalDateTime date = LocalDateTime.of(2021, 1, 1, 14, 22, 33, 678000000);
        int year = LocalDate.now().getYear();
        String fileName = ReportFileGeneratorHelper.generateFileName(ReportType.R0034, null, date);

        assertThat(fileName).isEqualTo("uk_ets_" + ReportType.R0034.getName() + "_" + "20210101142233678.pdf");
    }
}
