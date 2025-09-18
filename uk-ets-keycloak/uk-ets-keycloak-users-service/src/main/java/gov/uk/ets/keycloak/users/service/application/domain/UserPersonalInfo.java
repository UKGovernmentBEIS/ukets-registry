package gov.uk.ets.keycloak.users.service.application.domain;

import com.querydsl.core.annotations.QueryProjection;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class UserPersonalInfo implements Serializable {
    private String urid;
    private String firstName;
    private String lastName;
    private String alsoKnownAs;
    private String workBuildingAndStreet;
    private String workBuildingAndStreetOptional;
    private String workBuildingAndStreetOptional2;
    private String workPostCode;
    private String workTownOrCity;
    private String workStateOrProvince;
    private String workCountry;
    private String workCountryCode;
    private String workPhoneNumber;
    private String workMobileCountryCode;
    private String workMobilePhoneNumber;
    private String workAlternativeCountryCode;
    private String workAlternativePhoneNumber;
    private String noMobilePhoneNumberReason;
    private String recoveryCountryCode;
    private String recoveryPhoneNumber;
    private String recoveryEmailAddress;
    private String hideRecoveryMethodsNotification;
    private String email;

    @QueryProjection
    public UserPersonalInfo(String urid, String firstName, String lastName, 
    		String alsoKnownAs, String workBuildingAndStreet, String workBuildingAndStreetOptional,
    		String workBuildingAndStreetOptional2, String workPostCode, String workTownOrCity, 
    		String workStateOrProvince, String workCountry, String workCountryCode, String workPhoneNumber,
            String workMobileCountryCode, String workMobilePhoneNumber, String workAlternativeCountryCode,
            String workAlternativePhoneNumber, String noMobilePhoneNumberReason, String recoveryCountryCode,
            String recoveryPhoneNumber, String recoveryEmailAddress, String hideRecoveryMethodsNotification, String email) {
        this.urid = urid; 
        this.firstName = firstName;
        this.lastName = lastName;
        this.alsoKnownAs = alsoKnownAs;
        this.workBuildingAndStreet = workBuildingAndStreet;
        this.workBuildingAndStreetOptional = workBuildingAndStreetOptional;
        this.workBuildingAndStreetOptional2 = workBuildingAndStreetOptional2;
        this.workPostCode = workPostCode;
        this.workTownOrCity = workTownOrCity;
        this.workStateOrProvince = workStateOrProvince;
        this.workCountry = workCountry;
        this.workCountryCode = workCountryCode;
        this.workPhoneNumber = workPhoneNumber;
        this.workMobileCountryCode = workMobileCountryCode;
        this.workMobilePhoneNumber = workMobilePhoneNumber;
        this.workAlternativeCountryCode = workAlternativeCountryCode;
        this.workAlternativePhoneNumber = workAlternativePhoneNumber;
        this.noMobilePhoneNumberReason = noMobilePhoneNumberReason;
        this.recoveryCountryCode = recoveryCountryCode;
        this.recoveryPhoneNumber = recoveryPhoneNumber;
        this.recoveryEmailAddress = recoveryEmailAddress;
        this.hideRecoveryMethodsNotification = hideRecoveryMethodsNotification;
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserPersonalInfo{" +
            "urid='" + urid + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", alsoKnownAs='" + alsoKnownAs + '\'' +
            ", workBuildingAndStreet='" + workBuildingAndStreet + '\'' +
            ", workBuildingAndStreetOptional='" + workBuildingAndStreetOptional + '\'' +
            ", workBuildingAndStreetOptional2='" + workBuildingAndStreetOptional2 + '\'' +
            ", workPostCode='" + workPostCode + '\'' +
            ", workTownOrCity='" + workTownOrCity + '\'' +
            ", workStateOrProvince='" + workStateOrProvince + '\'' +
            ", workCountry='" + workCountry + '\'' +
            ", workCountryCode='" + workCountryCode + '\'' +
            ", workPhoneNumber='" + workPhoneNumber + '\'' +
            ", workMobileCountryCode='" + workMobileCountryCode + '\'' +
            ", workMobilePhoneNumber='" + workMobilePhoneNumber + '\'' +
            ", workAlternativeCountryCode='" + workAlternativeCountryCode + '\'' +
            ", workAlternativePhoneNumber='" + workAlternativePhoneNumber + '\'' +
            ", noMobilePhoneNumberReason='" + noMobilePhoneNumberReason + '\'' +
            ", recoveryCountryCode='" + recoveryCountryCode + '\'' +
            ", recoveryPhoneNumber='" + recoveryPhoneNumber + '\'' +
            ", recoveryEmailAddress='" + recoveryEmailAddress + '\'' +
            ", hideRecoveryMethodsNotification='" + hideRecoveryMethodsNotification + '\'' +
            ", email='" + email + '\'' +
            '}';
    }
}
