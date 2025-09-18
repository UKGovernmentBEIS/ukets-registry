package gov.uk.ets.registry.api.transaction.domain;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.task.domain.TaskTransaction;

import java.io.Serializable;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * Represents a transaction.
 */
@Entity
@Table(name = "transaction")
@Getter
@Setter
public class SearchableTransaction extends BaseTransactionEntity implements Serializable {
    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -1911557347082320662L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transferring_account_full_identifier",
        referencedColumnName = "full_identifier",
        insertable = false,
        updatable = false,
        foreignKey = @jakarta.persistence.ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    private Account transferringUkRegistryAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acquiring_account_full_identifier",
        referencedColumnName = "full_identifier",
        insertable = false, updatable = false,
        foreignKey = @jakarta.persistence.ForeignKey(value = ConstraintMode.NO_CONSTRAINT)
    )
    @NotFound(action = NotFoundAction.IGNORE)
    private Account acquiringUkRegistryAccount;

    @OneToOne
    @JoinColumn(name = "identifier",
        referencedColumnName = "transaction_identifier",
        insertable = false, updatable = false,
        foreignKey = @jakarta.persistence.ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    private TaskTransaction taskTransaction;
    
    @OneToOne
    @JoinColumn(name = "identifier",
        referencedColumnName = "transaction_identifier",
        insertable = false, 
        updatable = false,
        foreignKey = @jakarta.persistence.ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    private TransactionAccountBalance accountsBalances;
}
