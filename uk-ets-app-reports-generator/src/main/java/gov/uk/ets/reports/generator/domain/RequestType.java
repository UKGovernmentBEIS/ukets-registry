package gov.uk.ets.reports.generator.domain;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum RequestType {

    ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS("Update the Primary Contact details"),
    ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD("Add the alternative Primary Contact"),
    ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_DELETE("Remove the alternative Primary Contact"),
    ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_UPDATE("Update the alternative Primary Contact"),
    ACCOUNT_HOLDER_UPDATE_DETAILS("Update the account holder details"),
    ACCOUNT_OPENING_REQUEST("Open Account"),
    ACCOUNT_OPENING_INSTALLATION_TRANSFER_REQUEST("Open Account with installation transfer"),
    ADD_TRUSTED_ACCOUNT_REQUEST("Add trusted account"),
    AH_REQUESTED_DOCUMENT_UPLOAD("Submit documents for account holder"),
    ALLOCATION_REQUEST("Allocate allowances proposal"),
    ALLOCATION_TABLE_UPLOAD_REQUEST("Upload allocation table"),
    AR_REQUESTED_DOCUMENT_UPLOAD("Submit documents for user"),
    AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST("Add authorised representative"),
    AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST("Remove authorised representative"),
    AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST("Replace authorised representative"),
    AUTHORIZED_REPRESENTATIVE_RESTORE_REQUEST("Restore authorised representative"),
    AUTHORIZED_REPRESENTATIVE_SUSPEND_REQUEST("Suspend authorised representative"),
    AUTHORIZED_REPRESENTATIVE_UPDATE_ACCESS_RIGHTS_REQUEST("Change authorised representatives permissions"),
    CHANGE_TOKEN("Approve two factor authentication change request"),
    DELETE_TRUSTED_ACCOUNT_REQUEST("Remove trusted account"),
    EMISSIONS_TABLE_UPLOAD_REQUEST("Approve emission table"),
    LOST_TOKEN("Request two factor authentication emergency access"),
    PRINT_ENROLMENT_LETTER_REQUEST("Print letter with registry activation code"),
    REQUESTED_EMAIL_CHANGE("Approve email address change request"),
    TRANSACTION_REQUEST("Transaction Proposal"),
    TRANSACTION_RULES_UPDATE_REQUEST("Update transaction rules"),
    LOST_PASSWORD_AND_TOKEN("Request password and two factor authentication emergency access"),
    INSTALLATION_OPERATOR_UPDATE_REQUEST("Update account installation details"),
    AIRCRAFT_OPERATOR_UPDATE_REQUEST("Update account aircraft operator details"),
    ACCOUNT_TRANSFER("Account transfer"),
    USER_DETAILS_UPDATE_REQUEST("Update user details"),
    USER_DEACTIVATION_REQUEST("Deactivate user"),
    ACCOUNT_CLOSURE_REQUEST("Close account");
    
    private final String label;

    RequestType(String label) {
        this.label = label;
    }

    /**
     * Parses the provided string to a Request Type and returns its label.
     *
     * @param input The input string.
     * @return the Request Type label, or null if no matching input is found.
     */
    public static String getLabel(String input) {
        return Arrays.stream(values())
            .filter(requestType -> requestType.name().equals(input))
            .findFirst()
            .map(RequestType::getLabel)
            .orElse(null);
    }
}
