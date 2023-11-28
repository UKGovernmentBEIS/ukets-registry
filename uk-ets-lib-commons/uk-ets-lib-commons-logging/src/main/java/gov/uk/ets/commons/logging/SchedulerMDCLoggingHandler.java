package gov.uk.ets.commons.logging;

import java.time.Instant;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Used to uniformly log {@link Scheduled} executions.
 */
@Component
@Aspect
@Log4j2
public class SchedulerMDCLoggingHandler extends MDCLoggingHandler {

    private BuildProperties buildProperties;

    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    /**
     * Log KafkaListeners.
     * @param proceedingJoinPoint The join point
     * @param scheduledAnnotation the scheduled annotation
     * @return the actual outcome
     * @throws Throwable an exception
     */
    @Around(value = "@annotation(scheduledAnnotation)")
    public Object applyScheduled(ProceedingJoinPoint proceedingJoinPoint, Scheduled scheduledAnnotation)
        throws Throwable {
        MDCWrapper mdc = new MDCWrapper();
        Instant start = Instant.now();
        fillCommonMDC(mdc, buildProperties, start);
        fillMDC(new String[] {proceedingJoinPoint.getSignature().toShortString()}, mdc);
        return getProceedingJoinPointResult(proceedingJoinPoint, mdc, start);
    }

    private void fillMDC(String[] entrypoints, MDCWrapper mdc) {
        mdc.put(MDCWrapper.Attr.ENTRYPOINT, String.join(",", entrypoints));
        mdc.put(MDCWrapper.Attr.CAUSE, MDCWrapper.Cause.INTERNAL_SCHEDULER.outputValue());
    }
}
