package gov.uk.ets.registry.api.common.web.filter;

import gov.uk.ets.commons.logging.MDCLoggingFilter;
import gov.uk.ets.commons.logging.SecurityLog;
import gov.uk.ets.lib.commons.security.oauth2.token.OAuth2ClaimNames;
import gov.uk.ets.registry.api.authz.AuthorizationService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Used to intercept requests for the subject of a request..
 */

@Log4j2
public class CustomLoggingFilter extends MDCLoggingFilter {

    private AuthorizationService authorizationService;

    @Autowired(required = true)
    public void setAuthorizationService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Override
    public String userId(HttpServletRequest httpServletRequest) {
        String userId = "";

        //If the resource is public -hence no authentication required- getAuthType() returns null
        if (httpServletRequest.getAuthType() == null) {
            return "Public resource - no user ID";
        }
        try {
            if (authorizationService != null && authorizationService.getClaim(OAuth2ClaimNames.SUBJECT) != null) {
                userId = authorizationService.getClaim(OAuth2ClaimNames.SUBJECT);
            }
        } catch (Exception e) {
            SecurityLog.log(log, "A user id could not be extracted for this request", e);
        }
        return userId;
    }

    @Override
    public String etsUserId(HttpServletRequest httpServletRequest) {
        String etsUserId = "";

        //If the resource is public -hence no authentication required- getAuthType() returns null
        if (httpServletRequest.getAuthType() == null) {
            return "Public resource - no ETS userId";
        }
        try {
            if (authorizationService != null && authorizationService.getUrid() != null) {
                etsUserId = authorizationService.getUrid();
            }
        } catch (Exception e) {
            SecurityLog.log(log, "ETS userId could not be extracted for this request", e);
        }
        return etsUserId;
    }
}
