package gov.uk.ets.registry.api.common.model.events;

import java.util.ArrayList;
import java.util.List;

public class DomainEvent<T> {
    private T payload;

    List<DomainError> errors = new ArrayList<>();

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public T getPayload() {
        return payload;
    }

    public boolean isValid() {
        return errors.size() == 0;
    }

    public List<DomainError> getErrors() {
        return errors;
    }

    public void error(String code, String message) {
        errors.add(new DomainError(code, message));
    }
}
