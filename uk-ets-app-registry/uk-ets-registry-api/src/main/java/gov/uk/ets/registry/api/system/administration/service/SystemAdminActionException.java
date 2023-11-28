package gov.uk.ets.registry.api.system.administration.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@SuppressWarnings("serial")
public class SystemAdminActionException extends RuntimeException {

    public static SystemAdminActionException create(SystemAdministrationActionError error) {
        SystemAdminActionException exception = new SystemAdminActionException();
        exception.addError(error);
        return exception;
    }

    private final transient List<SystemAdministrationActionError> sysAdminActionErrors = new ArrayList<>();

    /**
     * @return The list of errors
     */
    public List<SystemAdministrationActionError> getSystemAdministrationActionErrors() {
        return sysAdminActionErrors;
    }

    /**
     * Adds the error to the error list.
     * @param error The {@link TaskActionError} error
     */
    public void addError(SystemAdministrationActionError error) {
        this.sysAdminActionErrors.add(error);
    }

    /**
     * @return the error messages of its {@link TaskActionError} errors.
     */
    @Override
    public String getMessage() {
        return String.join("\n", sysAdminActionErrors.stream()
                .map(err -> err.getMessage()).collect(Collectors.toList()));
    }    
    
}
