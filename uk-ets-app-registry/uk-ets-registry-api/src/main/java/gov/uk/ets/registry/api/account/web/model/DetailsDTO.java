package gov.uk.ets.registry.api.account.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Represents the details transfer object.
 */
@Getter
@Setter
public class DetailsDTO implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = -5071186330888835642L;

    /**
     * The birth country.
     */
    @Size(max = 2, message = "Country must not exceed 2 characters.")
    @JsonProperty("countryOfBirth")
    private String birthCountry;

    /**
     * The name.
     */
    @Size(max = 256, message = "Name must not exceed 256 characters.")
    private String name;

    @Size(max = 256, message = "Name must not exceed 256 characters.")
    private String firstName;

    @Size(max = 40, message = "Name must not exceed 256 characters.")
    private String lastName;

    /**
     * The registration number.
     */
    @Size(max = 256, message = "Registration must not exceed 256 characters.")
    private String registrationNumber;

    /**
     * The enum {@link gov.uk.ets.registry.api.account.domain.types.RegistrationNumberType} type
     * in order to be used in the radio button selection.
     */
    private Integer regNumTypeRadio;

    /**
     * The justification why no Registration is provided.
     */
    @Size(max = 1024, message = "Justification must not exceed 1024 characters.")
    private String noRegistrationNumJustification;

    /**
     * The birth year.
     */
    @Digits(integer = 4, fraction = 0, message = "Not a valid year.")
    @JsonProperty("yearOfBirth")
    private Integer birthYear;

    /**
     * The birth date info.
     */
    @Valid
    @JsonProperty("birthDate")
    private DateInfo birthDateInfo;
}
