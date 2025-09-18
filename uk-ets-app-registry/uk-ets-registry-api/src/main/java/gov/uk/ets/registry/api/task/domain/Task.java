package gov.uk.ets.registry.api.task.domain;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.auditevent.DomainObject;
import gov.uk.ets.registry.api.authz.Scope;
import gov.uk.ets.registry.api.common.model.events.DomainEvent;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.searchmetadata.domain.TaskSearchMetadata;
import gov.uk.ets.registry.api.task.service.TaskActionError;
import gov.uk.ets.registry.api.user.domain.User;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.*;

/**
 * Represents a task.
 */

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"requestId"})
public class Task implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = -2429814803730575621L;
    public static final String TASK_IS_COMPLETED_MESSAGE = "Task is completed \n task: %d";

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "task_id_generator", sequenceName = "task_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_id_generator")
    private Long id;

    /**
     * The request business identifier.
     */
    @Column(name = "request_identifier")
    private Long requestId;

    /**
     * The type.
     */
    @Enumerated(EnumType.STRING)
    private RequestType type;

    /**
     * The initiator.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiated_by")
    private User initiatedBy;

    /**
     * When this task was created.
     */
    @Column(name = "initiated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date initiatedDate;

    /**
     * The current claimant.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claimed_by")
    private User claimedBy;

    /**
     * When this task was claimed by the claimant.
     */
    @Column(name = "claimed_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date claimedDate;

    /**
     * The user who completed the task.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "completed_by")
    private User completedBy;

    /**
     * When this task was completed.
     */
    @Column(name = "completed_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date completedDate;

    /**
     * The account.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    /**
     * The transaction identifier.
     */
    @OneToMany(mappedBy = "task",fetch = FetchType.LAZY)
    private List<TaskTransaction> transactionIdentifiers;

    /**
     * The current status (e.g. Approved, Rejected etc.)
     */
    @Enumerated(EnumType.STRING)
    private RequestStateEnum status;

    /**
     * The business information before approving this task.
     */
    @Basic(fetch = FetchType.LAZY)
    @Column(
        columnDefinition = "CLOB"
    )
    private String before;

    /**
     * The business information after approving this task.
     */
    @Basic(fetch = FetchType.LAZY)
    @Column(
        columnDefinition = "CLOB"
    )
    private String after;

    /**
     * The difference in business information introduced by this task.
     */
    @Basic(fetch = FetchType.LAZY)
    @Column(
        columnDefinition = "CLOB"
    )
    private String difference;

    /**
     * The parent task.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;

    /**
     * The user referred to in the task.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * The file created by the task.
     * FIXME: this is not lazy - always fetched
     */
    @Basic(fetch = FetchType.LAZY)
    private byte[] file;
    
    /**
     * The task search metadata.
     */
    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private List<TaskSearchMetadata> taskSearchMetadata;

    /**
     * The task assignments.
     */
    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private List<TaskAssignment> taskAssignments;

    @Column(name = "deadline")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deadline;

    /**
     * Returns whether this task is currently claimed.
     *
     * @return false/true
     */
    public boolean isClaimed() {
        return claimedBy != null && !RequestStateEnum.APPROVED.equals(status) &&
            !RequestStateEnum.REJECTED.equals(status);
    }

    /**
     * Returns whether this task is completed.
     *
     * @return false/true
     */
    public boolean isCompleted() {
        return RequestStateEnum.APPROVED.equals(status) || RequestStateEnum.REJECTED.equals(status);
    }

    /**
     * Returns whether this task is unclaimed.
     *
     * @return false/true.
     */
    public boolean isUnclaimed() {
        return claimedBy == null;
    }

    /**
     * Claims this to claimant.
     *
     * @param claimant The claimant
     * @return The {@link DomainEvent} of {@link Task}
     */
    public DomainEvent<gov.uk.ets.registry.api.auditevent.DomainEvent> claim(User claimant) {
        if (type == null) {
            throw new IllegalStateException("the type of task should not be null at this point");
        }
        if (claimant == null) {
            throw new IllegalArgumentException("the claimant should not be null");
        }
        DomainEvent<gov.uk.ets.registry.api.auditevent.DomainEvent> domainEvent = new DomainEvent<>();
        if (isCompleted()) {
            domainEvent.error(TaskActionError.TASK_COMPLETED,
                String.format(TASK_IS_COMPLETED_MESSAGE, requestId));
        }
        claimedBy = claimant;
        claimedDate = new Date();
        String action = generateEventTypeDescription(" claimed.");
        domainEvent.setPayload(gov.uk.ets.registry.api.auditevent.DomainEvent.builder()
                                                                             .who(claimant.getUrid())
                                                                             .when(claimedDate)
                                                                             .what(action)
                                                                             .domainObject(DomainObject.create(EventType.TASK_CLAIMED.getClazz(), String.valueOf(this.getRequestId())))
                                                                             .description("")
                                                                             .build());
        return domainEvent;
    }

    /**
     * Assigns this to assignee.
     *
     * @param assignor The assignor
     * @param assignee The assignee
     * @param comment  The comment
     * @return The {@link DomainEvent}
     */
    public DomainEvent<gov.uk.ets.registry.api.auditevent.DomainEvent> assign(Assignor assignor, User assignee,
                                                                              String comment) {
        if (type == null) {
            throw new IllegalStateException("the type of task should not be null at this point");
        }
        if (assignor == null) {
            throw new IllegalArgumentException("assignor should not be null");
        }
        if (assignee == null) {
            throw new IllegalArgumentException("assignee should not be null");
        }
        if (comment == null) {
            throw new IllegalArgumentException("comment should not be null");
        }
        DomainEvent<gov.uk.ets.registry.api.auditevent.DomainEvent> domainEvent = new DomainEvent<>();
        if (isCompleted()) {
            domainEvent.error(TaskActionError.TASK_COMPLETED,
                String.format(TASK_IS_COMPLETED_MESSAGE, requestId));
        }
        switch (type) {
            case ACCOUNT_OPENING_REQUEST: {
                if (assignor.getScopes().contains(Scope.SCOPE_TASK_ACCOUNT_OPEN_WRITE.getScopeName()) &&
                    !assignor.getUser().equals(claimedBy)) {
                    domainEvent.error(TaskActionError.TASK_NOT_CLAIMED_BY_ASSIGNOR,
                        String.format(TASK_IS_COMPLETED_MESSAGE, requestId));
                }
                break;
            }
            default: //TODO logic for other task types
        }

        claimedBy = assignee;
        claimedDate = new Date();
        String action = generateEventTypeDescription(" assigned.");
        domainEvent.setPayload(gov.uk.ets.registry.api.auditevent.DomainEvent.builder()
                                                                             .who(assignor.getUser().getUrid())
                                                                             .when(claimedDate)
                                                                             .what(action)
                                                                             .domainObject(DomainObject.create(EventType.TASK_ASSIGNED.getClazz(), String.valueOf(this.getRequestId())))
                                                                             .description(String.format("The assignee user is %s.", claimedBy.getDisclosedName()))
                                                                             .build());
        return domainEvent;
    }

    public String generateEventTypeDescription(String appendText) {
        switch (type) {
            case TRANSACTION_RULES_UPDATE_REQUEST:
                return "Transaction rule update task" + appendText;
            case AUTHORIZED_REPRESENTATIVE_RESTORE_REQUEST:
                return "Authorised representative restore task" + appendText;
            case AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST:
                return "Authorised representative replacement task" + appendText;
            case AUTHORIZED_REPRESENTATIVE_UPDATE_ACCESS_RIGHTS_REQUEST:
                return "Authorised representative update access rights task" + appendText;
            case AUTHORIZED_REPRESENTATIVE_SUSPEND_REQUEST:
                return "Authorised representative suspend task" + appendText;
            case AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST:
                return "Authorised representative removal task" + appendText;
            case AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST:
                return "Authorised representative addition task" + appendText;
            case TRANSACTION_REQUEST:
                return "Transaction proposal task" + appendText;
            case ACCOUNT_OPENING_REQUEST:
                return "Open account task" + appendText;
            case ADD_TRUSTED_ACCOUNT_REQUEST:
                return "Add trusted account task" + appendText;
            case DELETE_TRUSTED_ACCOUNT_REQUEST:
                return "Delete trusted account task" + appendText;
            case PRINT_ENROLMENT_LETTER_REQUEST:
                return "Print enrolment letter task" + appendText;
            case ALLOCATION_TABLE_UPLOAD_REQUEST:
                return "Allocation table upload task" + appendText;
            case ALLOCATION_REQUEST:
                return "Allocation request" + appendText;
            case AH_REQUESTED_DOCUMENT_UPLOAD:
                return "Submit documents for AH task" + appendText;
            case AR_REQUESTED_DOCUMENT_UPLOAD:
                return "Submit documents for user task" + appendText;
            case ACCOUNT_HOLDER_UPDATE_DETAILS:
                return "Account Holder update details " + appendText;
            case ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS:
                return "Account Holder update primary contact " + appendText;
            case ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_UPDATE:
                return "Account Holder update alternative primary contact " + appendText;
            case ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_DELETE:
                return "Account Holder remove alternative primary contact " + appendText;
            case ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD:
                return "Account Holder add alternative primary contact " + appendText;
            case CHANGE_TOKEN:
                return "Request to change 2FA" + appendText;
            case EMISSIONS_TABLE_UPLOAD_REQUEST:
                return "Emissions table upload task" + appendText;
            case LOST_TOKEN:
                return "Emergency request to change 2FA" + appendText;
            case REQUESTED_EMAIL_CHANGE:
                return "Request to change email address " + appendText;
            case LOST_PASSWORD_AND_TOKEN:
                return "Emergency request to change Password & 2FA" + appendText;
            case INSTALLATION_OPERATOR_UPDATE_REQUEST:
                return "Installation operator update request task" + appendText;
            case AIRCRAFT_OPERATOR_UPDATE_REQUEST:
                return "Aircraft operator update request task" + appendText;
            case MARITIME_OPERATOR_UPDATE_REQUEST:
                return "Maritime operator update request task" + appendText;
            case ACCOUNT_TRANSFER:
                return "Account transfer request task" + appendText;
            case ACCOUNT_CLOSURE_REQUEST:
                return "Account closure request task" + appendText;
            case USER_DETAILS_UPDATE_REQUEST:
                return "User details update request task" + appendText;
            case USER_DEACTIVATION_REQUEST:
                return "User deactivation request task" + appendText;
            case ACCOUNT_OPENING_INSTALLATION_TRANSFER_REQUEST:
                return "Open account with installation transfer task" + appendText;
            default:
                return "";
        }
    }
}
