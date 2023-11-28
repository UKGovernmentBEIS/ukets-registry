package gov.uk.ets.registry.api.task.shared;

import java.util.Date;
import org.springframework.util.StringUtils;
import com.querydsl.core.annotations.QueryProjection;
import gov.uk.ets.registry.api.common.ConversionServiceImpl;
import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.common.search.SearchResult;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.domain.types.TaskStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskProjection implements SearchResult {
    private static final long serialVersionUID = -8448525332292925619L;
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
     * The initiator ID.
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
     * The name who completed the task.
     */
    private String completedByName;

    /**
     * The account number.
     */
    private String accountNumber;

    /**
     * The account full identifier.
     */
    private String accountFullIdentifier;

    /**
     * The account type.
     */
    private String accountType;

    /**
     * The kyoto account type.
     */
    private KyotoAccountType kyotoAccountType;

    /**
     * The registry account type.
     */
    private RegistryAccountType registryAccountType;

    /**
     * The account status.
     */
    private AccountStatus accountStatus;

    /**
     * The account holder.
     */
    private String accountHolder;

    /**
     * The allocation category.
     */
    private String allocationCategory;

    /**
     * The allocation year.
     */
    private String allocationYear;

    /**
     * The authorised representative.
     */
    private String authorisedRepresentative;

    /**
     * checks if authorized representative is administrator
     */
    private String authorizedRepresentativeUserId;

    /**
     * The transaction identifier.
     */
    private String transactionId;

    /**
     * When this task was created.
     */
    private Date createdOn;

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
     * The completed date.
     */
    private Date completedDate;

    /**
     * The claimant URID.
     */
    private String claimantURID;

    /**
     * Whether the current user is the claimant of this task.
     */
    private Boolean currentUserClaimant;

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
     * The Recipient Account Number.
     */
    private String recipientAccountNumber;
    /**
     * The task difference.
     */
    private String difference;

    private Long accountHolderNumber;

    private String accountTypeLabel;

    /**
     * Constructor.
     *
     * @param requestId           The request id.
     * @param taskType            The type.
     * @param initiatorFirstName  The initiator first name.
     * @param initiatorLastName   The initiator last name.
     * @param claimantFirstName   The claimant first name.
     * @param claimantLastName    The claimant last name.
     * @param identifier          The account full identifier.
     * @param fullIdentifier      The account full identifier.
     * @param kyotoAccountType    the kyoto account type.
     * @param registryAccountType The registry account type.
     * @param accountHolder       The account holder name.
     * @param transactionId       The transaction id.
     * @param createdOn           When this task was created.
     * @param status              The task status.
     */
    @QueryProjection
    public TaskProjection(Long requestId, RequestType taskType, String initiatorFirstName, String initiatorLastName,
                          Long initiatorId, String initiatorUrid, String claimantFirstName, String claimantLastName,
                          Long identifier, String fullIdentifier, KyotoAccountType kyotoAccountType,
                          RegistryAccountType registryAccountType, String accountType, AccountStatus accountStatus, 
                          String accountHolder,
                          String authorisedRepresentative, String authorizedRepresentativeUserId,
                          String transactionId, Date createdOn, RequestStateEnum status, String recipientAccountNumber,
                          String difference, String claimantURID, Date completedDate, Long accountHolderNumber,
                          String accountTypeLabel, String allocationCategory,String allocationYear) {
        this(requestId, taskType, status, transactionId,
            initiatorFirstName, initiatorLastName, initiatorId, initiatorUrid, createdOn,
            claimantFirstName, claimantLastName, null,
            null, null, completedDate,
            identifier, fullIdentifier, kyotoAccountType, registryAccountType, accountType,
            accountHolder, authorisedRepresentative,
            claimantURID, null, null, null, null,
            recipientAccountNumber, difference, accountHolderNumber, accountTypeLabel, allocationCategory, allocationYear);
        this.accountStatus = accountStatus;
        this.authorizedRepresentativeUserId = authorizedRepresentativeUserId;
    }

    /**
     * Constructor.
     *
     * @param requestId            The task unique business identifier.
     * @param type                 The task type.
     * @param status               The task status.
     * @param transactionId        The transaction identifier.
     * @param initiatorFirstName   The initiator first name.
     * @param initiatorLastName    The initiator last name.
     * @param initiatedDate        The date when the task was initiated.
     * @param claimantFirstName    The claimant first name.
     * @param claimantLastName     The claimant last name.
     * @param claimedDate          The date when the task was claimed.
     * @param completedByFirstName The first name of the user who completed the task.
     * @param completedByLastName  The last name of the user who completed the task.
     * @param completedDate        The date when the task was completed.
     * @param fullIdentifier       The account full identifier.
     * @param kyotoAccountType     The account kyoto type.
     * @param registryAccountType  The account registry internal type.
     * @param accountHolder        The account holder name.
     * @param claimantURID         The claimant URID.
     */
    public TaskProjection(Long requestId, RequestType type, RequestStateEnum status, String transactionId,
                          String initiatorFirstName, String initiatorLastName, Long initiatorId, String initiatorUrid,
                          Date initiatedDate,
                          String claimantFirstName, String claimantLastName, Date claimedDate,
                          String completedByFirstName, String completedByLastName, Date completedDate,
                          Long identifier, String fullIdentifier, KyotoAccountType kyotoAccountType,
                          RegistryAccountType registryAccountType, String accountType,
                          String accountHolder,
                          String authorisedRepresentative, String claimantURID,
                          String referredUserFirstName, String referredUserLastName,
                          String referredUserURID, byte[] file, String recipientAccountNumber, String difference,
                          Long accountHolderNumber, String accountTypeLabel, String allocationCategory,String allocationYear) {
        this.requestId = requestId;
        this.taskType = type;
        this.initiatorName = initiatorLastName != null ? Utils.concat(" ", initiatorFirstName, initiatorLastName)
                : initiatorFirstName;
        this.claimantName = claimantLastName != null ? Utils.concat(" ", claimantFirstName, claimantLastName)
                : claimantFirstName;
        this.completedByName = Utils.concat(" ", completedByFirstName, completedByLastName);
        this.initiatorId = initiatorId;
        this.initiatorUrid = initiatorUrid;
        this.accountNumber = (identifier != null) ? String.valueOf(identifier) : "";
        this.accountFullIdentifier = (fullIdentifier != null) ? fullIdentifier : "";
        this.kyotoAccountType = kyotoAccountType;
        this.registryAccountType = registryAccountType;
        // accountType is set only if it is retrieved from task search metadata
        if (StringUtils.hasLength(accountType)) {
            KyotoAccountType kyotoCode = KyotoAccountType.parse(accountType);	
            RegistryAccountType registryCode = RegistryAccountType.parse(accountType);
            kyotoAccountType = kyotoCode != null ? kyotoCode : KyotoAccountType.PARTY_HOLDING_ACCOUNT;
            registryAccountType = registryCode != null ? registryCode : RegistryAccountType.NONE;
        }
        if (kyotoAccountType != null && registryAccountType != null) {
            AccountType accountTypeEnum = AccountType.get(registryAccountType, kyotoAccountType);
            this.accountType = accountTypeEnum != null ? accountTypeEnum.name() : null;
        } else if (kyotoAccountType != null) {
            this.accountType = kyotoAccountType.name();
        } else if (registryAccountType != null) {
            this.accountType = registryAccountType.name();
        }
        this.accountHolder = accountHolder;
        this.allocationCategory = allocationCategory;
        this.allocationYear = allocationYear;
        this.authorisedRepresentative = (authorisedRepresentative != null) ? authorisedRepresentative : "";
        this.transactionId = transactionId;
        this.createdOn = initiatedDate;
        TaskStatus calculatedTaskStatus = TaskStatus.UNCLAIMED;
        if (RequestStateEnum.APPROVED.equals(status) || RequestStateEnum.REJECTED.equals(status)) {
            calculatedTaskStatus = TaskStatus.COMPLETED;

        } else if (StringUtils.hasText(claimantName)) {
            calculatedTaskStatus = TaskStatus.CLAIMED;

        }
        this.taskStatus = calculatedTaskStatus;
        this.requestStatus = status;

        this.initiatedDate = initiatedDate;
        this.claimedDate = claimedDate;
        this.completedDate = completedDate;
        this.claimantURID = claimantURID;

        this.referredUserFirstName = referredUserFirstName;
        this.referredUserLastName = referredUserLastName;
        this.referredUserURID = referredUserURID;

        if (file != null) {
            //TODO the file name and size should be moved to the respective dto
            this.fileName = "registry_activation_code_" + initiatedDate.getTime() + ".pdf";
            this.fileSize = new ConversionServiceImpl().convertByteAmountToHumanReadable(file.length);
        }

        this.recipientAccountNumber = recipientAccountNumber;
        this.difference = difference;

        this.accountHolderNumber = accountHolderNumber;
        this.accountTypeLabel = accountTypeLabel;
    }

}
