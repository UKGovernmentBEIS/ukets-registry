package uk.ets.lib.commons.kafkaclients;

import lombok.RequiredArgsConstructor;


import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * Aspect of message sending failure and success to the kafka server.
 */
@Aspect
@RequiredArgsConstructor
public class KafkaClientLoggingAspect {
    private final Logger log;

    /**
     * Pointcut that matches the execution of {@link org.springframework.kafka.core.KafkaOperations#send(String, Object)} method.
     */
    @Pointcut("execution(public * org.springframework.kafka.core.KafkaOperations+.send(*, *))")
    public void getKafkaOperationsPointcut() {}

    /**
     * Around advice which adds a future callback to the result of the {@link org.springframework.kafka.core.KafkaOperations#send(String, Object)} method.
     * The callback handles the failure and the success by logging the data and the topic.
     * @param proceedingJoinPoint The {@link ProceedingJoinPoint}
     * @param topic The kafka topic
     * @param data The message
     * @return The {@link ListenableFuture<SendResult<?, ?>>} result of the joinPoint enriched with the callback.
     * @throws Throwable
     */
    @Around("getKafkaOperationsPointcut() && args(topic, data)")
    public Object sendMessage(ProceedingJoinPoint proceedingJoinPoint, String topic, Object data) throws Throwable {
        try {
            ListenableFuture<SendResult<?, ?>> future = (ListenableFuture<SendResult<?, ?>>) proceedingJoinPoint
                .proceed();
            future.addCallback(new ListenableFutureCallback() {
                @Override
                public void onSuccess(Object result) {
                }

                @Override
                public void onFailure(Throwable throwable) {
                    logError(throwable, data, topic);
                }
            });
            return future;
        } catch (Throwable throwable) {
            logError(throwable, data,topic);
            throw throwable;
        }
    }

    private void logError(Throwable throwable, Object message, String topic) {
        String error = String.format("%s failed to be sent to topic with name: %s cause to %s", message, topic, throwable.getMessage());
        log.error(error);
    }
}
