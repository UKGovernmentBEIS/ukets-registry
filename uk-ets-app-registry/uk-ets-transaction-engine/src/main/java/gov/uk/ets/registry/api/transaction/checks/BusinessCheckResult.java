package gov.uk.ets.registry.api.transaction.checks;

import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

/**
 * Represents the result of a series of business checks.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class BusinessCheckResult implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = 1879797689082397933L;

    /**
     * The unique business request identifier (e.g. 10001).
     */
    private Long requestIdentifier;

    /**
     * The unique business transaction identifier (e.g. GB12345).
     */
    private String transactionIdentifier;

    /**
     * Whether an approval is required.
     */
    private Boolean approvalRequired;

    /**
     * The formatted execution day.
     */
    private String executionDate;

    /**
     * The formatted execution time.
     */
    private String executionTime;

    /**
     * The transaction type.
     */
    private TransactionType transactionType;

    /**
     * The transaction description.
     */
    private String transactionTypeDescription;

    /**
     * The errors identified during the checking.
     */
    private List<BusinessCheckError> errors;

    /**
     * Constructor.
     * @param errors The business check errors.
     */
    public BusinessCheckResult(List<BusinessCheckError> errors) {
        this.errors = errors;
    }

    /**
     * Returns whether the business check is successful.
     * @return false/true.
     */
    public boolean success() {
        return CollectionUtils.isEmpty(errors);
    }

}
