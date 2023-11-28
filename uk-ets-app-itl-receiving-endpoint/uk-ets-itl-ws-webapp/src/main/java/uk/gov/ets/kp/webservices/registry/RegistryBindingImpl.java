/**
 * RegistryBindingImpl.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.gov.ets.kp.webservices.registry;

import java.rmi.RemoteException;
import org.springframework.remoting.jaxrpc.ServletEndpointSupport;
import uk.gov.ets.kp.webservices.shared.types.ITLNoticeRequest;
import uk.gov.ets.kp.webservices.shared.types.ITLNoticeResponse;
import uk.gov.ets.kp.webservices.shared.types.InitiateReconciliationRequest;
import uk.gov.ets.kp.webservices.shared.types.InitiateReconciliationResponse;
import uk.gov.ets.kp.webservices.shared.types.MessageRequest;
import uk.gov.ets.kp.webservices.shared.types.MessageResponse;
import uk.gov.ets.kp.webservices.shared.types.NotificationRequest;
import uk.gov.ets.kp.webservices.shared.types.NotificationResponse;
import uk.gov.ets.kp.webservices.shared.types.ProposalRequest;
import uk.gov.ets.kp.webservices.shared.types.ProposalResponse;
import uk.gov.ets.kp.webservices.shared.types.ProvideAuditTrailRequest;
import uk.gov.ets.kp.webservices.shared.types.ProvideAuditTrailResponse;
import uk.gov.ets.kp.webservices.shared.types.ProvideTimeRequest;
import uk.gov.ets.kp.webservices.shared.types.ProvideTimeResponse;
import uk.gov.ets.kp.webservices.shared.types.ProvideTotalsRequest;
import uk.gov.ets.kp.webservices.shared.types.ProvideTotalsResponse;
import uk.gov.ets.kp.webservices.shared.types.ProvideUnitBlocksRequest;
import uk.gov.ets.kp.webservices.shared.types.ProvideUnitBlocksResponse;
import uk.gov.ets.kp.webservices.shared.types.ReconciliationResultRequest;
import uk.gov.ets.kp.webservices.shared.types.ReconciliationResultResponse;

/**
 * Registry endpoint for accepting messages coming from ITL and delegates these messages to the {@link RegistryService} Spring bean to process them.
 * Note:This class is not managed by Spring.To facilitate the Spring integration this class extends ServletEndpointSupport
 *
 * @author P35036
 */
public class RegistryBindingImpl extends ServletEndpointSupport implements RegistryPort {

    @Override
    public InitiateReconciliationResponse initiateReconciliation(
        InitiateReconciliationRequest initiateReconciliationRequest) throws RemoteException {
        return getDelegateService().initiateReconciliation(initiateReconciliationRequest);
    }

    @Override
    public ProvideTimeResponse provideTime(ProvideTimeRequest provideTimeRequest) throws RemoteException {
        return getDelegateService().provideTime(provideTimeRequest);
    }

    @Override
    public MessageResponse acceptMessage(MessageRequest acceptMessageRequest) throws RemoteException {
        return getDelegateService().acceptMessage(acceptMessageRequest);
    }

    @Override
    public ProposalResponse acceptProposal(ProposalRequest acceptProposalRequest) throws RemoteException {
        return getDelegateService().acceptProposal(acceptProposalRequest);
    }

    @Override
    public NotificationResponse acceptNotification(NotificationRequest acceptNotificationRequest)
        throws RemoteException {
        return getDelegateService().acceptNotification(acceptNotificationRequest);
    }

    @Override
    public ITLNoticeResponse acceptITLNotice(ITLNoticeRequest acceptITLNoticeRequest) throws RemoteException {
        return getDelegateService().acceptITLNotice(acceptITLNoticeRequest);
    }

    @Override
    public ReconciliationResultResponse receiveReconciliationResult(
        ReconciliationResultRequest receiveReconciliationResultRequest) throws RemoteException {
        return getDelegateService().receiveReconciliationResult(receiveReconciliationResultRequest);
    }

    @Override
    public ProvideAuditTrailResponse provideAuditTrail(ProvideAuditTrailRequest provideAuditTrailRequest)
        throws RemoteException {
        return getDelegateService().provideAuditTrail(provideAuditTrailRequest);
    }

    @Override
    public ProvideTotalsResponse provideTotals(ProvideTotalsRequest provideTotalsRequest) throws RemoteException {
        return getDelegateService().provideTotals(provideTotalsRequest);
    }

    @Override
    public ProvideUnitBlocksResponse provideUnitBlocks(ProvideUnitBlocksRequest provideUnitBlocksRequest)
        throws RemoteException {
        return getDelegateService().provideUnitBlocks(provideUnitBlocksRequest);
    }

    private RegistryService getDelegateService() {
        return getWebApplicationContext().getBean(RegistryService.class);
    }
}
