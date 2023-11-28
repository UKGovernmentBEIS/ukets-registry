package gov.uk.ets.registry.api.user.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The Personal User info entity, persisted in keycloak
 */
@Getter
@Setter
@EqualsAndHashCode(of = {"urid"})
@ToString
public class UserWorkContact {

    /**
     * The unique business identifier
     */
    private String urid;
    /**
     * The user first name
     */
    private String firstName;
    /**
     * The user last name
     */
    private String lastName;
    /**
     * The user known as name
     */
    private String alsoKnownAs;
    /**
     * The main work address
     */
    private String workBuildingAndStreet;
    /**
     * The second work address
     */
    private String workBuildingAndStreetOptional;
    /**
     * The third work address
     */
    private String workBuildingAndStreetOptional2;
    /**
     * The work postal code
     */
    private String workPostCode;
    /**
     * The work town or city
     */
    private String workTownOrCity;
    /**
     * The work state or province
     */
    private String workStateOrProvince;
    /**
     * The work country
     */
    private String workCountry;
    /**
     * The work country code
     */
    private String workCountryCode;
    /**
     * The work phone number
     */
    private String workPhoneNumber;
    /**
     * The email address
     */
    private String email;
}
