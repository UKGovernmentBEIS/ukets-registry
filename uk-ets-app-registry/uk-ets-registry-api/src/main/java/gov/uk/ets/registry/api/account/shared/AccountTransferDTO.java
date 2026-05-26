package gov.uk.ets.registry.api.account.shared;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the account transfer object.
 */
@Builder
@AllArgsConstructor
@Getter
@Setter
public class AccountTransferDTO implements Serializable {

    private static final long serialVersionUID = -9148779239072140115L;

    /**
     * The account holder.
     */
    private AccountHolderDTO accountHolder;

    /**
     * The emitter id.
     */
    private String emitterId;
}
