package gov.uk.ets.registry.api.integration.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import uk.ets.lib.commons.kafkaconfig.SharedKafkaConfig;
import uk.ets.lib.kafka.deadletter.IntegrationKafkaDeadLetterConfiguration;
import uk.gov.netz.integration.model.account.AccountOpeningEvent;
import uk.gov.netz.integration.model.account.AccountOpeningEventOutcome;
import uk.gov.netz.integration.model.account.AccountUpdatingEvent;
import uk.gov.netz.integration.model.account.AccountUpdatingEventOutcome;
import uk.gov.netz.integration.model.emission.AccountEmissionsUpdateEvent;
import uk.gov.netz.integration.model.emission.AccountEmissionsUpdateEventOutcome;
import uk.gov.netz.integration.model.exemption.AccountExemptionUpdateEvent;
import uk.gov.netz.integration.model.exemption.AccountExemptionUpdateEventOutcome;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEvent;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEventOutcome;
import uk.gov.netz.integration.model.operator.OperatorUpdateEvent;
import uk.gov.netz.integration.model.operator.OperatorUpdateEventOutcome;
import uk.gov.netz.integration.model.regulatornotice.RegulatorNoticeEvent;
import uk.gov.netz.integration.model.regulatornotice.RegulatorNoticeEventOutcome;
import uk.gov.netz.integration.model.withold.AccountWithholdUpdateEvent;
import uk.gov.netz.integration.model.withold.AccountWithholdUpdateEventOutcome;


@Configuration
@Log4j2
@Import({SharedKafkaConfig.class,IntegrationKafkaDeadLetterConfiguration.class})
@RequiredArgsConstructor
public class IntegrationKafkaConfiguration {

    @Value("${kafka.integration.installation.emissions.consumer.group-id}")
    private String installationEmissionsConsumerGroup;

    @Value("${kafka.integration.aviation.emissions.consumer.group-id}")
    private String aviationEmissionsConsumerGroup;

    @Value("${kafka.integration.maritime.emissions.consumer.group-id}")
    private String maritimeEmissionsConsumerGroup;

    @Value("${kafka.integration.outcome.consumer.group-id}")
    private String outcomeConsumerGroup;

    @Value("${kafka.integration.account.opening.request.consumer.group-id}")
    private String accountOpeningRequestConsumerGroup;

    @Value("${kafka.integration.account.opening.response.consumer.group-id}")
    private String accountOpeningResponseConsumerGroup;

    @Value("${kafka.integration.set.operator.response.consumer.group-id}")
    private String setOperatorResponseConsumerGroup;

    @Value("${kafka.integration.exemption.request.consumer.group-id}")
    private String exemptionRequestConsumerGroup;

    @Value("${kafka.integration.exemption.response.consumer.group-id}")
    private String exemptionResponseConsumerGroup;

    @Value("${kafka.integration.withhold.request.consumer.group-id}")
    private String withholdRequestConsumerGroup;

    @Value("${kafka.integration.withhold.response.consumer.group-id}")
    private String withholdResponseConsumerGroup;

    @Value("${kafka.integration.installation.emissions.response.transactional.id}")
    private String installationEmissionsResponseTransactionalId;

    @Value("${kafka.integration.aviation.emissions.response.transactional.id}")
    private String aviationEmissionsResponseTransactionalId;

    @Value("${kafka.integration.maritime.emissions.response.transactional.id}")
    private String maritimeEmissionsResponseTransactionalId;

    @Value("${kafka.integration.account.opening.response.transactional.id}")
    private String accountOpeningResponseTransactionalId;

    @Value("${kafka.integration.set.operator.request.transactional.id}")
    private String setOperatorIdRequestTransactionalId;

    @Value("${kafka.integration.exemption.response.transactional.id}")
    private String exemptionResponseTransactionalId;

    @Value("${kafka.integration.withhold.response.transactional.id}")
    private String withholdResponseTransactionalId;

    @Value("${kafka.integration.installation.emissions.response.topic}")
    private String installationEmissionsResponseTopic;

    @Value("${kafka.integration.maritime.emissions.response.topic}")
    private String maritimeEmissionsResponseTopic;

    @Value("${kafka.integration.aviation.emissions.response.topic}")
    private String aviationEmissionsResponseTopic;

    @Value("${kafka.integration.account.updating.request.consumer.group-id}")
    private String accountUpdatingRequestConsumerGroup;

    @Value("${kafka.integration.account.updating.response.consumer.group-id}")
    private String accountUpdatingResponseConsumerGroup;

    @Value("${kafka.integration.account.updating.response.transactional.id}")
    private String accountUpdatingResponseTransactionalId;

    @Value("${kafka.integration.mets.contacts.request.consumer.group-id}")
    private String metsContactsRequestConsumerGroup;

    @Value("${kafka.integration.mets.contacts.response.consumer.group-id}")
    private String metsContactsResponseConsumerGroup;

    @Value("${kafka.integration.mets.contacts.response.transactional.id}")
    private String metsContactsResponseTransactionalId;

    @Value("${kafka.integration.regulator.notice.request.consumer.group-id}")
    private String regulatorNoticeRequestConsumerGroup;

    @Value("${kafka.integration.regulator.notice.response.consumer.group-id}")
    private String regulatorNoticeResponseConsumerGroup;

    @Value("${kafka.integration.regulator.notice.response.transactional.id}")
    private String regulatorNoticeResponseTransactionalId;

    private final SharedKafkaConfig integrationKafkaAuthenticationConfig;

    private final IntegrationKafkaDeadLetterConfiguration integrationKafkaDeadLetterConfiguration;

    @ConditionalOnProperty(name = {"kafka.integration.enabled", "kafka.integration.emissions.enabled", "kafka.integration.installation.emissions.enabled"}, havingValue = "true")
    @Bean("installationEmissionsConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, AccountEmissionsUpdateEvent> installationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AccountEmissionsUpdateEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(integrationKafkaAuthenticationConfig.consumerFactory(AccountEmissionsUpdateEvent.class, installationEmissionsConsumerGroup));
        integrationKafkaDeadLetterConfiguration.setupErrorHandler(factory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    @ConditionalOnProperty(name = {"kafka.integration.enabled", "kafka.integration.emissions.enabled", "kafka.integration.aviation.emissions.enabled"}, havingValue = "true")
    @Bean("aviationEmissionsConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, AccountEmissionsUpdateEvent> aviationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AccountEmissionsUpdateEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(integrationKafkaAuthenticationConfig.consumerFactory(AccountEmissionsUpdateEvent.class, aviationEmissionsConsumerGroup));
        integrationKafkaDeadLetterConfiguration.setupErrorHandler(factory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    @ConditionalOnProperty(name = {"kafka.integration.enabled", "kafka.integration.emissions.enabled", "kafka.integration.maritime.emissions.enabled"}, havingValue = "true")
    @Bean("maritimeEmissionsConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, AccountEmissionsUpdateEvent> maritimeKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AccountEmissionsUpdateEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(integrationKafkaAuthenticationConfig.consumerFactory(AccountEmissionsUpdateEvent.class, maritimeEmissionsConsumerGroup));
        integrationKafkaDeadLetterConfiguration.setupErrorHandler(factory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    @Bean("outcomeConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, AccountEmissionsUpdateEventOutcome> outcomeKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AccountEmissionsUpdateEventOutcome> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(integrationKafkaAuthenticationConfig.consumerFactory(AccountEmissionsUpdateEventOutcome.class, outcomeConsumerGroup));
        integrationKafkaDeadLetterConfiguration.setupErrorHandler(factory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    @Bean("accountOpeningOutcomeConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, AccountOpeningEventOutcome> accountOpeningOutcomeConsumerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AccountOpeningEventOutcome> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(integrationKafkaAuthenticationConfig.consumerFactory(AccountOpeningEventOutcome.class, accountOpeningResponseConsumerGroup));
        integrationKafkaDeadLetterConfiguration.setupErrorHandler(factory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    @Bean("accountOpeningConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, AccountOpeningEvent> accountOpeningConsumerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AccountOpeningEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(integrationKafkaAuthenticationConfig.consumerFactory(AccountOpeningEvent.class, accountOpeningRequestConsumerGroup));
        integrationKafkaDeadLetterConfiguration.setupErrorHandler(factory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    @Bean("operatorSetIdOutcomeConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, OperatorUpdateEventOutcome> operatorSetIdOutcomeConsumerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OperatorUpdateEventOutcome> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(integrationKafkaAuthenticationConfig.consumerFactory(OperatorUpdateEventOutcome.class, setOperatorResponseConsumerGroup));
        integrationKafkaDeadLetterConfiguration.setupErrorHandler(factory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    // ---- Update account -------------------------//
    @ConditionalOnProperty(
            name = "kafka.integration.aviation.account.updating.enabled",
            havingValue = "true"
    )
    @Bean("aviationUpdatingConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, AccountUpdatingEvent> aviationUpdatingConsumerFactory() {
        return createUpdatingFactory(accountUpdatingRequestConsumerGroup);
    }

    @ConditionalOnProperty(
            name = "kafka.integration.installation.account.updating.enabled",
            havingValue = "true"
    )
    @Bean("installationUpdatingConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, AccountUpdatingEvent> installationUpdatingConsumerFactory() {
        return createUpdatingFactory(accountUpdatingRequestConsumerGroup);
    }

    @ConditionalOnProperty(
            name = "kafka.integration.maritime.account.updating.enabled",
            havingValue = "true"
    )
    @Bean("maritimeUpdatingConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, AccountUpdatingEvent> maritimeUpdatingConsumerFactory() {
        return createUpdatingFactory(accountUpdatingRequestConsumerGroup);
    }

    @ConditionalOnProperty(
            name = {
                    "kafka.integration.enabled"
            },
            havingValue = "true"
    )
    @Bean("accountUpdatingOutcomeConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, AccountUpdatingEventOutcome> accountUpdatingOutcomeConsumerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AccountUpdatingEventOutcome> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(integrationKafkaAuthenticationConfig.consumerFactory(AccountUpdatingEventOutcome.class, accountUpdatingResponseConsumerGroup));
        integrationKafkaDeadLetterConfiguration.setupErrorHandler(factory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    // ---- Regulator Notices -------------------------//
    @ConditionalOnProperty(
            name = "kafka.integration.aviation.regulator.notice.enabled",
            havingValue = "true"
    )
    @Bean("aviationRegulatorNoticeConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, RegulatorNoticeEvent> aviationRegulatorNoticeConsumerFactory() {
        return createRegulatorNoticeFactory(regulatorNoticeRequestConsumerGroup);
    }

    @ConditionalOnProperty(
            name = "kafka.integration.installation.regulator.notice.enabled",
            havingValue = "true"
    )
    @Bean("installationRegulatorNoticeConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, RegulatorNoticeEvent> installationRegulatorNoticeConsumerFactory() {
        return createRegulatorNoticeFactory(regulatorNoticeRequestConsumerGroup);
    }

    @ConditionalOnProperty(
            name = "kafka.integration.maritime.regulator.notice.enabled",
            havingValue = "true"
    )
    @Bean("maritimeRegulatorNoticeConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, RegulatorNoticeEvent> maritimeRegulatorNoticeConsumerFactory() {
        return createRegulatorNoticeFactory(regulatorNoticeRequestConsumerGroup);
    }

    @ConditionalOnProperty(
            name = {
                    "kafka.integration.enabled"
            },
            havingValue = "true"
    )
    @Bean("regulatorNoticeOutcomeConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, RegulatorNoticeEventOutcome> regulatorNoticeOutcomeConsumerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RegulatorNoticeEventOutcome> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(integrationKafkaAuthenticationConfig.consumerFactory(RegulatorNoticeEventOutcome.class, regulatorNoticeResponseConsumerGroup));
        integrationKafkaDeadLetterConfiguration.setupErrorHandler(factory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    @ConditionalOnProperty(
            name = {
                    "kafka.integration.enabled"
            },
            havingValue = "true"
    )
    @Bean("accountUpdatingOutcomeKafkaTemplate")
    public KafkaTemplate<String, AccountUpdatingEventOutcome> accountUpdatingOutcomeKafkaTemplate() {
        return integrationKafkaAuthenticationConfig.getKafkaTemplate(accountUpdatingResponseTransactionalId, null);
    }

    // ---- Mets contacts -------------------------//
    @ConditionalOnProperty(
            name = "kafka.integration.aviation.mets.contacts.enabled",
            havingValue = "true"
    )
    @Bean("aviationMetsContactsConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, MetsContactsEvent> aviationMetsContactsConsumerFactory() {
        return createMetsContactsFactory(metsContactsRequestConsumerGroup);
    }

    @ConditionalOnProperty(
            name = "kafka.integration.installation.mets.contacts.enabled",
            havingValue = "true"
    )
    @Bean("installationMetsContactsConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, MetsContactsEvent> installationMetsContactsConsumerFactory() {
        return createMetsContactsFactory(metsContactsRequestConsumerGroup);
    }

    @ConditionalOnProperty(
            name = "kafka.integration.maritime.mets.contacts.enabled",
            havingValue = "true"
    )
    @Bean("maritimeMetsContactsConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, MetsContactsEvent> maritimeMetsContactsConsumerFactory() {
        return createMetsContactsFactory(metsContactsRequestConsumerGroup);
    }

    @ConditionalOnProperty(
            name = {
                    "kafka.integration.enabled"
            },
            havingValue = "true"
    )
    @Bean("metsContactsOutcomeConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, MetsContactsEventOutcome> metsContactsOutcomeConsumerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MetsContactsEventOutcome> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(integrationKafkaAuthenticationConfig.consumerFactory(MetsContactsEventOutcome.class, metsContactsResponseConsumerGroup));
        integrationKafkaDeadLetterConfiguration.setupErrorHandler(factory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    @Bean("exemptionOutcomeConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, AccountExemptionUpdateEventOutcome> exemptionOutcomeConsumerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AccountExemptionUpdateEventOutcome> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(integrationKafkaAuthenticationConfig.consumerFactory(AccountExemptionUpdateEventOutcome.class, exemptionRequestConsumerGroup));
        integrationKafkaDeadLetterConfiguration.setupErrorHandler(factory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    @Bean("exemptionConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, AccountExemptionUpdateEvent> exemptionConsumerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AccountExemptionUpdateEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(integrationKafkaAuthenticationConfig.consumerFactory(AccountExemptionUpdateEvent.class, exemptionRequestConsumerGroup));
        integrationKafkaDeadLetterConfiguration.setupErrorHandler(factory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    @Bean("withholdOutcomeConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, AccountWithholdUpdateEventOutcome> withholdOutcomeConsumerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AccountWithholdUpdateEventOutcome> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(integrationKafkaAuthenticationConfig.consumerFactory(AccountWithholdUpdateEventOutcome.class, withholdRequestConsumerGroup));
        integrationKafkaDeadLetterConfiguration.setupErrorHandler(factory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    @Bean("withholdConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, AccountWithholdUpdateEvent> withholdConsumerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AccountWithholdUpdateEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(integrationKafkaAuthenticationConfig.consumerFactory(AccountWithholdUpdateEvent.class, withholdRequestConsumerGroup));
        integrationKafkaDeadLetterConfiguration.setupErrorHandler(factory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    @ConditionalOnProperty(
            name = {
                    "kafka.integration.enabled"
            },
            havingValue = "true"
    )
    @Bean("metsContactsOutcomeKafkaTemplate")
    public KafkaTemplate<String, MetsContactsEventOutcome> metsContactsOutcomeKafkaTemplate() {
        return integrationKafkaAuthenticationConfig.getKafkaTemplate(metsContactsResponseTransactionalId, null);
    }

    @ConditionalOnProperty(name = {"kafka.integration.enabled", "kafka.integration.emissions.enabled", "kafka.integration.aviation.emissions.enabled"}, havingValue = "true")
    @Bean("aviationEmissionsOutcomeKafkaTemplate")
    public KafkaTemplate<String, AccountEmissionsUpdateEventOutcome> aviationEmissionsOutcomeKafkaTemplate() {
        return integrationKafkaAuthenticationConfig.getKafkaTemplate(aviationEmissionsResponseTransactionalId, aviationEmissionsResponseTopic);
    }

    @ConditionalOnProperty(name = {"kafka.integration.enabled", "kafka.integration.emissions.enabled", "kafka.integration.maritime.emissions.enabled"}, havingValue = "true")
    @Bean("maritimeEmissionsOutcomeKafkaTemplate")
    public KafkaTemplate<String, AccountEmissionsUpdateEventOutcome> maritimeEmissionsOutcomeKafkaTemplate() {
        return integrationKafkaAuthenticationConfig.getKafkaTemplate(maritimeEmissionsResponseTransactionalId, maritimeEmissionsResponseTopic);
    }

    @ConditionalOnProperty(name = {"kafka.integration.enabled", "kafka.integration.emissions.enabled", "kafka.integration.installation.emissions.enabled"}, havingValue = "true")
    @Bean("installationEmissionsOutcomeKafkaTemplate")
    public KafkaTemplate<String, AccountEmissionsUpdateEventOutcome> installationEmissionsOutcomeKafkaTemplate() {
        return integrationKafkaAuthenticationConfig.getKafkaTemplate(installationEmissionsResponseTransactionalId, installationEmissionsResponseTopic);
    }

    @Bean("accountOpeningOutcomeKafkaTemplate")
    public KafkaTemplate<String, AccountOpeningEventOutcome> accountOpeningOutcomeKafkaTemplate() {
        return integrationKafkaAuthenticationConfig.getKafkaTemplate(accountOpeningResponseTransactionalId, null);
    }

    @Bean("operatorIdKafkaTemplate")
    public KafkaTemplate<String, OperatorUpdateEvent> operatorIdKafkaTemplate() {
        return integrationKafkaAuthenticationConfig.getKafkaTemplate(setOperatorIdRequestTransactionalId, null);
    }

    @Bean("exemptionOutcomeKafkaTemplate")
    public KafkaTemplate<String, AccountExemptionUpdateEventOutcome> exemptionOutcomeKafkaTemplate() {
        return integrationKafkaAuthenticationConfig.getKafkaTemplate(exemptionResponseTransactionalId, null);
    }

    @ConditionalOnProperty(
            name = {
                    "kafka.integration.enabled"
            },
            havingValue = "true"
    )
    @Bean("regulatorNoticeOutcomeKafkaTemplate")
    public KafkaTemplate<String, RegulatorNoticeEventOutcome> regulatorNoticeOutcomeKafkaTemplate() {
        return integrationKafkaAuthenticationConfig.getKafkaTemplate(regulatorNoticeResponseTransactionalId, null);
    }

    @Bean("withholdOutcomeKafkaTemplate")
    public KafkaTemplate<String, AccountWithholdUpdateEventOutcome> withholdOutcomeKafkaTemplate() {
        return integrationKafkaAuthenticationConfig.getKafkaTemplate(withholdResponseTransactionalId, null);
    }

    private ConcurrentKafkaListenerContainerFactory<String, AccountUpdatingEvent> createUpdatingFactory(String groupId) {
        ConcurrentKafkaListenerContainerFactory<String, AccountUpdatingEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(integrationKafkaAuthenticationConfig.consumerFactory(AccountUpdatingEvent.class, groupId));
        integrationKafkaDeadLetterConfiguration.setupErrorHandler(factory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    private ConcurrentKafkaListenerContainerFactory<String, MetsContactsEvent> createMetsContactsFactory(String groupId) {
        ConcurrentKafkaListenerContainerFactory<String, MetsContactsEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(integrationKafkaAuthenticationConfig.consumerFactory(MetsContactsEvent.class, groupId));
        integrationKafkaDeadLetterConfiguration.setupErrorHandler(factory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    private ConcurrentKafkaListenerContainerFactory<String, RegulatorNoticeEvent> createRegulatorNoticeFactory(String groupId) {
        ConcurrentKafkaListenerContainerFactory<String, RegulatorNoticeEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(integrationKafkaAuthenticationConfig.consumerFactory(RegulatorNoticeEvent.class, groupId));
        integrationKafkaDeadLetterConfiguration.setupErrorHandler(factory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }
}
