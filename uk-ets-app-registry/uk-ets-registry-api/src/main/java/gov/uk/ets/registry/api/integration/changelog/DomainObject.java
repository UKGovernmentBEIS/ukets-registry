package gov.uk.ets.registry.api.integration.changelog;

import lombok.Getter;

@Getter
public enum DomainObject {

    ACCOUNT_DETAILS("AccountDetails", "Account"),
    ACCOUNT_HOLDER("AccountHolder", "AccountHolder"),
    OPERATOR("Operator", "CompliantEntity"),
    EMISSIONS("Emissions", "EmissionsEntry"),
    UPDATE_ACCOUNT_CONTACT_LIST("UpdateAccountContactList", "MetsAccountContact"),
    EXEMPTION("Exemption", "ExcludeEmissionsEntry");

    private final String description;
    private final String entity;

    DomainObject(String description, String entity) {
        this.description = description;
        this.entity = entity;
    }
}
