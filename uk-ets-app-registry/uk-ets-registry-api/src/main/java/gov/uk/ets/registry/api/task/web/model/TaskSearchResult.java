package gov.uk.ets.registry.api.task.web.model;

import gov.uk.ets.registry.api.common.search.SearchResult;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.domain.types.TaskStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = "requestId")
public class TaskSearchResult implements SearchResult {
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
     * The claimant name.
     */
    private String claimantName;

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
     * The authorised representative.
     */
    private String authorisedRepresentative;

    /**
     * checks if authorized representative is administrator
     */
    private String authorizedRepresentativeUserId;

    /**
     * The Recipient Account Number.
     */
    private String recipientAccountNumber;

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
     * Flag to determine if the user has access to the account of the task.
     */
    private boolean userHasAccess;

    /**
     * ΅When sorting by account type, we need to have in the UI the same label that was sorted in the backend.
     * In case of open account tasks, the label is empty in the backend, but was not empty in the UI.
     */
    private String accountTypeLabel;

}
