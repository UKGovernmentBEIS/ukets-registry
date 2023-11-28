package uk.gov.ets.password.validator.api.web.filter;

import gov.uk.ets.commons.logging.MDCLoggingFilter;
import gov.uk.ets.commons.logging.SecurityLog;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
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
