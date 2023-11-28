package uk.gov.ets.kp.webservices.registry;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

@Getter
public class KafkaTopics {

    private String defaultTopic;
    private String noticesKafkaTopic;
    private String reconciliationInTopic;

    @Value("${spring.kafka.template.default-topic}")
    public void setDefaultTopic(String defaultTopic) {
        this.defaultTopic = defaultTopic;
    }

    @Value("${spring.kafka.template.itl-notices-topic}")
    public void setNoticesKafkaTopic(String noticesKafkaTopic) {
        this.noticesKafkaTopic = noticesKafkaTopic;
    }

    @Value("${spring.kafka.template.reconciliation-in-topic}")
    public void setReconciliationInTopic(String reconciliationInTopic) {
        this.reconciliationInTopic = reconciliationInTopic;
    }
}
