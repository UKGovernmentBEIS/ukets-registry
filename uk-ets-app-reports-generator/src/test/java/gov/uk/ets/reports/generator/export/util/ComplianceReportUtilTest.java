package gov.uk.ets.reports.generator.export.util;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ComplianceReportUtilTest {

    @Test
    @DisplayName("If start year is after the reference year the reporting years/months are null")
    public void startYearAfterReferenceYear () {
        Integer reportingYears = ComplianceReportUtil.calculateReportingYears(2022, 2023, 2021);
        Integer reportingMonths = ComplianceReportUtil.calculateReportingMonths(2022, 2023, 2021);

        assertThat(reportingYears).isEqualTo(null);
        assertThat(reportingMonths).isEqualTo(null);
    }

    @Test
    @DisplayName("If start year is before reference year and the end year is missing, the reporting years/months are calculated until the reference year inclusive ")
    public void startYearBeforeReferenceYear () {
        Integer reportingYears = ComplianceReportUtil.calculateReportingYears(2022, 0, 2023);
        Integer reportingMonths = ComplianceReportUtil.calculateReportingMonths(2022, 0, 2023);

        assertThat(reportingYears).isEqualTo(2);
        assertThat(reportingMonths).isEqualTo(24);
    }


    @Test
    @DisplayName("If start year is before reference year, end year is not missing and is before the reference year, the reporting years/months are calculated until the end year inclusive ")
    public void startYearBeforeReferenceYearAndEndYearNotEmptyAndBeforeReferenceYear () {
        Integer reportingYears = ComplianceReportUtil.calculateReportingYears(2022, 2024, 2025);
        Integer reportingMonths = ComplianceReportUtil.calculateReportingMonths(2022, 2024, 2025);

        assertThat(reportingYears).isEqualTo(3);
        assertThat(reportingMonths).isEqualTo(36);
    }

    @Test
    @DisplayName("If start year is before reference year, end year is not missing and is before the reference year, the reporting years/months are calculated until the reference year inclusive ")
    public void startYearBeforeReferenceYearAndEndYearNotEmptyAndAfterReferenceYear () {
        Integer reportingYears = ComplianceReportUtil.calculateReportingYears(2022, 2027, 2025);
        Integer reportingMonths = ComplianceReportUtil.calculateReportingMonths(2022, 2027, 2025);

        assertThat(reportingYears).isEqualTo(4);
        assertThat(reportingMonths).isEqualTo(48);
    }
}
