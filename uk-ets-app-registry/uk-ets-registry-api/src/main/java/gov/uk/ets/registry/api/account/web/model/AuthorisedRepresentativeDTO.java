package gov.uk.ets.registry.api.account.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.user.UserDTO;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * An authorised representative transfer object.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorisedRepresentativeDTO implements Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 705213473075709296L;

    /**
     * The access right.
     */
    private AccountAccessRight right;

    /**
     * The number.
     */
    private Long number;

    /**
     * The URID.
     */
    private String urid;

    /**
     * The first name.
     */
    private String firstName;

    /**
     * The last name.
     */
    private String lastName;

    /**
     * The user.
     */
    private UserDTO user;

    /**
     * The contact details.
     */
    private ContactDTO contact;

    /**
     * The state of the account access.
     */
    private AccountAccessState state;

    /**
     * The account name where the user is AR.
     */
    private String accountName;

    /**
     * The account identifier where the user is AR.
     */
    private Long accountIdentifier;

    /**
     * The account holder name.
     */
    private String accountHolderName;
    /**
     * The account Full Identifier.
     */
    private String accountFullIdentifier;
}
