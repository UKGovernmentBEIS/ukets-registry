package gov.uk.ets.reports.generator.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompliantEntity {
    private String regulator;
    private String complianceStatus;

    private Long installationId;
    private String installationName;
    private String installationActivity;
    private String installationPermitId;

    private Long firstYearOfVerifiedEmissionSubmission;
    private Long lastYearOfVerifiedEmissionSubmission;

    private Long aircraftOperatorId;
    private String aircraftMonitoringPlanId;

    private Long maritimeOperatorId;
    private String maritimeMonitoringPlanId;
    private String imo;
}
