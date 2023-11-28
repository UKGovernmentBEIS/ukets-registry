package gov.uk.ets.registry.api.transaction.exception;

import org.apache.logging.log4j.LogManager;

/**
 * Represents an exception during transaction execution.
 */
public class TransactionExecutionException extends RuntimeException {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = 5171386059598540736L;

    /**
     * Constructor.
     * @param invokingClass The invoking class.
     * @param message The message.
     * @param error The error.
     */
    public TransactionExecutionException(Class invokingClass, String message, Throwable error) {
        super(message, error);
        LogManager.getLogger(invokingClass).error(message, error);
    }

}
