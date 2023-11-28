package gov.uk.ets.registry.api.transaction.signing.migration;

import static gov.uk.ets.registry.api.transaction.domain.type.TransactionType.AllocateAllowances;
import static gov.uk.ets.registry.api.transaction.domain.type.TransactionType.BalanceInstallationTransferAllowances;

import gov.uk.ets.commons.logging.MDCWrapper;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.migration.Migrator;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.data.SignatureInfo;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import gov.uk.ets.registry.api.transaction.service.TransactionManagementService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Log4j2
public class TransactionSigningMigrator implements Migrator {

    @Value("${signing.sign-no-otp-endpoint}")
    private String signNoOtpEndpoint;
    private final MigratorHistoryRepository migratorHistoryRepository;
    private final RestTemplate restTemplate;
    private final TransactionManagementService transactionManagementService;
    private final TransactionRepository transactionRepository;
    private final Mapper mapper;

    @Transactional
    public void migrate() {
        log.info("Starting migration of transaction signatures");
        List<MigratorHistory> migratorHistories = migratorHistoryRepository.findByMigratorName(
            MigratorName.TRANSACTION_SIGNING_MIGRATOR);
        if (CollectionUtils.isNotEmpty(migratorHistories) ) {
            log.info("[Migration of transaction signatures], has already performed previously, skipping.");
            return;
        }
        List<Transaction> transactions = transactionRepository.findByTypeNotInAndSignatureIsNull(
            List.of(BalanceInstallationTransferAllowances, AllocateAllowances));
        if (transactions == null) {
            log.debug("Transactions do not get retrieved!");
            return;
        }
        transactions.forEach(t -> {
            TransactionSummary transactionSummary = transactionManagementService.constructTransactionSummary(t);
            String data = mapper.convertToJson(transactionSummary);
            SignatureInfo signatureInfo = createAndSendSigningRequest(data);
            t.setSignature(signatureInfo != null ? signatureInfo.getSignature() : null);
            t.setSignedData(data);
            transactionRepository.save(t);
        });
        MigratorHistory migratorHistory = new MigratorHistory();
        migratorHistory.setMigratorName(MigratorName.TRANSACTION_SIGNING_MIGRATOR);
        migratorHistory.setCreatedOn(LocalDateTime.now());
        migratorHistoryRepository.save(migratorHistory);
        log.info("Migration of transaction signatures completed");
    }

    private SignatureInfo createAndSendSigningRequest(String toBeSignedData) {

        SignatureInfo signatureInfo = SignatureInfo.builder()
                                                   .data(toBeSignedData)
                                                   .build();
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set("X-Request-ID", MDC.getMDCAdapter()
                .get(MDCWrapper.Attr.INTERACTION_IDENTIFIER.name().toLowerCase()));

        HttpEntity<SignatureInfo> request = new HttpEntity<>(signatureInfo, headers);
        ResponseEntity<SignatureInfo> response = restTemplate.postForEntity(signNoOtpEndpoint, request,
                                                                            SignatureInfo.class);
        return response.getBody();
    }
}
