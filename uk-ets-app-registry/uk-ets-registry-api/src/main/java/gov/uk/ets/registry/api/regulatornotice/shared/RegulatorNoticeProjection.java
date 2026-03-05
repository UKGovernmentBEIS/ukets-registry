package gov.uk.ets.registry.api.regulatornotice.shared;

import com.querydsl.core.annotations.QueryProjection;
import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.common.search.SearchResult;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.TaskStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.Date;

@Getter
@Setter
public class RegulatorNoticeProjection implements SearchResult {

    /**
     * The request id.
     */
    private Long requestId;
    private String accountHolderFirstName;
    private String accountHolderLastName;
    private String accountHolderName;
    private String permitOrMonitoringPlanIdentifier;
    private String processType;
    /**
     * The initiated date.
     */
    private Date initiatedDate;
    /**
     * The claimant name.
     */
    private String claimantName;
    /**
     * The claimant URID.
     */
    private String claimantURID;

    /**
     * The claimed date.
     */
    private Date claimedDate;
    /**
     * The task status.
     */
    private TaskStatus taskStatus;

    /**
     * The name who completed the task.
     */
    private String completedByName;

    /**
     * The completed date.
     */
    private Date completedDate;

    /**
     * Constructor.
     *
     * @param requestId                        The task unique business identifier.
     * @param claimantFirstName                The claimant first name.
     * @param claimantLastName                 The claimant last name.
     * @param initiatedDate                    The date when the task was initiated.
     * @param claimedDate                      The date when the task was claimed.
     * @param completedByFirstName             The first name of the user who completed the task.
     * @param completedByLastName              The last name of the user who completed the task.
     * @param completedDate                    The date when the task was completed.
     * @param accountHolderFirstName           Account holder first name.
     * @param accountHolderLastName            Account holder last name.
     * @param accountHolderName                Account holder actual name.
     * @param requestState                     The task status.
     * @param permitOrMonitoringPlanIdentifier The permit or monitoring plan identifier.
     * @param processType                      The process type (notice type).
     * @param claimantURID                     The claimant URID.
     */
    @QueryProjection
    public RegulatorNoticeProjection(Long requestId, String accountHolderFirstName, String accountHolderLastName,
                                     String claimantFirstName, String claimantLastName, String claimantURID, Date claimedDate,
                                     String completedByFirstName, String completedByLastName, Date completedDate,
                                     String accountHolderName, String permitOrMonitoringPlanIdentifier,
                                     String processType, Date initiatedDate, RequestStateEnum requestState) {


        this.requestId = requestId;
        this.accountHolderFirstName = accountHolderFirstName;
        this.accountHolderLastName = accountHolderLastName;
        this.accountHolderName = accountHolderName;
        this.permitOrMonitoringPlanIdentifier = permitOrMonitoringPlanIdentifier;
        this.processType = processType;
        this.initiatedDate = initiatedDate;
        this.claimantName = claimantLastName != null ? Utils.concat(" ", claimantFirstName, claimantLastName)
                : claimantFirstName;
        this.claimantURID = claimantURID;
        this.claimedDate = claimedDate;
        TaskStatus calculatedTaskStatus = TaskStatus.UNCLAIMED;
        if (RequestStateEnum.APPROVED.equals(requestState) || RequestStateEnum.REJECTED.equals(requestState)) {
            calculatedTaskStatus = TaskStatus.COMPLETED;

        } else if (StringUtils.hasText(claimantName)) {
            calculatedTaskStatus = TaskStatus.CLAIMED;
        }
        this.taskStatus = calculatedTaskStatus;
        this.completedByName = completedByLastName != null ? Utils.concat(" ", completedByFirstName, completedByLastName)
                : completedByFirstName;
        this.completedDate = completedDate;
    }
}
