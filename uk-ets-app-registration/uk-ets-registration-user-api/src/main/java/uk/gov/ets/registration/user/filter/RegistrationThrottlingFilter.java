package uk.gov.ets.registration.user.filter;

import gov.uk.ets.commons.ratelimiter.ThrottlingFilter;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Used to intercept requests for the subject of a request.
 */
@WebFilter("/api-registration")
@Component
@Log4j2
@Order(2)
public class RegistrationThrottlingFilter extends ThrottlingFilter {

    @Value("${throttling.registration.filter.rate-per-second:1}")
    private long refillRatePerSecond;

    @Value("${throttling.registration.filter.overdraft:50}")
    private long overdraft;


    /**
     * Global filter affecting the capability of users to register into the application.
     *
     * @param httpServletRequest the httpServletRequest from which we extract the user information.
     * @return the throttling key, in this case the user.
     */
    @Override
    public String getThrottlingKey(HttpServletRequest httpServletRequest) {

        return "registration-throttle";
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
