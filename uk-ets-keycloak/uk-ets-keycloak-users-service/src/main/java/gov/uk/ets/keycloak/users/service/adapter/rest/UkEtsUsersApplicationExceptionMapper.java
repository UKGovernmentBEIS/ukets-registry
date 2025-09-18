package gov.uk.ets.keycloak.users.service.adapter.rest;

import gov.uk.ets.keycloak.users.service.application.UkEtsUsersApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exception handler for uk ets endpoint
 */
@Provider
public class UkEtsUsersApplicationExceptionMapper implements ExceptionMapper<UkEtsUsersApplicationException> {
    private Logger logger = LoggerFactory.getLogger(UkEtsUsersApplicationExceptionMapper.class);
    @Override
    public Response toResponse(UkEtsUsersApplicationException exception) {
        logger.error(exception.getMessage(), exception);
        return Response.status(Status.BAD_REQUEST)
            .entity(exception.getMessage())
            .type("text/plain")
            .build();
    }
}
