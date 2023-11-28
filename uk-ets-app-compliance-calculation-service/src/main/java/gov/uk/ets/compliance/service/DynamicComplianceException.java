package gov.uk.ets.compliance.service;

public class DynamicComplianceException extends RuntimeException {
    public DynamicComplianceException(String message) {
        super(message);
    }

    public DynamicComplianceException(Throwable cause) {
        super(cause);
    }

    public DynamicComplianceException(String message, Throwable cause) {
        super(message, cause);
    }
}
