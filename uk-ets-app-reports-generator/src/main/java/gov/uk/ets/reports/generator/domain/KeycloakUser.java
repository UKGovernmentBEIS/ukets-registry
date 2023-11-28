package gov.uk.ets.reports.generator.domain;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KeycloakUser {

    private String urid;
    private String keycloakUserId;
    private List<UserRole> role;

    private LocalDateTime createdOn;
    private LocalDateTime registeredOn;
    private LocalDateTime lastLoginOn;
    private String username;
    private String email;
    private String status;
    private String firstName;
    private String lastName;
    private String alsoKnownAs;
    private String birthDate;
    private String country;
    private String countryOfBirth;
    private String buildingAndStreet;
    private String buildingAndStreetOptional;
    private String buildingAndStreetOptional2;
    private String postCode;
    private String townOrCity;
    private String stateOrProvince;
    private String workCountry;
    private String workCountryCode;
    private String workPhoneNumber;
    private String workBuildingAndStreet;
    private String workBuildingAndStreetOptional;
    private String workBuildingAndStreetOptional2;
    private String workPostCode;
    private String workTownOrCity;
    private String workStateOrProvince;

}
