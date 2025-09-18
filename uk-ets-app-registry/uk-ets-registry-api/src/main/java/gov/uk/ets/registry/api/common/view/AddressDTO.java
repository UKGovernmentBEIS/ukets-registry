package gov.uk.ets.registry.api.common.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import org.apache.commons.lang3.StringUtils;

/**
 * Represents the address transfer object.
 */
@Getter
@Setter
public class AddressDTO implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = 7345390117573117820L;

    /**
     * The country.
     */
    @Size(max = 256, message = "Country must not exceed 256 characters.")
    private String country;

    /**
     * The city.
     */
    @Size(max = 256, message = "City must not exceed 256 characters.")
    @JsonProperty("townOrCity")
    private String city;

    /**
     * The first address line.
     */
    @Size(max = 256, message = "Address line 1 must not exceed 256 characters.")
    @JsonProperty("buildingAndStreet")
    private String line1;

    /**
     * The second address line.
     */
    @Size(max = 256, message = "Address line 2 must not exceed 256 characters.")
    @JsonProperty("buildingAndStreet2")
    private String line2;

    /**
     * The third address line.
     */
    @Size(max = 256, message = "Address line 3 must not exceed 256 characters.")
    @JsonProperty("buildingAndStreet3")
    private String line3;

    /**
     * The Postal Code or ZIP.
     */
    @Size(max = 64, message = "Postal Code or ZIP must not exceed 64 characters.")
    private String postCode;
    
    /**
     * The State or Province.
     */
    @Size(max = 256, message = "State or Province must not exceed 256 characters.")
    @JsonProperty("stateOrProvince")
    private String stateOrProvince;

    /**
     * Checks whether the current object is empty.
     * @return false/true
     */
    public boolean isEmpty() {
        return StringUtils.isEmpty(country) &&
                StringUtils.isEmpty(city) &&
                StringUtils.isEmpty(line1) &&
                StringUtils.isEmpty(line2) &&
                StringUtils.isEmpty(line3) &&
                StringUtils.isEmpty(postCode);
    }

}
