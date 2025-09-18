package gov.uk.ets.registry.api.transaction.domain;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Represents a transaction.
 */
@Entity
@Getter
@Setter
@Table(name="transaction")
public class Transaction extends BaseTransactionEntity implements Serializable {
    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = -1911557347082320662L;
}
