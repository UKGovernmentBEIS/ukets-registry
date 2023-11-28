package uk.gov.ets.registration.user.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.ToString;

/**
 * Validation error response.
 */
@ToString
public class ValidationErrorResponse implements Serializable {

    /**
     * The violations.
     */
    private List<Violation> violations = new ArrayList<>();

    /**
     * Adds a violation.
     * @param violation The violation
     */
    public void add(Violation violation) {
        violations.add(violation);
    }

    /**
     * Returns the violations.
     * @return the violations.
     */
    public List<Violation> getViolations() {
        return violations;
    }

    /**
     * Sets the violations.
     * @param violations the violations.
     */
    public void setViolations(List<Violation> violations) {
        this.violations = violations;
    }

}
