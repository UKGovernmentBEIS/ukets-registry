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

    private Long aircraftOperatorId;
    private String monitoringPlanId;
    private Long firstYearOfVerifiedEmissionSubmission;
    private Long lastYearOfVerifiedEmissionSubmission;
}
