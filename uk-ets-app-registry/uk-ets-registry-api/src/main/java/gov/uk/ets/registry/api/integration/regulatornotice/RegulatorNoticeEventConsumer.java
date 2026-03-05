package gov.uk.ets.registry.api.integration.regulatornotice;

import gov.uk.ets.registry.api.integration.consumer.account.EventConsumerTopicsUtil;
import gov.uk.ets.registry.api.integration.service.regulatornotice.RegulatorNoticeEventService;
import gov.uk.ets.registry.api.integration.service.regulatornotice.RegulatorNoticeResult;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.DefaultKafkaHeaderMapper;
import org.springframework.kafka.support.KafkaHeaderMapper;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.regulatornotice.RegulatorNoticeEvent;
import uk.gov.netz.integration.model.regulatornotice.RegulatorNoticeEventOutcome;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
public abstract class RegulatorNoticeEventConsumer {

    private final RegulatorNoticeEventService service;
    private final KafkaTemplate<String, RegulatorNoticeEventOutcome> kafkaTemplate;
    private final EventConsumerTopicsUtil topicsUtil;
    private final KafkaHeaderMapper mapper = new DefaultKafkaHeaderMapper();

    public RegulatorNoticeEventConsumer(RegulatorNoticeEventService service,
                                        KafkaTemplate<String, RegulatorNoticeEventOutcome> kafkaTemplate,
                                        EventConsumerTopicsUtil topicsUtil) {
        this.service = service;
        this.kafkaTemplate = kafkaTemplate;
        this.topicsUtil = topicsUtil;
    }

    public void processEvent(RegulatorNoticeEvent event, @Headers Map<String, Object> headers) {
        RegulatorNoticeResult result;
        try {
            result = service.process(event, headers);
        } catch (Exception exception) {
            log.error("Failed to receive a regulator notice.", exception);
            List<IntegrationEventErrorDetails> internalErrors =
                    List.of(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0600, "Failed to receive a regulator notice."));
            result = new RegulatorNoticeResult(internalErrors);
        }

        kafkaTemplate.send(buildKafkaMessage(event, headers, result));
    }

    private ProducerRecord<String, RegulatorNoticeEventOutcome> buildKafkaMessage(RegulatorNoticeEvent event,
                                                                                  Map<String, Object> headers,
                                                                                  RegulatorNoticeResult result) {
        IntegrationEventOutcome outcome = result.getErrors().isEmpty() ? IntegrationEventOutcome.SUCCESS : IntegrationEventOutcome.ERROR;
        String key = Optional.ofNullable(result.getRegistryId()).map(Object::toString).orElse("");
        RegulatorNoticeEventOutcome eventOutcome =
                new RegulatorNoticeEventOutcome(event, result.getErrors(), outcome);

        String sourceTopic = headers.get(KafkaHeaders.RECEIVED_TOPIC).toString();

        RecordHeaders target = new RecordHeaders();
        mapper.fromHeaders(new MessageHeaders(headers), target);

        return new ProducerRecord<>(topicsUtil.getResponseTopic(sourceTopic), null, key, eventOutcome, target);
    }
}
