package uk.gov.ets.transaction.log.domain;

import java.io.Serializable;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;



/**
 * Represents a transaction.
 */
@Entity
@Getter
@Setter
public class Transaction extends BaseTransactionEntity implements Serializable {
    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = -1911557347082320662L;
}
