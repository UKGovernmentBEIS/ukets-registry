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
     *
     * @deprecated has been replaced by mobile/alternative country code
     */
    @Deprecated(forRemoval = true)
    private String workCountryCode;
    /**
     * The work phone number
     *
     * @deprecated has been replaced by mobile/alternative phone number
     */
    @Deprecated(forRemoval = true)
    private String workPhoneNumber;
    /**
     * The email address
     */
    private String email;
    /**
     * The work mobile country code
     */
    private String workMobileCountryCode;
    /**
     * The work mobile phone number
     */
    private String workMobilePhoneNumber;
    /**
     * The work alternative country code
     */
    private String workAlternativeCountryCode;
    /**
     * The work alternative phone number
     */
    private String workAlternativePhoneNumber;
    /**
     * The reason for not providing a mobile phone number
     */
    private String noMobilePhoneNumberReason;
}
