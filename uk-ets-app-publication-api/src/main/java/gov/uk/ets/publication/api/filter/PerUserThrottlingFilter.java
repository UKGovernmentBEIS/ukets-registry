package gov.uk.ets.publication.api.filter;

import gov.uk.ets.commons.logging.SecurityLog;
import gov.uk.ets.commons.ratelimiter.ThrottlingFilter;
import gov.uk.ets.lib.commons.security.oauth2.token.OAuth2ClaimNames;
import gov.uk.ets.publication.api.authz.AuthorizationService;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Used to intercept requests for the subject of a request.
 */
@WebFilter("/api-publication/*")
@Component
@Log4j2
public class PerUserThrottlingFilter extends ThrottlingFilter {

    @Value("${throttling.per.user.filter.rate-per-second:5}")
    private long refillRatePerSecond;

    @Value("${throttling.per.user.filter.overdraft:50}")
    private long overdraft;

    private AuthorizationService authorizationService;

    @Autowired(required = true)
    public void setAuthorizationService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    /**
     * Throttling filter implementation that throttles requests per user. Take note that non-authenticated requests
     * cannot be throttled.
     * @param httpServletRequest the httpServletRequest from which we extract the user information.
     * @return the throttling key, in this case the user.
     */
    @Override
    public String getThrottlingKey(HttpServletRequest httpServletRequest) {
        String userId = "";

        // no throttling for public resources
        if (httpServletRequest.getAuthType() == null) {
            return null;
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

    /**
     * @see ThrottlingFilter#refillRatePerSecond()
     */
    @Override
    public long refillRatePerSecond() {
        return refillRatePerSecond;
    }

    /**
     * @see ThrottlingFilter#overdraft()
     */
    @Override
    public long overdraft() {
        return overdraft;
    }

    /**
     * @see ThrottlingFilter#throttlingContentType()
     */
    @Override
    public String throttlingContentType() {
        return "application/json;charset=utf-8";
    }

    /**
     * @see ThrottlingFilter#throttlingMessage()
     */
    @Override
    public String throttlingMessage() {
        return "Too many requests";
    }


}
