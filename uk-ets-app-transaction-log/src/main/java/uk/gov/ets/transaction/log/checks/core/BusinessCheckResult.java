package uk.gov.ets.transaction.log.checks.core;

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
     * Serialization version.
     */
    private static final long serialVersionUID = -9173115922941741674L;

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
