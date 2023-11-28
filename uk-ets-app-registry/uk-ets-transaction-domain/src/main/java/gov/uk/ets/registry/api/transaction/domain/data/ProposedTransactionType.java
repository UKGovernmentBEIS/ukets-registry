package gov.uk.ets.registry.api.transaction.domain.data;

import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Encapsulates the required information for proposing a transaction.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = {"description"})
public class ProposedTransactionType implements Serializable, Comparable<ProposedTransactionType> {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = 8134803943837961391L;

    /**
     * The transaction type.
     */
    private TransactionType type;

    /**
     * The description.
     */
    private String description;

    /**
     * The transaction type hint.
     */
    private String hint;

    /**
     * The category.
     */
    private String category;

    /**
     * Whether an ITL notification is supported.
     */
    private Boolean supportsNotification;

    /**
     * Whether to display the transaction type.
     */
    private Boolean enabled;

    /**
     * Whether to skip choosing the acquiring account.
     */
    private boolean skipAccountStep;

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(ProposedTransactionType otherProposedTransactionType) {
        return type.getOrder() - otherProposedTransactionType.getType().getOrder();
    }
}
