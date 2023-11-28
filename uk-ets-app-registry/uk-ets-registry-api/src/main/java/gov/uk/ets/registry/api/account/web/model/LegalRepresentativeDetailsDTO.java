package gov.uk.ets.registry.api.account.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Legal representative details transfer object.
 */
@Getter
@Setter
public class LegalRepresentativeDetailsDTO implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = 4174926685693067917L;

    /**
     * The first name.
     */
    private String firstName;

    /**
     * The last name.
     */
    private String lastName;

    /**
     * Also known as.
     */
    private String aka;

    /**
     * Year of birth.
     */
    private Integer yearOfBirth;

    /**
     * The date of birth info
     */
    @JsonProperty("birthDate")
    private DateInfo birthDateInfo;

}
