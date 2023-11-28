package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class AllocationsReportData extends ReportData {

    String accountHolderName;
    String permitOrMonitoringPlanId;
    Long identifier;
    String installationName;
    String activityTypeCode;
    String regulator;
    String type;

    String withholdStatus2021;
    String withholdStatus2022;
    String withholdStatus2023;
    String withholdStatus2024;
    String withholdStatus2025;
    String withholdStatus2026;
    String withholdStatus2027;
    String withholdStatus2028;
    String withholdStatus2029;
    String withholdStatus2030;

    Long entitlement2021;
    Long entitlement2022;
    Long entitlement2023;
    Long entitlement2024;
    Long entitlement2025;
    Long entitlement2026;
    Long entitlement2027;
    Long entitlement2028;
    Long entitlement2029;
    Long entitlement2030;

    Long allocated2021;
    Long allocated2022;
    Long allocated2023;
    Long allocated2024;
    Long allocated2025;
    Long allocated2026;
    Long allocated2027;
    Long allocated2028;
    Long allocated2029;
    Long allocated2030;

    Long remaining2021;
    Long remaining2022;
    Long remaining2023;
    Long remaining2024;
    Long remaining2025;
    Long remaining2026;
    Long remaining2027;
    Long remaining2028;
    Long remaining2029;
    Long remaining2030;
}
