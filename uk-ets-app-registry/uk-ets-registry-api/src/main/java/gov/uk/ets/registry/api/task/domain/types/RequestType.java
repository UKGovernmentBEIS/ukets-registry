package gov.uk.ets.registry.api.task.domain.types;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

/**
 * Enumerates the available request types.
 */
public enum RequestType {

    /**
     * The account opening request.
     */
    ACCOUNT_OPENING_REQUEST,

    /**
     * Account opening request with installation transfer.
     */
    ACCOUNT_OPENING_INSTALLATION_TRANSFER_REQUEST,

    /**
     * The add trusted account request.
     */
    ADD_TRUSTED_ACCOUNT_REQUEST(true, false),

    /**
     * The delete trusted account request.
     */
    DELETE_TRUSTED_ACCOUNT_REQUEST(true, false),

    /**
     * The send enrolment key request.
     */
    PRINT_ENROLMENT_LETTER_REQUEST(false, true),

    /**
     * The transaction request.
     */
    TRANSACTION_REQUEST(true, false),

    /**
     * Request type of adding a new AR to to the ARs of the account.
     */
    AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST(false, true),

    /**
     * Request type of removing an AR from the ARs.
     */
    AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST(false, true),

    /**
     * Request type of replacing an AR with other.
     */
    AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST(false, true),

    /**
     * Request type of changing the access right of an AR of the account.
     */
    AUTHORIZED_REPRESENTATIVE_UPDATE_ACCESS_RIGHTS_REQUEST(false, true),

    /**
     * Request type of suspending an AR of the account.
     */
    AUTHORIZED_REPRESENTATIVE_SUSPEND_REQUEST(false, true),

    /**
     * Request type of making an AR of the account active again.
     */
    AUTHORIZED_REPRESENTATIVE_RESTORE_REQUEST(false, true),

    /**
     * Request type of changing the transaction rules for the tal of the specific account.
     */
    TRANSACTION_RULES_UPDATE_REQUEST,

    /**
     * Request type of uploading the allocation table.
     */
    ALLOCATION_TABLE_UPLOAD_REQUEST,

    /**
     * Request type of creating allocation job.
     */
    ALLOCATION_REQUEST,

    /**
     * Request type of requested document upload for Authorised Representative.
     */
    AR_REQUESTED_DOCUMENT_UPLOAD(true, true),

    /**
     * Request type of requested document upload for Account Holder.
     */
    AH_REQUESTED_DOCUMENT_UPLOAD(true, false),

    /**
     * Request type of updating Account Holder details.
     */
    ACCOUNT_HOLDER_UPDATE_DETAILS,
    ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS,
    ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_UPDATE,
    ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_DELETE,
    ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD,
    /**
     * Request type of email change.
     */
    REQUESTED_EMAIL_CHANGE(false, true),
    /**
     * Request to change the 2nd factor of authentication.
     */
    CHANGE_TOKEN(false, true),

    /**
     * Request due to loss of the 2nd factor of authentication (panic button).
     */
    LOST_TOKEN(false, true),
    /**
     * Request due to loss of both password and the 2nd factor of authentication.
     */
    LOST_PASSWORD_AND_TOKEN(false, true),

    /**
     * Request to update the installation operator info.
     */
    INSTALLATION_OPERATOR_UPDATE_REQUEST,

    /**
     * Request to update the aircraft operator info.
     */
    AIRCRAFT_OPERATOR_UPDATE_REQUEST,

    /**
     * Request to update the maritime operator info.
     */
    MARITIME_OPERATOR_UPDATE_REQUEST,

    /**
     * Request to transfer an account.
     */
    ACCOUNT_TRANSFER,

    /**
     * Request type of uploading the emissions table.
     */
    EMISSIONS_TABLE_UPLOAD_REQUEST,
    
    /**
     * Request to update the user details.
     */
    USER_DETAILS_UPDATE_REQUEST(false, true),

    /**
     * Request to deactivate a user from Registry.
     */
    USER_DEACTIVATION_REQUEST,

    /**
     * Request to close an account.
     */
    ACCOUNT_CLOSURE_REQUEST,

    /**
     * Request for Payment.
     */
    PAYMENT_REQUEST(true,true);

    /**
     * True if the request is considered a user task.
     */
    private final boolean isUserTask;

    /**
     * True if disclosed name should be consealed.
     */
    private final boolean isDisclosedName;

    RequestType() {
        this.isUserTask = false; // not really needed just to make clear what the default is
        this.isDisclosedName = false; // not really needed just to make clear what the default is
    }

    RequestType(boolean isUserTask, boolean isDisclosedName) {
        this.isUserTask = isUserTask;
        this.isDisclosedName = isDisclosedName;
    }

    public static List<RequestType> getUserTasks() {
        return Arrays.stream(values())
            .filter(t -> t.isUserTask)
            .collect(toList());
    }

    public static RequestType[] getTasksWithDisclosedName() {
        List<RequestType> isTaskWithDisclosedNameList = Arrays.stream(values())
            .filter(t -> t.isDisclosedName)
            .collect(toList());

        RequestType[] requestTypes = new RequestType[isTaskWithDisclosedNameList.size()];
        return isTaskWithDisclosedNameList.toArray(requestTypes);
    }
    
	public static List<RequestType> getTasksCausingUserSuspension() {
		return List.of(RequestType.LOST_TOKEN,
				RequestType.LOST_PASSWORD_AND_TOKEN, 
				RequestType.CHANGE_TOKEN);
	}
    
	public static List<RequestType> getARUpdateTasks() {
		return List.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST,
				RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST,
				RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST,
				RequestType.AUTHORIZED_REPRESENTATIVE_RESTORE_REQUEST,
				RequestType.AUTHORIZED_REPRESENTATIVE_SUSPEND_REQUEST,
				RequestType.AUTHORIZED_REPRESENTATIVE_UPDATE_ACCESS_RIGHTS_REQUEST);
	}

    public static List<RequestType> getTasksNotDisplayedToAR() {
        return List.of(ACCOUNT_TRANSFER,
                ACCOUNT_CLOSURE_REQUEST,
                ACCOUNT_OPENING_REQUEST,
                PRINT_ENROLMENT_LETTER_REQUEST,
                AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST,
                AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST,
                AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST,
                AUTHORIZED_REPRESENTATIVE_UPDATE_ACCESS_RIGHTS_REQUEST,
                AUTHORIZED_REPRESENTATIVE_SUSPEND_REQUEST,
                AUTHORIZED_REPRESENTATIVE_RESTORE_REQUEST,
                INSTALLATION_OPERATOR_UPDATE_REQUEST,
                AIRCRAFT_OPERATOR_UPDATE_REQUEST,
                MARITIME_OPERATOR_UPDATE_REQUEST,
                ACCOUNT_HOLDER_UPDATE_DETAILS,
                ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS,
                ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_UPDATE,
                ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_DELETE,
                ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD,
                TRANSACTION_RULES_UPDATE_REQUEST,
                USER_DETAILS_UPDATE_REQUEST,
                USER_DEACTIVATION_REQUEST,
                CHANGE_TOKEN,
                LOST_TOKEN,
                LOST_PASSWORD_AND_TOKEN);
    }
}
