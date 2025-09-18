package gov.uk.ets.registry.api.integration.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AccountHolderMessage {

    private String accountHolderType;
    private String name;
    private String addressLine1;
    private String addressLine2;
    private String townOrCity;
    private String stateOrProvince;
    /*
     * Cannot accept values that are not part of the Registry’s Countries list.
     */
    private String country;
    /*
     * if Country is UK, Optional otherwise.
     */
    private String postalCode;
    private Boolean crnNotExist;
    private String companyRegistrationNumber;
    /*
     * This is the noRegNumjustification.
     * Mandatory if the companyRegistrationNumber is null.
     */
    private String crnJustification;
}
