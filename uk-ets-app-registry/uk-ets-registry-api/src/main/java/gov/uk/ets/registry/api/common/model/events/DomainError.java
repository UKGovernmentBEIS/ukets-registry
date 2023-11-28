package gov.uk.ets.registry.api.common.model.events;

public class DomainError {
    String message;
    String code;

    public DomainError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}


