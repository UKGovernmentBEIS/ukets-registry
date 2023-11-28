package gov.uk.ets.registry.api.system.administration.service;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SystemAdministrationActionError {

    /**
     * Error code for the case that the current user does not have access to modify users.
     */
    public static final String ACCESS_NOT_ALLOWED = "ACCESS_NOT_ALLOWED";
    /**
     * Error code for the case that a json file could be parsed.
     */
    public static final String ERROR_PARSING_JSON = "ERROR_PARSING_JSON";

    /**
     * Error code for the case that production of performance users fails.
     */
    public static final String ERROR_PRODUCING_PERFORMANCE_USERS = "ERROR_PRODUCING_PERFORMANCE_USERS";
    
    /**
     * The error code.
     */
    String code;
    
    /**
     * The error message
     */
    String message;
}
