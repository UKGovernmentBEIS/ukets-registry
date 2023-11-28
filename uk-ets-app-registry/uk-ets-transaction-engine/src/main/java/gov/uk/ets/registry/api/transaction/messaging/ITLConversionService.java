package gov.uk.ets.registry.api.transaction.messaging;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckError;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckErrorResult;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionBlock;
import gov.uk.ets.registry.api.transaction.domain.data.ItlNotificationSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.ets.lib.commons.kyoto.types.EvaluationResult;
import uk.gov.ets.lib.commons.kyoto.types.NotificationRequest;
import uk.gov.ets.lib.commons.kyoto.types.ProposalRequest;
import uk.gov.ets.lib.commons.kyoto.types.ProposalTransaction;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Service for preparing messages for ITL communication.
 */
@Log4j2
@Service
@AllArgsConstructor
public class ITLConversionService {

    /**
     * Conversion service for unit blocks.
     */
    private ITLBlockConversionService itlBlockConversionService;

    /**
     * Prepares an "Accept Proposal" message.
     *
     * @param transaction The transaction.
     * @param blocks      The transaction blocks.
     * @return a proposal request.
     */
    public ProposalRequest prepareAcceptProposal(Transaction transaction, List<TransactionBlock> blocks) {
        if (transaction == null) {
            throw new IllegalArgumentException("No transaction provided");
        }

        ProposalRequest request = new ProposalRequest();
        request.setFrom(transaction.getTransferringAccount().getAccountRegistryCode());
        request.setTo(Constants.ITL_TO);
        request.setMajorVersion(Constants.ITL_MAJOR_VERSION);
        request.setMinorVersion(Constants.ITL_MINOR_VERSION);

        ProposalTransaction proposal = new ProposalTransaction();
        request.setProposedTransaction(proposal);

        // Main transaction fields
        proposal.setTransactionIdentifier(transaction.getIdentifier());
        TransactionType type = transaction.getType();
        proposal.setTransactionType(type.getPrimaryCode());
        // Supplementary Transaction type should be null for Kyoto Transactions
        if (!type.isKyoto()) {
            proposal.setSuppTransactionType(type.getSupplementaryCode());
        }
        proposal.setNotificationIdentifier(transaction.getNotificationIdentifier());

        // Transferring account
        proposal.setTransferringRegistryAccountIdentifier(transaction.getTransferringAccount().getAccountIdentifier());
        proposal.setTransferringRegistryAccountType(transaction.getTransferringAccount().getAccountType().getCode());
        proposal.setTransferringRegistryCode(transaction.getTransferringAccount().getAccountRegistryCode());

        // Acquiring account
        proposal.setAcquiringRegistryAccountIdentifier(transaction.getAcquiringAccount().getAccountIdentifier());
        proposal.setAcquiringRegistryAccountType(transaction.getAcquiringAccount().getAccountType().getCode());
        proposal.setAcquiringRegistryCode(transaction.getAcquiringAccount().getAccountRegistryCode());

        proposal.setProposalUnitBlocks(itlBlockConversionService.convert(transaction, blocks));

        return request;
    }


    /**
     * Parses an "Accept Proposal" message.
     *
     * @return a proposal request.
     */
    public TransactionSummary parseAcceptProposal(ProposalRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("No proposal request provided");
        }

        if (!hasValidMetaData(request)) {
            log.info("The incoming request was ignored due to invalid meta-data {}", request);
            return null;
        }

        TransactionSummary transaction = new TransactionSummary();
        ProposalTransaction proposal = request.getProposedTransaction();

        // Main transaction fields
        transaction.setIdentifier(proposal.getTransactionIdentifier());
        transaction.setType(TransactionType.of(proposal.getTransactionType(), Optional.ofNullable(proposal.getSuppTransactionType()).orElse(0)));
        if(Optional.ofNullable(proposal.getNotificationIdentifier()).isPresent()) {
            transaction.setItlNotification(ItlNotificationSummary.builder().notificationIdentifier(proposal.getNotificationIdentifier()).build());	
        }

        // Transferring account
        transaction.setTransferringRegistryCode(proposal.getTransferringRegistryCode());
        transaction.setTransferringAccountIdentifier(proposal.getTransferringRegistryAccountIdentifier());
        transaction.setTransferringAccountType(KyotoAccountType.parse(proposal.getTransferringRegistryAccountType()));
        transaction.setTransferringAccountFullIdentifier(String.format("%s-%s-%s",
            proposal.getTransferringRegistryCode(),
            proposal.getTransferringRegistryAccountType(),
            proposal.getTransferringRegistryAccountIdentifier()));

        // Acquiring account
        transaction.setAcquiringAccountRegistryCode(proposal.getAcquiringRegistryCode());
        transaction.setAcquiringAccountIdentifier(proposal.getAcquiringRegistryAccountIdentifier());
        transaction.setAcquiringAccountType(KyotoAccountType.parse(proposal.getAcquiringRegistryAccountType()));
        transaction.setAcquiringAccountFullIdentifier(String.format("%s-%s-%s",
            proposal.getAcquiringRegistryCode(),
            proposal.getAcquiringRegistryAccountType(),
            proposal.getAcquiringRegistryAccountIdentifier()));

        transaction.setBlocks(itlBlockConversionService.convert(proposal.getProposalUnitBlocks()));

        return transaction;
    }

    /**
     * Checks whether this request has valid meta data.
     *
     * @param request The request.
     * @return false/true.
     */
    private boolean hasValidMetaData(ProposalRequest request) {
        return compare(Constants.ITL_TO, request.getFrom(), "Invalid sender") &&
            compare(Constants.ITL_MAJOR_VERSION, request.getMajorVersion(), "Invalid protocol major version") &&
            compare(Constants.ITL_MINOR_VERSION, request.getMinorVersion(), "Invalid protocol minor version") &&
            compare(Constants.KYOTO_REGISTRY_CODE, request.getTo(), "Invalid registry code");
    }

    /**
     * Compares the provided fields.
     *
     * @param expected The excepted value.
     * @param actual   The actual value.
     * @param message  The error message.
     * @return false/true.
     */
    private boolean compare(Comparable expected, Comparable actual, String message) {
        boolean result = Objects.equals(expected, actual);
        if (!result) {
            log.info("Validation failed: {}, expected {}, actual {}", message, expected, actual);
        }
        return result;
    }

    public NotificationRequest prepareNotificationRequest(String transactionIdentifier,
                                                          BusinessCheckErrorResult businessResult) {
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setFrom(Constants.KYOTO_REGISTRY_CODE);
        notificationRequest.setTo(Constants.ITL_TO);
        notificationRequest.setMajorVersion(Constants.ITL_MAJOR_VERSION);
        notificationRequest.setMinorVersion(Constants.ITL_MINOR_VERSION);
        notificationRequest.setTransactionIdentifier(transactionIdentifier);
        notificationRequest.setTransactionStatus(
            businessResult == null ? TransactionStatus.ACCEPTED.getCode() : TransactionStatus.REJECTED.getCode());

        List<EvaluationResult> evaluationResults = new ArrayList<>();
        if (businessResult != null && !CollectionUtils.isEmpty(businessResult.getErrors())) {
            for (BusinessCheckError error : businessResult.getErrors()) {
                EvaluationResult evaluationResult = new EvaluationResult();
                evaluationResult.setResponseCode(error.getCode());
                evaluationResults.add(evaluationResult);
            }
        }
        notificationRequest.setEvaluationResult(evaluationResults.toArray(EvaluationResult[]::new));
        return notificationRequest;
    }

    public NotificationRequest prepareNotificationRequestForTransactionCompletion(String transactionIdentifier) {
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setFrom(Constants.KYOTO_REGISTRY_CODE);
        notificationRequest.setTo(Constants.ITL_TO);
        notificationRequest.setMajorVersion(Constants.ITL_MAJOR_VERSION);
        notificationRequest.setMinorVersion(Constants.ITL_MINOR_VERSION);
        notificationRequest.setTransactionIdentifier(transactionIdentifier);
        notificationRequest.setTransactionStatus(TransactionStatus.COMPLETED.getCode());
        notificationRequest.setEvaluationResult(new EvaluationResult[0]);
        return notificationRequest;
    }

}
