/**
 * RegistryPort.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.gov.ets.kp.webservices.registry;

public interface RegistryPort extends java.rmi.Remote {
    public uk.gov.ets.kp.webservices.shared.types.InitiateReconciliationResponse initiateReconciliation(uk.gov.ets.kp.webservices.shared.types.InitiateReconciliationRequest initiateReconciliationRequest) throws java.rmi.RemoteException;
    public uk.gov.ets.kp.webservices.shared.types.ProvideTimeResponse provideTime(uk.gov.ets.kp.webservices.shared.types.ProvideTimeRequest provideTimeRequest) throws java.rmi.RemoteException;
    public uk.gov.ets.kp.webservices.shared.types.MessageResponse acceptMessage(uk.gov.ets.kp.webservices.shared.types.MessageRequest acceptMessageRequest) throws java.rmi.RemoteException;
    public uk.gov.ets.kp.webservices.shared.types.ProposalResponse acceptProposal(uk.gov.ets.kp.webservices.shared.types.ProposalRequest acceptProposalRequest) throws java.rmi.RemoteException;
    public uk.gov.ets.kp.webservices.shared.types.NotificationResponse acceptNotification(uk.gov.ets.kp.webservices.shared.types.NotificationRequest acceptNotificationRequest) throws java.rmi.RemoteException;
    public uk.gov.ets.kp.webservices.shared.types.ITLNoticeResponse acceptITLNotice(uk.gov.ets.kp.webservices.shared.types.ITLNoticeRequest acceptITLNoticeRequest) throws java.rmi.RemoteException;
    public uk.gov.ets.kp.webservices.shared.types.ReconciliationResultResponse receiveReconciliationResult(uk.gov.ets.kp.webservices.shared.types.ReconciliationResultRequest receiveReconciliationResultRequest) throws java.rmi.RemoteException;
    public uk.gov.ets.kp.webservices.shared.types.ProvideAuditTrailResponse provideAuditTrail(uk.gov.ets.kp.webservices.shared.types.ProvideAuditTrailRequest provideAuditTrailRequest) throws java.rmi.RemoteException;
    public uk.gov.ets.kp.webservices.shared.types.ProvideTotalsResponse provideTotals(uk.gov.ets.kp.webservices.shared.types.ProvideTotalsRequest provideTotalsRequest) throws java.rmi.RemoteException;
    public uk.gov.ets.kp.webservices.shared.types.ProvideUnitBlocksResponse provideUnitBlocks(uk.gov.ets.kp.webservices.shared.types.ProvideUnitBlocksRequest provideUnitBlocksRequest) throws java.rmi.RemoteException;
}
