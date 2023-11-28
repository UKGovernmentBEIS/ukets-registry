package uk.ets.lib.kafka.deadletter;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.kafka.annotation.KafkaListener;

@TestComponent
public class TestConsumer {
    @KafkaListener(
        topics = "the-topic",
        groupId = "group-id",
        containerFactory = "concurrentKafkaListenerContainerFactory")
    public void handleAnswer(String message) {
        throw new RuntimeException("An exception during message handling");
    }
}
