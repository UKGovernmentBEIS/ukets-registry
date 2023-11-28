package gov.uk.ets.commons.logging;

import static gov.uk.ets.commons.logging.MDCExceptionCode.retrieveErrorCodeForException;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.util.NestedServletException;

/**
 * Used to intercept requests for the subject of a request..
 */
@Log4j2
public abstract class MDCLoggingFilter implements Filter {


    private static final String X_REQUEST_ID = "X-Request-ID";
    private static final String X_REQUEST_TIME = "X-Request-Time";

    private BuildProperties buildProperties;
    private Config config;

    public void setConfig(Config config) {
        this.config = config;
    }

    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    /**
     * Override this in order to set how the userId is identified.
     *
     * @param httpServletRequest the request
     * @return the user id
     */
    public abstract String userId(HttpServletRequest httpServletRequest);

    /**
     * Override this in order to set how the ETS userId is identified.
     *
     * @param httpServletRequest the request
     * @return the ETS user id
     */
    public abstract String etsUserId(HttpServletRequest httpServletRequest);

    /**
     * This filter is responsible for creating all the necessary MDC entries already in the reception of the
     * request. At the end the MDC is cleared.
     *
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain)
        throws IOException, ServletException {
        MDCWrapper mdc = new MDCWrapper();
        // clearing it in a try-finally block resulted in clearing the context too soon (before exception handling kicks in)
        clear(mdc);
        String xRequestId = xRequestId((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
        Instant start = Instant.now();
        mdc.put(MDCWrapper.Attr.RECEPTION_DATE, MDCWrapper.FORMATTER.format(start));
        mdc.put(MDCWrapper.Attr.INTERACTION_IDENTIFIER, xRequestId);
        mdc.put(MDCWrapper.Attr.REQUEST_TIME, ((HttpServletRequest) servletRequest).getHeader(X_REQUEST_TIME));
        mdc.put(MDCWrapper.Attr.APP_NAME, buildProperties != null ? buildProperties.getName() : "");
        mdc.put(MDCWrapper.Attr.APP_VERSION, buildProperties != null ? buildProperties.getVersion() : "");
        mdc.put(MDCWrapper.Attr.ENTRYPOINT, ((HttpServletRequest) servletRequest).getRequestURI());
        mdc.put(MDCWrapper.Attr.CALLING_SERVER, servletRequest.getRemoteHost());
        mdc.put(MDCWrapper.Attr.APP_SERVER,
            InetAddress.getLocalHost().getHostName() + ":" + servletRequest.getLocalPort()
        );
        mdc.put(MDCWrapper.Attr.METHOD, ((HttpServletRequest) servletRequest).getMethod());
        mdc.put(MDCWrapper.Attr.PROTOCOL, servletRequest.getProtocol());
        mdc.put(MDCWrapper.Attr.CAUSE, MDCWrapper.Cause.INCOMING_REQUEST.outputValue());
        mdc.put(MDCWrapper.Attr.USER_ID, userId((HttpServletRequest) servletRequest));
        mdc.put(MDCWrapper.Attr.ETS_USER_ID, etsUserId((HttpServletRequest) servletRequest));
        mdc.put(MDCWrapper.Attr.APP_COMMIT_ID,
            config != null && config.getCommitId() != null ? config.getCommitId()[0] : "");
        mdc.put(MDCWrapper.Attr.APP_COMMIT_DATE,
            config != null && config.getCommitTime() != null ? config.getCommitTime()[0] : "");
        log.info(LogEvent.PROCESSING_START.outputValue());
        try {
            filterChain.doFilter(servletRequest, servletResponse);
            mdc.put(MDCWrapper.Attr.TIME_ELAPSED_MS, String.valueOf(Duration.between(start, Instant.now()).toMillis()));
            log.info(LogEvent.PROCESSING_END.outputValue());
            // TODO: For the moment, log the exception here to bypass the issue with the MDC context being cleared
            // before the exception is logged from Spring. This will have as an effect that the exception will
            // be logged twice.
            // We should consider if it is possible to not throw the exception but directly return a 500 response.
        } catch (NestedServletException nse) {
            mdc.put(MDCWrapper.Attr.EXCEPTION_ERROR_CODES, retrieveErrorCodeForException(nse.getCause()));
            log.error(LogEvent.UNHANDLED_EXCEPTION, nse.getCause());
            throw nse;
        } catch (Exception e) {
            mdc.put(MDCWrapper.Attr.EXCEPTION_ERROR_CODES, retrieveErrorCodeForException(e));
            log.error(LogEvent.UNHANDLED_EXCEPTION, e);
            throw e;
        } finally {
            clear(mdc);
        }
    }

    private String xRequestId(HttpServletRequest servletRequest, HttpServletResponse servletResponse)
        throws UnsupportedEncodingException {
        /* Note that the following line raises the SERVLET_HEADER spotbugs error. This error is suppressed
            because the X_REQUEST_ID header will be only used to track the user request.  */
        final String xRequestIdHeaderValue = servletRequest.getHeader(X_REQUEST_ID);
        String encodedValue;
        if (xRequestIdHeaderValue != null) {
            encodedValue = URLEncoder.encode(xRequestIdHeaderValue, StandardCharsets.UTF_8.displayName());
        } else {
            encodedValue = UUID.randomUUID().toString();
        }
        servletResponse.setHeader(X_REQUEST_ID, encodedValue);
        servletResponse.addHeader(ACCESS_CONTROL_EXPOSE_HEADERS, X_REQUEST_ID);
        return encodedValue;
    }

    private void clear(MDCWrapper mdc) {
        mdc.clear(
            MDCWrapper.Attr.APP_NAME,
            MDCWrapper.Attr.APP_VERSION,
            MDCWrapper.Attr.APP_SERVER,
            MDCWrapper.Attr.ENTRYPOINT,
            MDCWrapper.Attr.CALLING_SERVER,
            MDCWrapper.Attr.METHOD,
            MDCWrapper.Attr.PROTOCOL,
            MDCWrapper.Attr.CAUSE,
            MDCWrapper.Attr.USER_ID,
            MDCWrapper.Attr.ETS_USER_ID,
            MDCWrapper.Attr.INTERACTION_IDENTIFIER,
            MDCWrapper.Attr.TIME_ELAPSED_MS,
            MDCWrapper.Attr.RECEPTION_DATE,
            MDCWrapper.Attr.APP_COMMIT_ID,
            MDCWrapper.Attr.APP_COMMIT_DATE,
            MDCWrapper.Attr.RESOURCE_ID,
            MDCWrapper.Attr.RESOURCE_TYPE,
            MDCWrapper.Attr.EXCEPTION_ERROR_CODES
        );
    }
}
