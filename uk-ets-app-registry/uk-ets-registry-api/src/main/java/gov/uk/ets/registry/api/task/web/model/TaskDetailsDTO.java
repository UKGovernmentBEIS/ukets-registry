package gov.uk.ets.registry.api.task.web.model;

import com.querydsl.core.annotations.QueryProjection;
import gov.uk.ets.registry.api.common.ConversionServiceImpl;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.domain.types.TaskStatus;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

/**
 * Transfer object for tasks. This DTO contains information from the task entity Only.
 * TODO:  find a way to merge with {@link gov.uk.ets.registry.api.task.shared.TaskProjection}
 */
@Getter
@Setter
@NoArgsConstructor
public class TaskDetailsDTO {

    /**
     * The request id.
     */
    private Long requestId;

    /**
     * The task type.
     */
    private RequestType taskType;

    /**
     * The initiator name.
     */
    private String initiatorName;

    /**
     * The initiator id.
     */
    private Long initiatorId;

    /**
     * The initiator URID.
     */
    private String initiatorUrid;

    /**
     * The claimant name.
     */
    private String claimantName;

    /**
     * The claimant URID.
     */
    private String claimantURID;

    /**
     * The task status.
     */
    private TaskStatus taskStatus;

    /**
     * The request status.
     */
    private RequestStateEnum requestStatus;

    /**
     * The initiated date.
     */
    private Date initiatedDate;

    /**
     * The claimed date.
     */
    private Date claimedDate;

    /**
     * Whether the current user is the claimant of this task.
     */
    private Boolean currentUserClaimant;

    /**
     * The account number.
     */
    private String accountNumber;

    /**
     * The account full identifier.
     */
    private String accountFullIdentifier;

    /**
     * The account name.
     */
    private String accountName;

    /**
     * The referred user's first name.
     */
    private String referredUserFirstName;

    /**
     * The referred user's last name.
     */
    private String referredUserLastName;

    /**
     * The referred user's URID.
     */
    private String referredUserURID;

    /**
     * The file name.
     */
    private String fileName;

    /**
     * The file size.
     */
    private String fileSize;

    /**
     * The transaction identifier filled for {@link RequestType#TRANSACTION_REQUEST} tasks.
     */
    private List<String> transactionIdentifiers;

    // TODO describe this field.
    private String difference;

    /**
     * The before string.
     */
    private String before;

    /**
     * A list of sub tasks
     */
    private List<TaskDetailsDTO> subTasks;

    /**
     * The parent task
     */
    private TaskDetailsDTO parentTask;

    private Date completedDate;

    private Date deadline;

    /**
     * Constructor.
     */
    @QueryProjection
    public TaskDetailsDTO(Long requestId, RequestType type, RequestStateEnum status, String initiatorFirstName,
                          String initiatorLastName, Long initiatorId, String initiatorUrid, String initiatorKnownAs, Date initiatedDate,
                          String claimantFirstName, String claimantLastName, String claimantKnownAs,
                          String claimantURID, Date claimedDate, Long accountIdentifier, String accountFullIdentifier,
                          String accountName, String referredUserFirstName,
                          String referredUserLastName, String referredUserURID, byte[] file,
                          String transactionIdentifiers, String difference, String before, Long parentRequestId,
                          RequestType parentType, Date completedDate, Date deadline) {
        
        this( requestId,  type,  status,  initiatorFirstName,
             initiatorLastName,  initiatorId,  initiatorUrid, initiatorKnownAs,  initiatedDate,
             claimantFirstName, claimantLastName, claimantKnownAs,
             claimantURID,  claimedDate,  accountIdentifier,  accountFullIdentifier,
             accountName,  referredUserFirstName,
             referredUserLastName,  referredUserURID, file,
             Objects.nonNull(transactionIdentifiers) ?  Stream.of(transactionIdentifiers.split(",", -1)).collect(Collectors.toList()) : null,  
             difference,  before,  parentRequestId, parentType,  completedDate, deadline);
    }


    private String getDisplayName(String firstName, String lastName, String knownAs){
        boolean knownAsExists = knownAs != null && !org.apache.commons.lang3.StringUtils.isEmpty(knownAs);
        boolean fullNameExists = firstName != null && lastName != null && !org.apache.commons.lang3.StringUtils.isEmpty(firstName) && !org.apache.commons.lang3.StringUtils.isEmpty(lastName);
        if(!fullNameExists && !knownAsExists) {
            return "";
        } else {
            return knownAsExists ? knownAs : firstName + " " + lastName;
        }
    }


    /**
     * Constructor.
     */
    @QueryProjection
    public TaskDetailsDTO(Long requestId, RequestType type, RequestStateEnum status, String initiatorFirstName,
                          String initiatorLastName, Long initiatorId, String initiatorUrid, String initiatorKnownAs, Date initiatedDate,
                          String claimantFirstName, String claimantLastName, String claimantKnownAs,
                          String claimantURID, Date claimedDate, Long accountIdentifier, String accountFullIdentifier,
                          String accountName, String referredUserFirstName,
                          String referredUserLastName, String referredUserURID, byte[] file,
                          List<String> transactionIdentifiers, String difference, String before, Long parentRequestId,
                          RequestType parentType, Date completedDate, Date deadline) {
        this.requestId = requestId;
        this.taskType = type;
        this.initiatorName = this.getDisplayName(initiatorFirstName, initiatorLastName, initiatorKnownAs);
        this.initiatorId = initiatorId;
        this.initiatorUrid = initiatorUrid;
        this.claimantName = this.getDisplayName(claimantFirstName, claimantLastName, claimantKnownAs);
        this.accountNumber = Objects.toString(accountIdentifier, null);
        this.accountFullIdentifier = accountFullIdentifier;
        this.accountName = Objects.toString(accountName, null);
        TaskStatus taskStatus = TaskStatus.UNCLAIMED;
        if (RequestStateEnum.APPROVED.equals(status) || RequestStateEnum.REJECTED.equals(status)) {
            taskStatus = TaskStatus.COMPLETED;

        } else if (StringUtils.hasText(claimantName)) {
            taskStatus = TaskStatus.CLAIMED;

        }
        this.taskStatus = taskStatus;
        this.requestStatus = status;

        this.initiatedDate = initiatedDate;
        this.claimedDate = claimedDate;
        this.claimantURID = claimantURID;

        this.referredUserFirstName = referredUserFirstName;
        this.referredUserLastName = referredUserLastName;
        this.referredUserURID = referredUserURID;

        if (file != null) {
            this.fileName = "registry_activation_code_" + initiatedDate.getTime() + ".pdf";
            this.fileSize = new ConversionServiceImpl().convertByteAmountToHumanReadable(file.length);
        }
        this.transactionIdentifiers = transactionIdentifiers;
        this.difference = difference;
        this.before = before;
        TaskDetailsDTO dto = new TaskDetailsDTO();
        dto.setRequestId(parentRequestId);
        dto.setTaskType(parentType);
        this.parentTask = dto;
        this.completedDate = completedDate;
        this.deadline = deadline;
    }

    /**
     * TaskDetails DTO constructor.
     *
     * @param taskDetailsDTO an input
     */
    public TaskDetailsDTO(TaskDetailsDTO taskDetailsDTO) {
        this.setRequestId(taskDetailsDTO.getRequestId());
        this.setTaskType(taskDetailsDTO.getTaskType());
        this.setInitiatorName(taskDetailsDTO.getInitiatorName());
        this.setInitiatorId(taskDetailsDTO.getInitiatorId());
        this.setInitiatorUrid(taskDetailsDTO.getInitiatorUrid());
        this.setClaimantName(taskDetailsDTO.getClaimantName());
        this.setClaimantURID(taskDetailsDTO.getClaimantURID());
        this.setTaskStatus(taskDetailsDTO.getTaskStatus());
        this.setRequestStatus(taskDetailsDTO.getRequestStatus());
        this.setInitiatedDate(taskDetailsDTO.getInitiatedDate());
        this.setClaimedDate(taskDetailsDTO.getClaimedDate());
        this.setCurrentUserClaimant(taskDetailsDTO.getCurrentUserClaimant());
        this.setAccountNumber(taskDetailsDTO.getAccountNumber());
        this.setAccountFullIdentifier(taskDetailsDTO.getAccountFullIdentifier());
        this.setAccountName(taskDetailsDTO.getAccountName());
        this.setReferredUserFirstName(taskDetailsDTO.getReferredUserFirstName());
        this.setReferredUserLastName(taskDetailsDTO.getReferredUserLastName());
        this.setReferredUserURID(taskDetailsDTO.getReferredUserURID());
        this.setFileName(taskDetailsDTO.getFileName());
        this.setFileSize(taskDetailsDTO.getFileSize());
        this.setTransactionIdentifiers(taskDetailsDTO.getTransactionIdentifiers());
        this.setDifference(taskDetailsDTO.getDifference());
        this.setBefore(taskDetailsDTO.getBefore());
        this.setSubTasks(taskDetailsDTO.getSubTasks());
        this.setParentTask(taskDetailsDTO.getParentTask());
        this.setCompletedDate(taskDetailsDTO.getCompletedDate());
        this.setDeadline(taskDetailsDTO.getDeadline());
    }
}
