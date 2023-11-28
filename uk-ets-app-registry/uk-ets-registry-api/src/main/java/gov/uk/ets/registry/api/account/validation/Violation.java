package gov.uk.ets.registry.api.account.validation;

import java.io.Serializable;
import lombok.ToString;

/**
 * A violation.
 */
@ToString
public class Violation implements Serializable {

    /**
     * The field name.
     */
    private  String fieldName;

    /**
     * The
     */
    private  String message;

    /**
     * Default constructor.
     */
    public Violation() {
        // nothing to implement here
    }

    /**
     * Constructor.
     * @param fieldName The field name.
     * @param message The message.
     */
    public Violation(String fieldName, String message) {
        this.fieldName = fieldName;
        this.message = message;
    }

    /**
     * Returns the field name.
     * @return the field name.
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Sets the field name.
     * @param fieldName the field name.
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Returns the message.
     * @return the message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message.
     * @param message the message.
     */
    public void setMessage(String message) {
        this.message = message;
    }

}

