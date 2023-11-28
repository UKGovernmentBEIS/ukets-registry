package uk.gov.ets.transaction.log.service.reconciliation;

import static java.util.stream.Collectors.toList;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.ets.transaction.log.domain.Reconciliation;
import uk.gov.ets.transaction.log.domain.type.ReconciliationStatus;
import uk.gov.ets.transaction.log.messaging.types.ReconciliationEntrySummary;
import uk.gov.ets.transaction.log.messaging.types.ReconciliationFailedEntrySummary;
import uk.gov.ets.transaction.log.messaging.types.ReconciliationSummary;
import uk.gov.ets.transaction.log.repository.ReconciliationRepository;
import uk.gov.ets.transaction.log.repository.TransactionRepository;
import uk.gov.ets.transaction.log.repository.UnitBlockRepository;

@Service
@Log4j2
@RequiredArgsConstructor
public class ReconciliationService {

    @Value("${kafka.reconciliation-uktl.answer.topic:txlog.originating.reconciliation.answer.topic}")
    private String reconciliationAnswerTopic;
    private final UnitBlockRepository unitBlockRepository;
    private final ReconciliationRepository reconciliationRepository;
    private final TransactionRepository transactionRepository;
    private final ObjectMapper objectMapper;

    private final KafkaTemplate<String, ReconciliationSummary> kafkaTemplate;

    @Transactional
    public void performReconciliation(ReconciliationSummary registryReconciliationSummary) {
        log.info("the reconciliation overview from registry is: {}", registryReconciliationSummary);

        List<ReconciliationEntrySummary> ukTlReconciliationEntries = calculateReconciliationEntries();
        log.info("reconciliation entries found in transaction log: {}", ukTlReconciliationEntries);

        ReconciliationEntriesComparator entriesComparator = ReconciliationEntriesComparator.builder()
            .withRegistryReconciliationEntries(registryReconciliationSummary.getEntries())
            .withUkTlReconciliationEntries(ukTlReconciliationEntries)
            .build();
        List<ReconciliationFailedEntrySummary> failedEntries = entriesComparator.compare();
        log.info("reconciliation entries failed: {}", failedEntries);

        ReconciliationStatus reconciliationStatus =
            failedEntries.isEmpty() ? ReconciliationStatus.COMPLETED : ReconciliationStatus.INCONSISTENT;

        ReconciliationSummary summary = new ReconciliationSummary();
        summary.setFailedEntries(failedEntries);
        summary.setStatus(reconciliationStatus);
        summary.setIdentifier(registryReconciliationSummary.getIdentifier());

        saveReconciliation(failedEntries, reconciliationStatus, summary);

        kafkaTemplate.send(reconciliationAnswerTopic, summary);
    }


    /**
     * Retrieves latest reconciliation date.
     * <br>
     * Retrieves account identifiers that were involved in transactions after this date.
     * <br>
     * Sums up blocks per account identifiers for these accounts.
     *
     * @return the {@link ReconciliationEntrySummary}s for the running reconciliation instance.
     */
    List<ReconciliationEntrySummary> calculateReconciliationEntries() {
        return unitBlockRepository.retrieveEntriesForAllAccounts();
    }


    void saveReconciliation(List<ReconciliationFailedEntrySummary> failedEntries,
                            ReconciliationStatus reconciliationStatus, ReconciliationSummary summary) {
        Reconciliation reconciliation = new Reconciliation();
        reconciliation.setStatus(reconciliationStatus);
        reconciliation.setIdentifier(summary.getIdentifier());
        reconciliation.setFailedEntries(
            failedEntries.stream()
                .map(e -> e.toEntity(reconciliation))
                .collect(toList()));
        reconciliation.setCreated(new Date());
        try {
            reconciliation.setData(objectMapper.writeValueAsString(summary));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
        reconciliationRepository.save(reconciliation);
    }
}
