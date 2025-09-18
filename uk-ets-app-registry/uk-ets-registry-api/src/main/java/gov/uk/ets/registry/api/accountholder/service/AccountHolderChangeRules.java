package gov.uk.ets.registry.api.accountholder.service;

import gov.uk.ets.registry.api.task.domain.types.RequestType;

import java.util.List;

public final class AccountHolderChangeRules {

    private AccountHolderChangeRules() {}

    static List<RequestType> changeAccountHolderInvalidRequestTypes() {
        return List.of(
                RequestType.ADD_TRUSTED_ACCOUNT_REQUEST,
                RequestType.DELETE_TRUSTED_ACCOUNT_REQUEST,
                RequestType.ALLOCATION_TABLE_UPLOAD_REQUEST,
                RequestType.ALLOCATION_REQUEST,
                RequestType.TRANSACTION_RULES_UPDATE_REQUEST,
                RequestType.TRANSACTION_REQUEST,
                RequestType.ACCOUNT_OPENING_REQUEST,
                RequestType.ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS,
                RequestType.ACCOUNT_HOLDER_UPDATE_DETAILS,
                RequestType.AH_REQUESTED_DOCUMENT_UPLOAD
        );
    }

    static List<RequestType> changeAccountHolderInvalidPendingTasks() {
        return List.of(
                RequestType.ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS,
                RequestType.ACCOUNT_HOLDER_UPDATE_DETAILS,
                RequestType.AH_REQUESTED_DOCUMENT_UPLOAD,
                RequestType.INSTALLATION_OPERATOR_UPDATE_REQUEST,
                RequestType.AIRCRAFT_OPERATOR_UPDATE_REQUEST,
                RequestType.MARITIME_OPERATOR_UPDATE_REQUEST
        );
    }
}
