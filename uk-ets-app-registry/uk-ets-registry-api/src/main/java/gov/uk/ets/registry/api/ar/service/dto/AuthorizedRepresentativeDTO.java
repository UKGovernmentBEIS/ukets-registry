package gov.uk.ets.registry.api.ar.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.user.UserDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Data transfer object that describes an Authorised Representative
 */
@Getter
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorizedRepresentativeDTO {
    /**
     * The {@link AccountAccessRight}
     */
    private AccountAccessRight right;

    /**
     * The {@link AccountAccessState}
     */
    private AccountAccessState state;

    /**
     * The user unique buisiness identifier of the AR
     */
    private String urid;

    /**
     * The first name
     */
    private String firstName;

    /**
     * The last name
     */
    private String lastName;

    /**
     * The user details
     */
    private UserDTO user;

    /**
     * The {@link WorkContactDetailsDTO} work contact details info of the AR
     */
    private WorkContactDetailsDTO contact;
}
