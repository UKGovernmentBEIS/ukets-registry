package gov.uk.ets.registry.api.ar.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * The user work contact details data transfer object
 */
@Getter
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkContactDetailsDTO {
    /**
     * The main work address
     */
    @JsonProperty("line1")
    private String workBuildingAndStreet;
    /**
     * The second work address
     */
    @JsonProperty("line2")
    private String workBuildingAndStreetOptional;
    /**
     * The third work address
     */
    @JsonProperty("line3")
    private String workBuildingAndStreetOptional2;
    /**
     * The work postal code
     */
    @JsonProperty("postCode")
    private String workPostCode;
    /**
     * The work town or city
     */
    @JsonProperty("city")
    private String workTownOrCity;
    /**
     * The work state or province
     */
    @JsonProperty("stateOrProvince")
    private String workStateOrProvince;
    /**
     * The work country
     */
    @JsonProperty("country")
    private String workCountry;
    /**
     * The work country code
     */
    @JsonProperty("countryCode1")
    private String workCountryCode;
    /**
     * The work phone number
     */
    @JsonProperty("phoneNumber1")
    private String workPhoneNumber;
    /**
     * The work email address
     */
    @JsonProperty("emailAddress")
    private String workEmailAddress;
}
