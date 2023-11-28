package gov.uk.ets.registry.api.authz;

/**
 * Available scopes as defined in Keycloak Authorization Scopes tab of uk-ets-registry-api client.
 *
 * @author P35036
 * @since v0.3.0
 */
public enum Scope {
    SCOPE_TASK_ACCOUNT_OPEN_COMPLETE("urn:uk-ets-registry-api:task:account-open:complete"),
    SCOPE_TASK_ACCOUNT_OPEN_READ("urn:uk-ets-registry-api:task:account-open:read"),
    SCOPE_TASK_ACCOUNT_OPEN_WRITE("urn:uk-ets-registry-api:task:account-open:write"),
    SCOPE_ACTION_ANY_ADMIN("urn:uk-ets-registry-api:actionForAnyAdmin"),
    SCOPE_ACTION_NON_ADMIN("urn:uk-ets-registry-api:actionForNonAdmin"),
    SCOPE_ACTION_ENROLLED_NON_ADMIN("urn:uk-ets-registry-api:actionForEnrolledNonAdmin"),
    SCOPE_ACTION_TRANSACTION_PROPOSAL_ISSUANCE_READ("urn:uk-ets-registry-api:task:transaction-proposal:issuance:read"),
    SCOPE_ACTION_TRANSACTION_PROPOSAL_ISSUANCE_COMPLETE(
        "urn:uk-ets-registry-api:task:transaction-proposal:issuance:complete"),
    SCOPE_ACTION_TRANSACTION_PROPOSAL_GENERIC_READ("urn:uk-ets-registry-api:task:transaction-proposal:generic:read"),
    SCOPE_ACTION_TRANSACTION_PROPOSAL_GENERIC_SRA_COMPLETE(
        "urn:uk-ets-registry-api:task:transaction-proposal:generic:sra:complete"),
    SCOPE_ACTION_TRANSACTION_PROPOSAL_GENERIC_AR_COMPLETE(
        "urn:uk-ets-registry-api:task:transaction-proposal:generic:ar:complete"),
    SCOPE_TASK_TRUSTED_ACCOUNTS_LIST_READ("urn:uk-ets-registry-api:task:trusted-accounts-list:read"),
    SCOPE_TASK_TRUSTED_ACCOUNTS_LIST_WRITE("urn:uk-ets-registry-api:task:trusted-accounts-list:write");
    private final String scopeName;

    Scope(String scopeName) {
        this.scopeName = scopeName;
    }

    public String getScopeName() {
        return scopeName;
    }

}
