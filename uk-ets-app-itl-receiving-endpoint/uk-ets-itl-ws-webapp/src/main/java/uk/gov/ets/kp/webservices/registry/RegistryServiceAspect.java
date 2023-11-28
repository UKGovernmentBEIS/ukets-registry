package uk.gov.ets.kp.webservices.registry;

import java.rmi.RemoteException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Aspect of {@link RegistryService} methods execution.
 */
@Aspect
public class RegistryServiceAspect {
    private Log log = LogFactory.getLog(RegistryServiceAspect.class);

    /**
     * Pointcut that matches methods of {@link RegistryService} class
     */
    @Pointcut("within(uk.gov.ets.kp.webservices.registry.RegistryService)")
    public void withinRegistryService() {
    }

    /**
     * Pointcut that matches all the public methods
     */
    @Pointcut("execution(public * *.*(..))")
    public void publicMethods() {
    }

    @AfterThrowing(pointcut = "withinRegistryService() && publicMethods()", throwing = "exception")
    public void afterThrowingAdvice(Exception exception) throws Exception {
        log.error(exception);
        throw new IllegalStateException("Unexpected error while processing the request.");
    }
}
