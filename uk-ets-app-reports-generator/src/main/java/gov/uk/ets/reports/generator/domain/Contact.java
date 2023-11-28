package gov.uk.ets.reports.generator.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Contact {
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String townOrCity;
    private String stateOrProvince;
    private String country;
    private String postCode;
    private String phoneNumber1;
    private String phoneNumber2;
    private String email;
    private String contactName;
    private String sopCustomerId;
}
