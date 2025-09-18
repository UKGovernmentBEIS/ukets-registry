package uk.gov.ets.registration.user.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.commons.ratelimiter.ThrottlingFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * This filter is used to prevent consecutive email requests using the same email.
 */
@WebFilter("/api-registration")
@Component
@Log4j2
@Order(3)
public class PerMailThrottlingFilter extends ThrottlingFilter {

    @Value("${throttling.registration.filter.rate-per-second:1}")
    private long refillRatePerSecond;

    @Value("${throttling.registration.filter.overdraft:50}")
    private long overdraft;


    /**
     * This filter is applied after the general {@link RegistrationThrottlingFilter} and retrieves the email from
     * the request body. When the email is set, we append the email to the throttling key in order to create a different
     * bucket for this email. When the email is null, we set the throttling key to null in order to prevent throttle
     * the same request twice.
     *
     * @param httpServletRequest the httpServletRequest from which we extract the user information.
     * @return the throttling key, in this case an email specific key or null.
     */
    @Override
    public String getThrottlingKey(HttpServletRequest httpServletRequest) {
        String email = getRequestBodyParam(httpServletRequest, "email");

        if (email != null) {
            return "registration-throttle" + email;
        }
        return null;
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

    private String getRequestBodyParam(HttpServletRequest httpServletRequest, String key) {
        String param = null;
        try {
            String requestBody = httpServletRequest.getReader().lines().reduce("", (s1, s2) -> s1 + s2);
            TypeReference<HashMap<String, String>> typeRef = new TypeReference<>() {
            };

            if (!requestBody.isEmpty()) {
                Map<String, String> parametersMap = new ObjectMapper().readValue(requestBody, typeRef);
                param = parametersMap.get(key);
            }
        } catch (IOException e) {
            return null;
        }
        return param;
    }
}
