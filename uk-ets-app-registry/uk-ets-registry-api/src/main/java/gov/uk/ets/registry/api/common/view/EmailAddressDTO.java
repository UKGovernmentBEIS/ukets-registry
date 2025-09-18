package gov.uk.ets.registry.api.common.view;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

/**
 * Represents the e-mail address transfer object.
 */
@Getter
@Setter
public class EmailAddressDTO implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = 3291564215292201510L;

    /**
     * The e-mail address.
     */
    @Email
    @Size(max = 256, message = "Email must not exceed 256 characters.")
    private String emailAddress;

    /**
     * The confirmation of the e-mail address.
     */
    @Email
    @Size(max = 256, message = "Email must not exceed 256 characters.")
    private String emailAddressConfirmation;

    /**
     * Checks whether the current object is empty.
     * @return false/true
     */
    public boolean isEmpty() {
        return StringUtils.isEmpty(emailAddress);
    }

}
