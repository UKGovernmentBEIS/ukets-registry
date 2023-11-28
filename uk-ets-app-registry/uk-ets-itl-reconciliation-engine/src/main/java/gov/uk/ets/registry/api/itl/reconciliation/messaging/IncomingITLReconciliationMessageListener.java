package gov.uk.ets.registry.api.itl.reconciliation.messaging;

import gov.uk.ets.registry.api.itl.reconciliation.service.AuditTrailService;
import gov.uk.ets.registry.api.itl.reconciliation.service.ITLReconciliationProvideTotalsService;
import gov.uk.ets.registry.api.itl.reconciliation.service.ITLReconciliationProvideUnitBlocksService;
import gov.uk.ets.registry.api.itl.reconciliation.service.ITLReconciliationResultService;
import gov.uk.ets.registry.api.itl.reconciliation.service.InitiateITLReconciliationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.ets.lib.commons.kyoto.types.InitiateReconciliationRequest;
import uk.gov.ets.lib.commons.kyoto.types.ProvideAuditTrailRequest;
import uk.gov.ets.lib.commons.kyoto.types.ProvideTotalsRequest;
import uk.gov.ets.lib.commons.kyoto.types.ProvideUnitBlocksRequest;
import uk.gov.ets.lib.commons.kyoto.types.ReceiveAuditTrailRequest;
import uk.gov.ets.lib.commons.kyoto.types.ReceiveTotalsRequest;
import uk.gov.ets.lib.commons.kyoto.types.ReceiveUnitBlocksRequest;
import uk.gov.ets.lib.commons.kyoto.types.ReconciliationResultRequest;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Service for receiving Reconciliation related messages from ITL.
 *
 * @author P35036
 * @since v1.1
 */
@Service
@Log4j2
@KafkaListener(topics = "itl.originating.reconciliation.in.topic")
@RequiredArgsConstructor
public class IncomingITLReconciliationMessageListener {

    private final InitiateITLReconciliationService reconciliationService;
    private final ITLReconciliationProvideTotalsService provideTotalsService;
    private final OutgoingItlReconciliationMessageProducer reconciliationMessageProducer;
    private final AuditTrailService auditTrailService;
    private final ITLReconciliationResultService reconciliationResultService;
    private final ITLReconciliationProvideUnitBlocksService provideUnitBlocksService;


    /**
     * Handles the incoming message request.
     *
     * @param request The message request.
     */
    @KafkaHandler
    public void handleInitiateReconciliationRequest(InitiateReconciliationRequest request) {
        log.info("Received a Initiate Reconciliation Request from ITL: {}", request);
        reconciliationService.initiateReconciliation(request);
    }

    /**
     * Handles the incoming provide totals request.
     *
     * @param request The provide totals  request.
     */
    @KafkaHandler
    public void handleProvideTotalsRequest(ProvideTotalsRequest request) {
        log.info("Received a Provide Totals Request from ITL: {}", request);
        ReceiveTotalsRequest receiveTotalsRequest = provideTotalsService.provideTotals(request);
        reconciliationMessageProducer.sendTotalsRequest(receiveTotalsRequest);
    }

    /**
     * Handles the incoming ProvideUnitBlocksRequest.
     *
     * @param request The ProvideUnitBlocksRequest.
     */
    @KafkaHandler
    public void handleProvideUnitBlocksRequest(ProvideUnitBlocksRequest request) {
        log.info("Received a Provide UnitBlocks Request from ITL: {}", request);
        ReceiveUnitBlocksRequest unitBlocksRequest = provideUnitBlocksService.provideUnitBlocks(request);
        reconciliationMessageProducer.sendUnitBlocksRequest(unitBlocksRequest);
    }

    /**
     * Handles the incoming ReconciliationResultRequest.
     *
     * @param request The ReconciliationResultRequest.
     */
    @KafkaHandler
    public void handleReconciliationResultRequest(ReconciliationResultRequest request) {
        log.info("Received a Reconciliation Result Request from ITL: {}", request);
        reconciliationResultService.writeReconciliationResult(request);
    }

    /**
     * Handles the incoming ProvideAuditTrailRequest.
     *
     * @param request The ProvideAuditTrailRequest.
     */
    @KafkaHandler
    public void handleProvideAuditTrailRequest(ProvideAuditTrailRequest request) {
        log.info("Received a Provide AuditTrail Request from ITL: {}", request);
        ReceiveAuditTrailRequest receiveAuditTrailRequest = auditTrailService.provideAuditTrail(request);
        reconciliationMessageProducer.sendAuditTrailRequest(receiveAuditTrailRequest);
    }

}
