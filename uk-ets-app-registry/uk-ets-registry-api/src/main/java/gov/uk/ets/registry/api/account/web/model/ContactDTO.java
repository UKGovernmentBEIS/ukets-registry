package gov.uk.ets.registry.api.account.web.model;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactDTO implements Serializable {

    private String city;
    private String country;
    private String emailAddress;
    private String line1;
    private String line2;
    private String line3;
    // Required for compatibility issues
    private String countryCode1;
    // Required for compatibility issues
    private String phoneNumber1;
    // Required for compatibility issues
    private String countryCode2;
    // Required for compatibility issues
    private String phoneNumber2;
    private String postCode;
    private String positionInCompany;
    private String stateOrProvince;
    private String mobileCountryCode;
    private String mobilePhoneNumber;
    private String alternativeCountryCode;
    private String alternativePhoneNumber;
    private String noMobilePhoneNumberReason;
}
