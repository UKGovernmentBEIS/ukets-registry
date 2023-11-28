package gov.uk.ets.commons.ratelimiter;

import com.google.common.cache.LoadingCache;
import gov.uk.ets.commons.logging.SecurityLog;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import java.io.IOException;
import java.time.Duration;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;


/**
 * Generic filter that can be used to throttle REST API requests. Implementation uses bucket4j.
 *
 * The bucket size is {@link ThrottlingFilter#overdraft()} calls (which cannot be exceeded at any given time),
 * with a {@link ThrottlingFilter#refillRatePerSecond()} that continually increases tokens in the bucket.
 * Example. Assuming that:
 * <ul>
 *  <li>{@link ThrottlingFilter#overdraft()} = 50<li/>
 *  <li>{@link ThrottlingFilter#refillRatePerSecond()} = 10<li/>
 * <ul/>
 * then if client app averages 10 calls per second, it will never be throttled, and moreover client
 * have overdraft equals to 50 calls which can be used if average is little bit higher
 * that 10 call/sec on short time period.
 *
 */
@Log4j2
public abstract class ThrottlingFilter implements javax.servlet.Filter {

    private LoadingCache<String, Bucket> buckets;

    @Autowired(required = false)
    public void setBuckets(LoadingCache<String, Bucket> buckets) {
        this.buckets = buckets;
    }

    /**
     * Override this to provide the key for which you are going to throttle the API. It can be either dynamic (i.e.
     * the user id) or fixed (something generic for the whole application).
     *
     * If null is returned, then throttling is not applied.
     *
     * @return the throttling key
     * @param httpRequest the request
     */
    public abstract String getThrottlingKey(HttpServletRequest httpRequest);

    /**
     * Override this to provide the refill rate per second. This value denotes the actual rate limiting.
     * @return the rate limit
     */
    public abstract long refillRatePerSecond();

    /**
     * Overdraft is the slack that can be tolerated for a short amount of time.
     * @return the overdraft
     */
    public abstract long overdraft();

    /**
     * Returns the content type in case of throttling.
     * @return
     */
    public abstract String throttlingContentType();

    /**
     * Returns the message in case of throttling.
     * @return
     */
    public abstract String throttlingMessage();

    /**
     * This filter will implement the bucket4j approach.
     *
     * @param servletRequest the servletRequest
     * @param servletResponse the servletResponse
     * @param filterChain the filterChain
     * @throws IOException exception in case of errors
     * @throws ServletException exception in case of errors
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws
        IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;

        String throttlingKey = getThrottlingKey(httpRequest);
        if (throttlingKey == null) {
            log.debug("Throttling key was not set. Throttling is disabled for this request {}.", httpRequest.getRequestURL());
            filterChain.doFilter(servletRequest, servletResponse);
        } else if (buckets == null) {
            log.warn("Bucket cache was not set. Throttling is disabled for this request {}.", httpRequest.getRequestURL());
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            throttle(servletRequest, servletResponse, filterChain, throttlingKey);
        }
    }

    private void throttle(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain,
                           String throttlingKey) throws IOException, ServletException {

        Bucket bucket = null;
        try {
            bucket = buckets.getUnchecked("throttler-" + throttlingKey);
        } catch (Exception e) {
            // could not find a bucket for this throttler
        }
        if (bucket == null) {
            bucket = createNewBucket();
            buckets.put("throttler-" + throttlingKey, bucket);
        }


        // tryConsume returns false immediately if no tokens available with the bucket
        if (bucket.tryConsume(1)) {
            // the limit is not exceeded
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            // limit is exceeded
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            httpResponse.setContentType(throttlingContentType());
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.getWriter().append(throttlingMessage());
            SecurityLog.log(log, "Request was throttled due to excessive amount of requests.");
        }
    }

    private Bucket createNewBucket() {
        long overdraft = overdraft();
        Refill refill = Refill.greedy(refillRatePerSecond(), Duration.ofSeconds(1));
        Bandwidth limit = Bandwidth.classic(overdraft, refill);
        return Bucket4j.builder().addLimit(limit).build();
    }
}