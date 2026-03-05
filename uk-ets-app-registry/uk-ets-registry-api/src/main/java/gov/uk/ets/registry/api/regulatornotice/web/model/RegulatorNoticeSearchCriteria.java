package gov.uk.ets.registry.api.regulatornotice.web.model;

import gov.uk.ets.registry.api.task.domain.types.TaskStatus;
import lombok.Getter;
import lombok.Setter;

/**
 * Search criterias for Regulator Notices.
 */
@Getter
@Setter
public class RegulatorNoticeSearchCriteria {

    /**
     * The account number.
     */
    private String accountNumber;

    /**
     * The account holder identifier.
     */
    private Long accountHolderId;

    private String processType;

    /**
     * The task status.
     */
    private TaskStatus taskStatus;

    /**
     * The operator ID.
     */
    private Long operatorId;

    private String permitOrMonitoringPlanIdentifier;

    /**
     * The claimant name.
     */
    private String claimantName;

}
