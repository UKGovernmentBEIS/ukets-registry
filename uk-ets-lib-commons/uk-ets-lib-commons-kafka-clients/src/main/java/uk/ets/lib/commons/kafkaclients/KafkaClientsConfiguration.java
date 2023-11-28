package uk.ets.lib.commons.kafkaclients;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@Configuration
public class KafkaClientsConfiguration {
    @Bean
    public Logger log() {
        return LogManager.getLogger(KafkaClientLoggingAspect.class);
    }

    @Bean
    public KafkaClientLoggingAspect kafkaClientLoggingAspect(Logger log) {
        return new KafkaClientLoggingAspect(log);
    }
}
