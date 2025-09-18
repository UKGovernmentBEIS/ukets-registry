package gov.uk.ets.registry.api.account.shared;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.web.model.DetailsDTO;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.common.view.AddressDTO;
import gov.uk.ets.registry.api.common.view.EmailAddressDTO;
import gov.uk.ets.registry.api.common.view.PhoneNumberDTO;
import java.io.Serializable;
import java.util.Objects;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the account holder transfer object.
 */
@Builder
@AllArgsConstructor
@Getter
@Setter
public class AccountHolderDTO implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = 8566306592755689070L;
    /**
     * The id.
     */
    private Long id;
    /**
     * The e-mail address.
     */
    @Valid
    private EmailAddressDTO emailAddress;
    /**
     * The address.
     */
    @Valid
    private AddressDTO address;
    /**
     * The phone number.
     */
    @Valid
    private PhoneNumberDTO phoneNumber;
    /**
     * The details.
     */
    @Valid
    private DetailsDTO details;
    /**
     * The type.
     */
    @NotNull
    private AccountHolderType type;
    /**
     * The status.
     */
    private Status status;

    /**
     * Constructor.
     */
    public AccountHolderDTO() {
        // nothing to implement here
    }

    /**
     * Constructor.
     *
     * @param id   The id.
     * @param name The name.
     * @param type The type.
     */
    public AccountHolderDTO(Long id, String name, AccountHolderType type, String firstname, String lastname) {
        this.id = id;
        this.details = new DetailsDTO();
        this.details.setName(name);
        this.details.setFirstName(firstname);
        this.details.setLastName(lastname);
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AccountHolderDTO that = (AccountHolderDTO) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(emailAddress, that.emailAddress) &&
            Objects.equals(address, that.address) &&
            Objects.equals(phoneNumber, that.phoneNumber) &&
            Objects.equals(details, that.details) &&
            type == that.type &&
            status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, emailAddress, address, phoneNumber, details, type, status);
    }

    /**
     * Returns first name with last name concatenated if AH is INDIVIDUAL otherwise returns the name (of the ORG).
     **/
    @JsonIgnore
    public String actualName() {
        return this.getType() == AccountHolderType.INDIVIDUAL ?
            this.getDetails().getFirstName() + " " + this.getDetails().getLastName() : this.getDetails().getName();
    }
}
