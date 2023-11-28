package gov.uk.ets.commons.logging;

import java.lang.reflect.Method;
import java.time.Instant;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Used to uniformly log kafka topic executions.
 */
@Component
@Aspect
@Log4j2
public class KafkaConsumerMDCLoggingHandler extends MDCLoggingHandler {

    private static final String KAFKA_PROTOCOL = "Kafka protocol";

    @Value("${spring.kafka.bootstrap-servers:}")
    private String kafkaBootstrapAddress;

    private BuildProperties buildProperties;

    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    /**
     * Log KafkaListeners.
     * @param proceedingJoinPoint The join point
     * @param kafkaListenerAnnotation the kafka listener annotation
     * @return the actual outcome
     * @throws Throwable an exception
     */
    @Around(value = "@annotation(kafkaListenerAnnotation)")
    public Object applyKafkaListener(ProceedingJoinPoint proceedingJoinPoint, KafkaListener kafkaListenerAnnotation)
        throws Throwable {
        MDCWrapper mdc = new MDCWrapper();
        Instant start = Instant.now();
        fillCommonMDC(mdc, buildProperties, start);
        fillMDC(kafkaListenerAnnotation.topics(), mdc);
        return getProceedingJoinPointResult(proceedingJoinPoint, mdc, start);
    }

    /**
     * Log kafkaHandlerAnnotation.
     * @param proceedingJoinPoint The join point
     * @param kafkaHandlerAnnotation the kafka listener annotation
     * @return the actual outcome
     * @throws Throwable an exception
     */
    @Around(value = "@annotation(kafkaHandlerAnnotation)")
    public Object applyKafkaHandler(ProceedingJoinPoint proceedingJoinPoint, KafkaHandler kafkaHandlerAnnotation)
        throws Throwable {
        MDCWrapper mdc = new MDCWrapper();
        Instant start = Instant.now();
        fillCommonMDC(mdc, buildProperties, start);
        KafkaListener kafkaListener = getKafkaListener(proceedingJoinPoint);
        fillMDC(kafkaListener != null ? kafkaListener.topics() : new String[]{}, mdc);
        return getProceedingJoinPointResult(proceedingJoinPoint, mdc, start);
    }

    private KafkaListener getKafkaListener(ProceedingJoinPoint proceedingJoinPoint) {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        Class<?> clazz = method.getDeclaringClass();
        return clazz.getAnnotation(KafkaListener.class);
    }

    private void fillMDC(String[] entrypoints, MDCWrapper mdc) {
        mdc.put(MDCWrapper.Attr.ENTRYPOINT, String.join(",", entrypoints));
        mdc.put(MDCWrapper.Attr.CALLING_SERVER, kafkaBootstrapAddress);
        mdc.put(MDCWrapper.Attr.PROTOCOL, KAFKA_PROTOCOL);
        mdc.put(MDCWrapper.Attr.CAUSE, MDCWrapper.Cause.MESSAGE_CONSUMPTION.outputValue());
    }
}
