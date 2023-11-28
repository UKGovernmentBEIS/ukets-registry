package gov.uk.ets.registry.api.account.web.model;

import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Data transfer object for holding the primary and alternative contact information
 */
@Getter
@Setter
public class AccountHolderContactInfoDTO implements Serializable {

    private static final long serialVersionUID = -7707107492359907494L;
    /**
     * The primary account holder contact
     */
    @Valid
    @NotNull
    private AccountHolderRepresentativeDTO primaryContact;

    /**
     * The alternative account holder contact
     */
    @Valid
    private AccountHolderRepresentativeDTO alternativeContact;

}
