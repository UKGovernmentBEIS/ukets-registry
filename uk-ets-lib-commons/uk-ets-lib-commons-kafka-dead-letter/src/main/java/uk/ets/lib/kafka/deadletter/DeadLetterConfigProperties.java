package uk.ets.lib.kafka.deadletter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;

@ToString
@EqualsAndHashCode
@Getter
public class DeadLetterConfigProperties {

    private String applicationName;

    private int deadLetterTopicPartition;

    private int deadLetterMaxAttempts;


    @Value("${spring.application.name:unknown}")
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * Partition index starts from zero.
     */
    @Value("${dead.letter.topic.partition:0}")
    public void setDeadLetterTopicPartition(int deadLetterTopicPartition) {
        this.deadLetterTopicPartition = deadLetterTopicPartition;
    }

    @Value("${dead.letter.max.attempts:3}")
    public void setDeadLetterMaxAttempts(int deadLetterMaxAttempts) {
        this.deadLetterMaxAttempts = deadLetterMaxAttempts;
    }
}
