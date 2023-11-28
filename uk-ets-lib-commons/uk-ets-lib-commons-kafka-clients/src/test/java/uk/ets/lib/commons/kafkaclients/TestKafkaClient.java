package uk.ets.lib.commons.kafkaclients;

import java.util.concurrent.ExecutionException;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@TestComponent
public class TestKafkaClient {
    private KafkaOperations<String, String> kafkaOperations;

    public TestKafkaClient(KafkaOperations<String, String> client) {
        this.kafkaOperations = client;
    }

    public ListenableFuture<SendResult<String, String>> sendToKafka(String topic, String message) throws ExecutionException, InterruptedException {
        return kafkaOperations.send(topic, message);
    }
}
