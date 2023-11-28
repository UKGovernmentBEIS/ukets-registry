package gov.uk.ets.registry.api.tal.error;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrustedAccountListActionErrorResponse implements Serializable {
    private static final long serialVersionUID = 1096253694792178102L;
    TrustedAccountListActionError error;

    /**
     * An error response for a trusted account list action.
     *
     * @param exception an exception.
     * @return the error response.
     */
    public static TrustedAccountListActionErrorResponse from(TrustedAccountListActionException exception) {
        TrustedAccountListActionErrorResponse
            response = new TrustedAccountListActionErrorResponse();
        response.setError(exception.getTrustedAccountListActionError());
        return response;
    }
}
