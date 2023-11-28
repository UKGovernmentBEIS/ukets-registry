package uk.gov.ets.itl.webservices.client.messaging.listener;

import java.rmi.RemoteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.ets.itl.webservices.client.transactionlog.TransactionLogPort;
import uk.gov.ets.kp.webservices.shared.types.ReceiveAuditTrailRequest;
import uk.gov.ets.kp.webservices.shared.types.ReceiveTotalsRequest;
import uk.gov.ets.kp.webservices.shared.types.ReceiveUnitBlocksRequest;

@Service
@KafkaListener(topics = "itl.originating.reconciliation.out.topic")
public class ITLReconciliationListener {

    private static final Logger log = LoggerFactory.getLogger(ITLReconciliationListener.class);
    private final TransactionLogPort transactionLogPort;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public ITLReconciliationListener(TransactionLogPort transactionLogPort) {
        this.transactionLogPort = transactionLogPort;
    }

    /**
     * Calls the ITL SOAP endpoint for Receive Totals operation.
     */
    @KafkaHandler
    public void handleReceiveTotalsRequestMessage(uk.gov.ets.lib.commons.kyoto.types.ReceiveTotalsRequest request) {
        try {
            log.info("Sending Receive Totals request to ITL for reconciliation: {}",
                request.getReconciliationIdentifier());
            ReceiveTotalsRequest receiveTotalsRequest = objectMapper
                .readValue(objectMapper.writeValueAsString(request), ReceiveTotalsRequest.class);
            transactionLogPort.receiveTotals(receiveTotalsRequest);
        } catch (RemoteException | JsonProcessingException e) {
            // TODO is there any way we can handle this error?
            log.error("Receive Totals call failed", e);
        }
    }

    /**
     * Calls the ITL SOAP endpoint for Receive Unit Blocks operation.
     */
    @KafkaHandler
    public void handleReceiveUnitBlocksRequestMessage(uk.gov.ets.lib.commons.kyoto.types.ReceiveUnitBlocksRequest request) {
        try {
            log.info("Sending Receive Unit Blocks request to ITL for reconciliation: {}",
                request.getReconciliationIdentifier());
            ReceiveUnitBlocksRequest receiveUnitBlocksRequest = objectMapper
                .readValue(objectMapper.writeValueAsString(request), ReceiveUnitBlocksRequest.class);
            transactionLogPort.receiveUnitBlocks(receiveUnitBlocksRequest);
        } catch (RemoteException | JsonProcessingException e) {
            // TODO is there any way we can handle this error?
            log.error("Receive Unit Blocks call failed", e);
        }
    }
    
    /**
     * Calls the ITL SOAP endpoint for Receive AuditTrail operation.
     */
    @KafkaHandler
    public void handleReceiveAuditTrailRequestMessage(uk.gov.ets.lib.commons.kyoto.types.ReceiveAuditTrailRequest request) {
        try {
            log.info("Sending Receive Audit Trail request to ITL for reconciliation: {}",
                request.getReconciliationIdentifier());
            ReceiveAuditTrailRequest receiveAuditTrailRequest = objectMapper
                .readValue(objectMapper.writeValueAsString(request), ReceiveAuditTrailRequest.class);
            transactionLogPort.receiveAuditTrail(receiveAuditTrailRequest);
        } catch (RemoteException | JsonProcessingException e) {
            log.error("Receive AuditTrail call failed", e);
        }
    }
}
