package uk.gov.ets.password.validator.api.web.filter;

import gov.uk.ets.commons.logging.MDCLoggingFilter;
import gov.uk.ets.commons.logging.SecurityLog;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * Used to intercept requests for the subject of a request..
 */
@WebFilter("/api-password-validate")
@Component
@Log4j2
public class CustomLoggingAndExceptionHandlingFilter extends MDCLoggingFilter {

    @Override
    public String userId(HttpServletRequest httpServletRequest) {
        return "";
    }

    @Override
    public String etsUserId(HttpServletRequest httpServletRequest) {
        return "";
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        try {
            super.doFilter(servletRequest, servletResponse, filterChain);
        } catch (Exception e) {
            SecurityLog.log(log, e.getMessage(), e.getCause());
        }
    }
}
