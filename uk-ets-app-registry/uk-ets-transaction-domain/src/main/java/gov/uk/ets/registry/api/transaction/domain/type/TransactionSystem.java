package gov.uk.ets.registry.api.transaction.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents the various systems that participate in a transaction.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum TransactionSystem {

    /**
     * UK Registry.
     */
    REGISTRY,

    /**
     * UK Transaction Log.
     */
    UKTL(TransactionProtocol.UKETS),

    /**
     * International Transaction Log.
     */
    ITL(TransactionProtocol.KYOTO);

    private TransactionProtocol protocol;

}
