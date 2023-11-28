package gov.uk.ets.registry.api.file.upload.bulkar.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BulkArError {

    INVALID_TABLE_EMPTY("File is empty"),

    INVALID_COLUMN_MISSING(" column missing"),

    INVALID_COLUMNS("Invalid column layout"),

    EMPTY_ACCOUNT_ID("The Account number must not be empty"),

    EMPTY_USER_ID("The User ID must not be empty"),

    EMPTY_PERMISSIONS("Permissions must not be empty"),

    INVALID_PERMISSIONS("Invalid Permissions"),

    ACCOUNT_ID_NOT_IN_REGISTRY("The Account number does not exist in the Registry"),

    ACCOUNT_STATUS_PROPOSED("The Account number belongs to an account with Proposed status"),

    ACCOUNT_STATUS_CLOSED("The Account number belongs to an account with Closed status"),

    ACCOUNT_STATUS_CLOSURE_PENDING("The Account number belongs to an account with a pending closure request"),

    ACCOUNT_STATUS_REJECTED("The Account number belongs to an account with Rejected status"),

    ACCOUNT_STATUS_TRANSFER_PENDING("The Account number belongs to an account with Transfer Pending status"),

    ACCOUNT_ID_BELONGS_TO_GOV_ACC("The Account number belongs to a government account"),

    USER_ID_SUSPENDED("User ID is suspended"),
    
    USER_ID_DEACTIVATED("User ID is deactivated"),

    USER_ID_DEACTIVATION_PENDING("User ID is pending for deactivation"),

    USER_ID_NOT_IN_REGISTRY("The User ID does not exist in the Registry"),

    DUPLICATE_ID_ENTRIES("Invalid Account number,User ID assignment - duplicate entries"),

    AR_ALREADY_ADDED("The AR is already added"),

    PENDING_ADD_AR_APPROVAL("There is already a pending task to assign User ID to Account ID");
    
    private final String message;
}
