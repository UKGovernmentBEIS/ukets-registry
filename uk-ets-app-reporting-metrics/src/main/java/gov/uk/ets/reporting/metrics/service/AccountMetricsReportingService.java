package gov.uk.ets.reporting.metrics.service;

import gov.uk.ets.reporting.metrics.domain.AccountMetrics;
import gov.uk.ets.reporting.metrics.domain.ProcessedAccountMetricsEvent;
import gov.uk.ets.reporting.metrics.messaging.events.AbstractReportingMetricsEvent;
import gov.uk.ets.reporting.metrics.messaging.events.DynamicComplianceStatusCalculatedEvent;
import gov.uk.ets.reporting.metrics.messaging.events.EmissionsUpdatedEvent;
import gov.uk.ets.reporting.metrics.messaging.events.InstallationTransferEvent;
import gov.uk.ets.reporting.metrics.messaging.events.TransactionFinalisationEvent;
import gov.uk.ets.reporting.metrics.outbox.repository.AccountMetricsRepository;
import gov.uk.ets.reporting.metrics.outbox.repository.ProcessedAccountMetricsEventRepository;
import gov.uk.ets.reporting.metrics.types.TransactionStatus;
import gov.uk.ets.reporting.metrics.types.TransactionType;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Log4j2
public class AccountMetricsReportingService {

    private static final Long ZERO = 0L;
    private final AccountMetricsRepository accountMetricsRepository;
    private final ProcessedAccountMetricsEventRepository processedReportingMetricsEventRepository;

    @Transactional
    public void processAccountMetricEvent(AbstractReportingMetricsEvent event) {

        log.info("Received ReportingMetricsEvent {}", event);
        if (processedReportingMetricsEventRepository.findByEventId(event.getEventId()).isPresent()) {     
        	return;
        }
        AccountMetrics accountMetrics;

        
        switch (event.getType()) {
            case RECALCULATE_DYNAMIC_STATUS:
            	DynamicComplianceStatusCalculatedEvent dynamicComplianceStatusCalculatedEvent = (DynamicComplianceStatusCalculatedEvent) event;
                accountMetrics = accountMetricsRepository
                        .findByIdentifier(dynamicComplianceStatusCalculatedEvent.getAccountIdentifier())
                        .orElseGet(AccountMetrics::new);
                if (Optional.ofNullable(accountMetrics.getIdentifier()).isEmpty()) {
                    accountMetrics.setIdentifier(dynamicComplianceStatusCalculatedEvent.getAccountIdentifier());
                }
                accountMetrics.setDynamicComplianceStatus(dynamicComplianceStatusCalculatedEvent.getDynamicComplianceStatus());
                accountMetricsRepository.save(accountMetrics);
                break;
            case UPDATE_OF_VERIFIED_EMISSIONS:
                EmissionsUpdatedEvent emissionsUploadedEvent = (EmissionsUpdatedEvent) event;
                accountMetrics = accountMetricsRepository
                        .findByIdentifier(emissionsUploadedEvent.getAccountIdentifier())
                        .orElseGet(AccountMetrics::new);
                if (Optional.ofNullable(accountMetrics.getIdentifier()).isEmpty()) {
                    accountMetrics.setIdentifier(emissionsUploadedEvent.getAccountIdentifier());            	
                }
                long oldEmissionsValue = Optional.ofNullable(emissionsUploadedEvent.getOldEmissionsValue()).orElse(0L);
                long newEmissionsValue = Optional.ofNullable(emissionsUploadedEvent.getNewEmissionsValue()).orElse(0L);
                
                accountMetrics.setTotalVerifiedEmissions(Optional.ofNullable(accountMetrics.getTotalVerifiedEmissions()).orElse(ZERO) - oldEmissionsValue + newEmissionsValue);  
                accountMetrics.setSurrenderBalance(Optional.ofNullable(accountMetrics.getSurrenderBalance()).orElse(ZERO) + oldEmissionsValue - newEmissionsValue);  
                accountMetricsRepository.save(accountMetrics);
                break;
            case TRANSACTION_FINALISATION:
                TransactionFinalisationEvent transactionCompletedEvent = (TransactionFinalisationEvent) event;
                TransactionType transactionType = transactionCompletedEvent.getTransactionType();
                TransactionStatus transactionStatus = transactionCompletedEvent.getTransactionStatus();
                if (!TransactionStatus.COMPLETED.equals(transactionStatus)) {
                    break;
                }
                if (TransactionType.SurrenderAllowances.equals(transactionType)) {
                    accountMetrics = accountMetricsRepository
                          .findByIdentifier(transactionCompletedEvent.getTransferringAccountIdentifier())
                          .orElseGet(AccountMetrics::new);
                    if (Optional.ofNullable(accountMetrics.getIdentifier()).isEmpty()) {
                        accountMetrics.setIdentifier(transactionCompletedEvent.getTransferringAccountIdentifier());
                    }
                    accountMetrics.setQuantitySurrendered(Optional.ofNullable(accountMetrics.getQuantitySurrendered()).orElse(ZERO) + transactionCompletedEvent.getAmount());
                    accountMetrics.setSurrenderBalance(Optional.ofNullable(accountMetrics.getSurrenderBalance()).orElse(ZERO) + transactionCompletedEvent.getAmount());
                    accountMetrics = accountMetricsRepository.save(accountMetrics);
                } else if (TransactionType.ReverseSurrenderAllowances.equals(transactionType)) {
                    accountMetrics = accountMetricsRepository
                            .findByIdentifier(transactionCompletedEvent.getAcquiringAccountIdentifier())
                            .orElseGet(AccountMetrics::new);
                    if (Optional.ofNullable(accountMetrics.getIdentifier()).isEmpty()) {
                        accountMetrics.setIdentifier(transactionCompletedEvent.getAcquiringAccountIdentifier());
                    }
                    accountMetrics.setQuantityReversedSurrendered(Optional.ofNullable(accountMetrics.getQuantityReversedSurrendered()).orElse(ZERO) + transactionCompletedEvent.getAmount());
                    accountMetrics.setSurrenderBalance(Optional.ofNullable(accountMetrics.getSurrenderBalance()).orElse(ZERO) - transactionCompletedEvent.getAmount());
                    accountMetrics = accountMetricsRepository.save(accountMetrics);
                }
                break;
            case INSTALLATION_TRANSFER:
                InstallationTransferEvent installationTransferEvent = (InstallationTransferEvent) event;
                accountMetrics = accountMetricsRepository
                        .findByIdentifier(installationTransferEvent.getTransferringAccountIdentifier())
                        .orElseGet(AccountMetrics::new);
                //Copy the old values
                AccountMetrics acquiringAccountMetrics = new AccountMetrics();
                acquiringAccountMetrics.setIdentifier(installationTransferEvent.getAcquiringAccountIdentifier());
                acquiringAccountMetrics.setDynamicComplianceStatus(accountMetrics.getDynamicComplianceStatus());
                acquiringAccountMetrics.setQuantitySurrendered(accountMetrics.getQuantitySurrendered());
                acquiringAccountMetrics.setQuantityReversedSurrendered(accountMetrics.getQuantityReversedSurrendered());
                acquiringAccountMetrics.setTotalVerifiedEmissions(accountMetrics.getTotalVerifiedEmissions());
                acquiringAccountMetrics.setSurrenderBalance(accountMetrics.getSurrenderBalance());
                accountMetricsRepository.save(acquiringAccountMetrics);

                break; 
            default: throw new AssertionError("Unknown event type : " + event.getType());
        }
        saveProcessedMessage(event);
    }
    
    private void saveProcessedMessage(AbstractReportingMetricsEvent event) {
        ProcessedAccountMetricsEvent processedAccountMetricsEvent = new ProcessedAccountMetricsEvent();
        processedAccountMetricsEvent.setProcessedOn(LocalDateTime.now());
        processedAccountMetricsEvent.setEventId(event.getEventId());
        processedAccountMetricsEvent.setPayload(event);   	
        
        processedReportingMetricsEventRepository.save(processedAccountMetricsEvent);
    }
}
