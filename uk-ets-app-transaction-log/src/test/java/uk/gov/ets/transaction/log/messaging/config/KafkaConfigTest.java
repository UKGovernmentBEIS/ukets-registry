package uk.gov.ets.transaction.log.messaging.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;

import uk.ets.lib.commons.kafkaconfig.SharedKafkaConfig;
import uk.gov.ets.transaction.log.messaging.types.AccountNotification;
import uk.gov.ets.transaction.log.messaging.types.ReconciliationSummary;
import uk.gov.ets.transaction.log.messaging.types.TransactionNotification;

class KafkaConfigTest {

    private final SharedKafkaConfig sharedKafkaConfig = mock(SharedKafkaConfig.class);

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withBean(SharedKafkaConfig.class, () -> sharedKafkaConfig)
                    .withBean(KafkaConfig.class)
                    .withPropertyValues(
                            "kafka.account-uktl.answer.transactional.id=tx-uk-ets-account-opening",
                            "kafka.transaction-uktl.answer.transactional.id=tx-uk-ets-transaction-answer",
                            "kafka.reconciliation-uktl.answer.transactional.id=tx-uk-ets-reconciliation-answer",
                            "kafka.transaction.log.consumer.group.id=transaction-log-messages-registry-group",
                            "kafka.max.age.millis=60000",
                            "kafka.integration.authentication.enabled=false"
                    );

    @Test
    void kafkaTemplatesShouldBeCreated() {
        when(sharedKafkaConfig.getKafkaTemplate(anyString(), any()))
                .thenReturn(mock(KafkaTemplate.class));

        contextRunner.run(context -> {
            assertThat(context).hasBean("accountOpeningKafkaTemplate");
            assertThat(context).hasBean("transactionKafkaTemplate");
            assertThat(context).hasBean("reconciliationKafkaTemplate");

            verify(sharedKafkaConfig).getKafkaTemplate("tx-uk-ets-account-opening", null);
            verify(sharedKafkaConfig).getKafkaTemplate("tx-uk-ets-transaction-answer", null);
            verify(sharedKafkaConfig).getKafkaTemplate("tx-uk-ets-reconciliation-answer", null);
        });
    }

    
    @Test
    void listenerFactoryShouldUseCorrectConsumerGroupId() {
        // Arrange
        var consumerFactoryMock = Mockito.mock(org.springframework.kafka.core.ConsumerFactory.class);

        when(sharedKafkaConfig.consumerFactory(any(), anyString()))
                .thenReturn(consumerFactoryMock);

        contextRunner.run(context -> {
            // Act: trigger bean creation
            context.getBean("accountNotificationKafkaListenerContainerFactory",
                    ConcurrentKafkaListenerContainerFactory.class);
            context.getBean("transactionNotificationKafkaListenerContainerFactory",
                    ConcurrentKafkaListenerContainerFactory.class);
            context.getBean("reconciliationKafkaListenerContainerFactory",
                    ConcurrentKafkaListenerContainerFactory.class);
            
            // Assert: verify group ID passed to consumerFactory(...)
            verify(sharedKafkaConfig).consumerFactory(
                    Mockito.eq(AccountNotification.class),
                    Mockito.endsWith("registry-group")
            );
            
            verify(sharedKafkaConfig).consumerFactory(
                    Mockito.eq(TransactionNotification.class),
                    Mockito.endsWith("registry-group")
            );
            
            verify(sharedKafkaConfig).consumerFactory(
                    Mockito.eq(ReconciliationSummary.class),
                    Mockito.endsWith("registry-group")
            );            
        });
    }

    
    @Test
    void listenerFactoriesShouldUseRecordAckMode() {
        when(sharedKafkaConfig.consumerFactory(any(), anyString()))
                .thenReturn(Mockito.mock(org.springframework.kafka.core.ConsumerFactory.class));

        contextRunner.run(context -> {
            ConcurrentKafkaListenerContainerFactory<String, AccountNotification> factory =
                    context.getBean("accountNotificationKafkaListenerContainerFactory",
                            ConcurrentKafkaListenerContainerFactory.class);

            assertThat(factory.getContainerProperties().getAckMode())
                    .isEqualTo(ContainerProperties.AckMode.RECORD);
        });
    }
}

