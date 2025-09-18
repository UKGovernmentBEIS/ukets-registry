package uk.ets.app.send.sms;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import uk.ets.app.send.sms.domain.SmsNotification;
import uk.ets.app.send.sms.service.SmsService;
import uk.gov.service.notify.NotificationClient;

@EmbeddedKafka(topics = {"send.sms.topic"},
    brokerProperties = "auto.create.topics.enable=true",
    count = 3,
    ports = {0, 0, 0}
)
@TestPropertySource(properties = {
    "spring.application.name=send-sms",
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}",
    "logging.level.root=INFO",
    "kafka.authentication.enabled=false"
})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SendSmsIntegrationTest {

    @Autowired
    private KafkaTemplate<String, SmsNotification> kafkaTemplate;

    @Autowired
    private SmsService service;

    @Test
    void test() throws Exception {
        // given
        NotificationClient mockClient = mock(NotificationClient.class);
        ReflectionTestUtils.setField(service, "client", mockClient);

        SmsNotification smsNotification = new SmsNotification();
        smsNotification.setPhoneNumber("+306987456321");
        smsNotification.setSmsTemplate("templateId");

        // when
        kafkaTemplate.send("send.sms.topic", smsNotification);
        kafkaTemplate.flush();
        kafkaTemplate.destroy();
        Thread.sleep(500);

        // then
        verify(mockClient, times(1)).sendSms("templateId", "+306987456321", null, null);
    }

}
