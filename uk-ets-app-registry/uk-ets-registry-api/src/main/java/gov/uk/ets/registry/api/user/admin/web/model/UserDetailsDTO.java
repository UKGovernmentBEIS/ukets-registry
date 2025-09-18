package gov.uk.ets.registry.api.user.admin.web.model;

import gov.uk.ets.registry.api.common.view.DateDTO;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * Use @UserDetailsUpdateField annotation to the editable fields that can be changed on the "Update User Detail" form.
 */
@Getter
@Setter
public class UserDetailsDTO implements Serializable {
    
    private static final long serialVersionUID = -5048415006611289767L;
        
    private String emailAddress;
    private String emailAddressConfirmation;
    private String userId;
    private String username;
    private String password;
    @UserDetailsUpdateField(label="First and middle names")
    private String firstName;
    @UserDetailsUpdateField(label="Last Name")
    private String lastName;
    @UserDetailsUpdateField(label="Known As")
    private String alsoKnownAs;
    private String buildingAndStreet;
    private String buildingAndStreetOptional;
    private String buildingAndStreetOptional2;
    private String postCode;
    private String townOrCity;
    private String stateOrProvince;
    private String country;
    @UserDetailsUpdateField(label="Date of birth")
    private DateDTO birthDate;
    @UserDetailsUpdateField(label="Country of birth")
    private String countryOfBirth;
    @Deprecated(forRemoval = true)
    private String workCountryCode;
    @Deprecated(forRemoval = true)
    @UserDetailsUpdateField(label="(Work)Phone number")
    private String workPhoneNumber;
    @UserDetailsUpdateField(label="(Work)Address 1", isMinor=true)
    private String workBuildingAndStreet;
    @UserDetailsUpdateField(label="(Work)Address 2", isMinor=true)
    private String workBuildingAndStreetOptional;
    @UserDetailsUpdateField(label="(Work)Address 3", isMinor=true)
    private String workBuildingAndStreetOptional2;
    @UserDetailsUpdateField(label="(Work)Town or City", isMinor=true)
    private String workTownOrCity;
    @UserDetailsUpdateField(label="(Work)State or Province", isMinor=true)
    private String workStateOrProvince;
    @UserDetailsUpdateField(label="(Work)Postal Code or ZIP", isMinor=true)
    private String workPostCode;
    @UserDetailsUpdateField(label="(Work)Country", isMinor=true)
    private String workCountry;
    private String urid;
    private String state;
    private UserStatus status;
    @UserDetailsUpdateField(label="Memorable phrase")
    private String memorablePhrase;
    private String workMobileCountryCode;
    @UserDetailsUpdateField(label="(Work)Mobile Phone number")
    private String workMobilePhoneNumber;
    private String workAlternativeCountryCode;
    @UserDetailsUpdateField(label="(Work)Alternative Phone number")
    private String workAlternativePhoneNumber;
    @UserDetailsUpdateField(label="(Work)Reason for not providing mobile phone")
    private String noMobilePhoneNumberReason;
    private String recoveryCountryCode;
    @UserDetailsUpdateField(label="Recovery Mobile Phone number")
    private String recoveryPhoneNumber;
    private String recoveryEmailAddress;
}
