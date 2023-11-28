package gov.uk.ets.registry.api.task.domain.types;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.user.domain.User;

/**
 * Enumerates the various event types.
 */
public enum EventType {

    /**
     * Account related
     */
    ACCOUNT_ALLOCATION_STATUS_UPDATED(Account.class),
    ACCOUNT_DETAILS_UPDATED(Account.class),
    ACCOUNT_STATUS_CHANGED(Account.class),
    ACCOUNT_TASK_COMPLETED(Account.class),
    ACCOUNT_TASK_COMMENT(Account.class),
    ACCOUNT_TASK_CLAIMED(Account.class),
    ACCOUNT_TASK_ASSIGNED(Account.class),
    ACCOUNT_TASK_REQUESTED(Account.class),
    ACCOUNT_TASK_EDIT(Account.class),
    ACCOUNT_TRUSTED_ACCOUNT_UPDATED(Account.class),
    ACCOUNT_TRUSTED_ACCOUNT_CANCELLED(Account.class),
    ACCOUNT_INSTALLATION_TRANSFER_COMPLETED(Account.class),
    ACCOUNT_AR_SUSPENDED(Account.class),
    ACCOUNT_AR_RESTORED(Account.class),
    ACCOUNT_AR_REMOVED(Account.class),
    ACCOUNT_EXCLUSION_STATUS_UPDATED(Account.class),
    ACCOUNT_CLOSURE_REQUEST(Account.class),
    UPLOAD_ACCOUNT_EMISSIONS(Account.class),
    APPROVE_ACCOUNT_EMISSIONS(Account.class),
    ACCOUNT_HOLDER_DELETE_SUBMITTED_DOCUMENT(Account.class),
    ACCOUNT_EXCLUSION_FROM_BILLING(Account.class),
    ACCOUNT_INCLUSION_IN_BILLING(Account.class),

    /**
     * Task related
     */
    TASK_COMMENT(Task.class),
    TASK_REQUESTED(Task.class),
    TASK_CLAIMED(Task.class),
    TASK_ASSIGNED(Task.class),
    TASK_COMPLETED(Task.class),
    TASK_APPROVED(Task.class),
    TASK_REJECTED(Task.class),
    TASK_EDIT(Task.class),
    TASK_DELETE_SUBMITTED_DOCUMENT(Task.class),

    /**
     * Trsansaction related
     */
    TRANSACTION_PROPOSAL(Transaction.class),
    TRANSACTION_PROPOSAL_COMMENT(Transaction.class),
    TRANSACTION_TASK_COMPLETED(Transaction.class),
    TRANSACTION_TASK_COMMENT(Transaction.class),
    TRANSACTION_UKTL_REPLY(Transaction.class),

    /**
     * User related
     */
    USER_STATUS_CHANGED(User.class),
    USER_REGISTERED(User.class),
    USER_VALIDATED(User.class),
    USER_ENROLLED(User.class),
    USER_TASK_COMMENT(User.class),
    USER_DOCUMENTS_REQUESTED(User.class),
    REGISTRY_ACTIVATION_CODE(User.class),
    USER_PASSWORD_CHANGED(User.class),
    TERMS_AND_CONDITIONS_ACCEPTED(User.class),
    USER_TASK_COMPLETED(User.class),
    USER_DELETE_SUBMITTED_DOCUMENT(User.class),
    USER_ROLE_REMOVED(User.class),
    USER_ROLE_ADDED(User.class),
    USER_MINOR_DETAILS_UPDATED(User.class),
    /**
     * NAP UploadedFile related.
     */
    UPLOAD_ALLOCATION_TABLE(UploadedFile.class),
    APPROVE_ALLOCATION_TABLE(UploadedFile.class),
    REJECT_ALLOCATION_TABLE(UploadedFile.class),
    /**
     * Verified Emissions UploadedFile related.
     */
    UPLOAD_EMISSIONS_TABLE(UploadedFile.class),
    APPROVE_EMISSIONS_TABLE(UploadedFile.class),

    /**
     * Compliance Related
     */
    UPLOAD_COMPLIANT_ENTITY_EMISSIONS(CompliantEntity.class),
    APPROVE_COMPLIANT_ENTITY_EMISSIONS(CompliantEntity.class),
    COMPLIANT_ENTITY_EXCLUSION_STATUS_UPDATED(CompliantEntity.class),
    COMPLIANT_ENTITY_SURRENDER_COMPLETED(CompliantEntity.class),
    COMPLIANT_ENTITY_REVERSAL_SURRENDER_COMPLETED(CompliantEntity.class),
    COMPLIANT_ENTITY_EMISSIONS_FIRST_YEAR_UPDATED(CompliantEntity.class),
    COMPLIANT_ENTITY_EMISSIONS_LAST_YEAR_UPDATED(CompliantEntity.class),
    COMPLIANT_ENTITY_NEW_YEAR(CompliantEntity.class),
    COMPLIANT_ENTITY_ROLL_OVER_FAILURE(CompliantEntity.class),

    COMPLIANT_ENTITY_STATIC_COMPLIANCE_REQUEST(CompliantEntity.class),
    COMPLIANT_ENTITY_GET_CURRENT_DYNAMIC_STATUS_REQUEST(CompliantEntity.class),
    COMPLIANT_ENTITY_RECALCULATE_DYNAMIC_STATUS_REQUEST(CompliantEntity.class),
    COMPLIANT_ENTITY_CALCULATION_ERROR(CompliantEntity.class),

    /**
     * Keycloak events.
     */
    INVALID_LOGIN_ATTEMPT(User.class),
    VERIFY_EMAIL(User.class),
    OTP_UPDATED(User.class),

    NOTIFICATION_SENT(Account.class);

    private Class<?> clazz;

    EventType(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
