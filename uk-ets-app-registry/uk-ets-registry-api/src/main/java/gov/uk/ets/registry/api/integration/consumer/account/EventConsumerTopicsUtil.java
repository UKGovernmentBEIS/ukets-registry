package gov.uk.ets.registry.api.integration.consumer.account;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class EventConsumerTopicsUtil {

    //Create account topics
    // Other topics than maritime are not in used currently.
    @Value("${kafka.integration.installation.account.opening.request.topic}")
    private String installationAccountOpeningRequestTopic;
    @Value("${kafka.integration.aviation.account.opening.request.topic}")
    private String aviationAccountOpeningRequestTopic;
    @Value("${kafka.integration.maritime.account.opening.request.topic}")
    private String maritimeAccountOpeningRequestTopic;
    @Value("${kafka.integration.installation.account.opening.response.topic}")
    private String installationAccountOpeningResponseTopic;
    @Value("${kafka.integration.aviation.account.opening.response.topic}")
    private String aviationAccountOpeningResponseTopic;
    @Value("${kafka.integration.maritime.account.opening.response.topic}")
    private String maritimeAccountOpeningResponseTopic;

    //Update Account topics
    @Value("${kafka.integration.installation.account.updating.request.topic}")
    private String installationAccountUpdatingRequestTopic;
    @Value("${kafka.integration.aviation.account.updating.request.topic}")
    private String aviationAccountUpdatingRequestTopic;
    @Value("${kafka.integration.maritime.account.updating.request.topic}")
    private String maritimeAccountUpdatingRequestTopic;
    @Value("${kafka.integration.installation.account.updating.response.topic}")
    private String installationAccountUpdatingResponseTopic;
    @Value("${kafka.integration.aviation.account.updating.response.topic}")
    private String aviationAccountUpdatingResponseTopic;
    @Value("${kafka.integration.maritime.account.updating.response.topic}")
    private String maritimeAccountUpdatingResponseTopic;

    //Mets contacts topics
    @Value("${kafka.integration.installation.mets.contacts.request.topic}")
    private String installationMetsContactsRequestTopic;
    @Value("${kafka.integration.aviation.mets.contacts.request.topic}")
    private String aviationMetsContactsRequestTopic;
    @Value("${kafka.integration.maritime.mets.contacts.request.topic}")
    private String maritimeMetsContactsRequestTopic;
    @Value("${kafka.integration.installation.mets.contacts.response.topic}")
    private String installationMetsContactsResponseTopic;
    @Value("${kafka.integration.aviation.mets.contacts.response.topic}")
    private String aviationMetsContactsResponseTopic;
    @Value("${kafka.integration.maritime.mets.contacts.response.topic}")
    private String maritimeMetsContactsResponseTopic;

    public String getResponseTopic(String topic) {
        if (Objects.equals(topic, installationAccountOpeningRequestTopic)) {
            return installationAccountOpeningResponseTopic;
        }

        if (Objects.equals(topic, aviationAccountOpeningRequestTopic)) {
            return aviationAccountOpeningResponseTopic;
        }

        if (Objects.equals(topic, maritimeAccountOpeningRequestTopic)) {
            return maritimeAccountOpeningResponseTopic;
        }

        if (Objects.equals(topic, installationAccountUpdatingRequestTopic)) {
            return installationAccountUpdatingResponseTopic;
        }

        if (Objects.equals(topic, aviationAccountUpdatingRequestTopic)) {
            return aviationAccountUpdatingResponseTopic;
        }

        if (Objects.equals(topic, maritimeAccountUpdatingRequestTopic)) {
            return maritimeAccountUpdatingResponseTopic;
        }

        if (Objects.equals(topic, installationMetsContactsRequestTopic)) {
            return installationMetsContactsResponseTopic;
        }

        if (Objects.equals(topic, aviationMetsContactsRequestTopic)) {
            return aviationMetsContactsResponseTopic;
        }

        if (Objects.equals(topic, maritimeMetsContactsRequestTopic)) {
            return maritimeMetsContactsResponseTopic;
        }

        throw new IllegalStateException("Unknown Request topic");
    }
}
