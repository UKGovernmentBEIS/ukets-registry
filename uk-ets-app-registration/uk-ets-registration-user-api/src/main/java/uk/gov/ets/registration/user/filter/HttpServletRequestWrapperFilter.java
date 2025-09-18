package uk.gov.ets.registration.user.filter;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@WebFilter("/api-registration")
@Component
@Log4j2
@Order(1)
public class HttpServletRequestWrapperFilter implements jakarta.servlet.Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {

        /* In the {@link PerMailThrottlingFilter} filter, we have to read the registration request body to get the email
        for which we will make the throttling bucket. As we cannot read the body multiple times using the
        {@link HttpServletRequest} methods getInputStream or getReader, we create a wrapping class
        {@link CachedBodyHttpServletRequest} to cache the request body. */
        CachedBodyHttpServletRequest cachedBodyHttpServletRequest =
            new CachedBodyHttpServletRequest((HttpServletRequest) servletRequest);

        filterChain.doFilter(cachedBodyHttpServletRequest, servletResponse);

    }
}
