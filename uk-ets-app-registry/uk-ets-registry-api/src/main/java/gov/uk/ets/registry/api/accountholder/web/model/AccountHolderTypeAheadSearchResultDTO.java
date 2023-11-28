package gov.uk.ets.registry.api.accountholder.web.model;

import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the account holder type ahead search object.
 */
@Builder
@AllArgsConstructor
@Getter
@Setter
public class AccountHolderTypeAheadSearchResultDTO implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = 8566306592755689070L;

    private Long identifier;

    private String name;

    private String firstName;

    private String lastName;
    
    private AccountHolderType type;


}
