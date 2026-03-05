package gov.uk.ets.registry.api.regulatornotice.web.model;

import gov.uk.ets.registry.api.common.search.SearchResult;
import gov.uk.ets.registry.api.regulatornotice.shared.RegulatorNoticeProjection;
import gov.uk.ets.registry.api.task.domain.types.TaskStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RegulatorNoticeSearchResult implements SearchResult {

    private static final long serialVersionUID = -8448525332292925688L;

    public static RegulatorNoticeSearchResult of(RegulatorNoticeProjection projection) {
        RegulatorNoticeSearchResult result = new RegulatorNoticeSearchResult();
        result.setRequestId(projection.getRequestId());
        result.setAccountHolderName(mapAccountHolderName(projection));
        result.setPermitOrMonitoringPlanIdentifier(projection.getPermitOrMonitoringPlanIdentifier());
        result.setProcessType(projection.getProcessType());
        result.setInitiatedDate(projection.getInitiatedDate());
        result.setClaimantName(projection.getClaimantName());
        result.setTaskStatus(projection.getTaskStatus());
        return result;
    }

    private static String mapAccountHolderName(RegulatorNoticeProjection projection) {
        if (projection == null) {
            return null;
        }
        String fullName;
        if (projection.getAccountHolderFirstName() != null && projection.getAccountHolderLastName() != null) {
            fullName = projection.getAccountHolderFirstName() + " " + projection.getAccountHolderLastName();
        } else {
            fullName = projection.getAccountHolderName();
        }
        return fullName;
    }

    /**
     * The request id.
     */
    private Long requestId;

    /**
     * The account holder name
     */
    private String accountHolderName;

    /**
     * The permit identifier of the installation
     * or the monitoring plan identifier of account.
     */
    private String permitOrMonitoringPlanIdentifier;

    private String processType;

    /**
     * When this task was created.
     */
    private Date initiatedDate;

    /**
     * The claimant name.
     */
    private String claimantName;

    /**
     * The task status.
     */
    private TaskStatus taskStatus;
}
