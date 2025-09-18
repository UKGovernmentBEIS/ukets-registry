package gov.uk.ets.registry.api.ar.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserNotFoundException extends RuntimeException {

    private final String componentId;

    public UserNotFoundException(String message, String componentId) {
        super(message);
        this.componentId = componentId;
    }
}
