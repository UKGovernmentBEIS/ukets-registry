package uk.gov.ets.transaction.log.checks.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import uk.gov.ets.transaction.log.messaging.types.TransactionNotification;


/**
 * Encapsulates the business context, i.e. a place to store objects and add errors.
 */
@Getter
@Setter
@NoArgsConstructor
public class BusinessCheckContext implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = 3771377969362754795L;

    /**
     * The transaction.
     */
    private TransactionNotification transaction;

    /**
     * The errors.
     */
    private List<BusinessCheckError> errors;

    /**
     * Adds a business error.
     * @param code The code.
     * @param message The message.
     */
    public void addError(Integer code, String message) {
        if (code == null || StringUtils.isEmpty(message)) {
            throw new IllegalArgumentException("Empty error code or message provided");
        }
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errors.add(new BusinessCheckError(code, message));
    }

    /**
     * Checks whether the business context contains any error.
     * @return false/true
     */
    public boolean hasError() {
        return !CollectionUtils.isEmpty(errors);
    }

    /**
     * Resets this business check context.
     */
    public void reset() {
        errors = null;
    }
}
