package gov.uk.ets.registry.api.common.view;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import org.apache.commons.lang3.StringUtils;

/**
 * Represents the phone number transfer object.
 */
@Getter
@Setter
public class PhoneNumberDTO implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = -4915007308630254527L;

    /**
     * The first phone number.
     */
    @NotNull
    @Size(max = 256, message = "Phone number 1 must not exceed 256 characters.")
    private String phoneNumber1;

    /**
     * The second phone number.
     */
    @Size(max = 256, message = "Phone number 1 must not exceed 256 characters.")
    private String phoneNumber2;

    /**
     * The country code of the first phone number.
     */
    @NotNull
    @Size(max = 10, message = "Country code 1 must not exceed 10 characters.")
    private String countryCode1;

    /**
     * The country code of the second phone number.
     */
    @Size(max = 10, message = "Country code 2 must not exceed 10 characters.")
    private String countryCode2;

    /**
     * Checks whether the current object is empty.
     * @return false/true
     */
    public boolean isEmpty() {
        return StringUtils.isEmpty(phoneNumber1) &&
                StringUtils.isEmpty(phoneNumber2) &&
                StringUtils.isEmpty(countryCode1) &&
                StringUtils.isEmpty(countryCode2);
    }

}
