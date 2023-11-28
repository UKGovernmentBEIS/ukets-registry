package gov.uk.ets.registry.api.transaction.domain;

import com.google.gson.*;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.transaction.domain.data.*;
import gov.uk.ets.registry.api.transaction.processor.ExcessAllocationProcessor;

import gov.uk.ets.registry.api.transaction.shared.ZonedDateTimeTypeAdapter;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ExcessAllocationTransactionFactory {

    public List<TransactionSummary> createNatAndNerTransactionSummaries(ReturnExcessAllocationTransactionSummary returnExcessAllocationTransaction) {
        List<TransactionSummary> transactionSummaries = new ArrayList<>();
        long nerQuantity = returnExcessAllocationTransaction.getNerQuantity();
        long natQuantity = returnExcessAllocationTransaction.getNatQuantity();


        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeTypeAdapter())
                .create();

        ReturnExcessAllocationTransactionSummary returnExcessAllocationTransactionCopy =
                gson.fromJson(gson.toJson(returnExcessAllocationTransaction),
                ReturnExcessAllocationTransactionSummary.class);


        TransactionSummary nerTransactionSummary =
                createTransactionSummary(returnExcessAllocationTransactionCopy,"NER", nerQuantity);
        TransactionSummary natTransactionSummary =
                createTransactionSummary(returnExcessAllocationTransaction,"NAT", natQuantity);


        modifyTransactionBlocks(nerTransactionSummary,nerQuantity);
        modifyTransactionBlocks(natTransactionSummary,natQuantity);



        transactionSummaries.add(natTransactionSummary);
        transactionSummaries.add(nerTransactionSummary);

        return transactionSummaries;
    }


    public List<SignedTransactionSummary> createNatAndNerSignedTransactions(SignedReturnExcessAllocationTransactionSummary
                                                                           signedReturnExcessAllocationTransactionSummary) {

        Long natQuantity = signedReturnExcessAllocationTransactionSummary.getNatQuantity();
        Long nerQuantity = signedReturnExcessAllocationTransactionSummary.getNerQuantity();

        List<SignedTransactionSummary> signedTransactionSummaries = new ArrayList<>();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeTypeAdapter())
                .create();

        SignedReturnExcessAllocationTransactionSummary signedTransactionCopy =
                gson.fromJson(gson.toJson(signedReturnExcessAllocationTransactionSummary),
                 SignedReturnExcessAllocationTransactionSummary.class);

        SignedTransactionSummary natSigned = createSignedTransactionSummary(signedReturnExcessAllocationTransactionSummary,
                signedReturnExcessAllocationTransactionSummary.getSignatureInfo(),
                "NAT", natQuantity);

        modifyTransactionBlocks(natSigned, natQuantity);

        SignedTransactionSummary nerSigned = createSignedTransactionSummary(signedTransactionCopy,
                signedReturnExcessAllocationTransactionSummary.getSignatureInfo(),
                "NER", nerQuantity);

        modifyTransactionBlocks(nerSigned,nerQuantity);


        signedTransactionSummaries.add(natSigned);
        signedTransactionSummaries.add(nerSigned);


        return signedTransactionSummaries;
    }

    public TransactionSummary createTransactionSummary(ReturnExcessAllocationTransactionSummary transaction,String allocationType, Long quantity) {
        TransactionSummary transactionSummary = new TransactionSummary();
        transactionSummary.setIdentifier(transaction.getIdentifier());
        transactionSummary.setReversedIdentifier(transaction.getReversedIdentifier());
        transactionSummary.setTransactionConnectionSummary(transaction.getTransactionConnectionSummary());
        transactionSummary.setType(transaction.getType());
        transactionSummary.setStatus(transaction.getStatus());
        transactionSummary.setQuantity(quantity);
        transactionSummary.setAcquiringAccountIdentifier(transaction.getAcquiringAccountIdentifier());
        transactionSummary.setAcquiringAccountType(transaction.getAcquiringAccountType());
        transactionSummary.setAcquiringAccountRegistryCode(transaction.getAcquiringAccountRegistryCode());
        transactionSummary.setAcquiringAccountFullIdentifier(transaction.getAcquiringAccountFullIdentifier());
        transactionSummary.setToBeReplacedBlocksAccountFullIdentifier(transaction.getToBeReplacedBlocksAccountFullIdentifier());
        transactionSummary.setAcquiringAccountCommitmentPeriod(transaction.getAcquiringAccountCommitmentPeriod());
        transactionSummary.setAcquiringAccountName(transaction.getAcquiringAccountName());
        transactionSummary.setExternalAcquiringAccount(transaction.isExternalAcquiringAccount());
        transactionSummary.setHasAccessToAcquiringAccount(transaction.isHasAccessToAcquiringAccount());
        transactionSummary.setTransferringAccountIdentifier(transaction.getTransferringAccountIdentifier());
        transactionSummary.setTransferringAccountType(transaction.getTransferringAccountType());
        transactionSummary.setTransferringRegistryCode("UK");
        transactionSummary.setTransferringAccountFullIdentifier(transaction.getTransferringAccountFullIdentifier());
        transactionSummary.setTransferringAccountCommitmentPeriod(transaction.getTransferringAccountCommitmentPeriod());
        transactionSummary.setTransferringAccountName(transaction.getTransferringAccountName());
        transactionSummary.setExternalTransferringAccount(transaction.isExternalTransferringAccount());
        transactionSummary.setHasAccessToTransferringAccount(transaction.isHasAccessToTransferringAccount());
        transactionSummary.setStarted(transaction.getStarted());
        transactionSummary.setLastUpdated(transaction.getLastUpdated());
        transactionSummary.setItlNotification(transaction.getItlNotification());
        transactionSummary.setBlocks(transaction.getBlocks());
        transactionSummary.setResponses(transaction.getResponses());
        transactionSummary.setUnitType(transaction.getUnitType());
        transactionSummary.setTaskIdentifier(transaction.getTaskIdentifier());
        transactionSummary.setComment(transaction.getComment());
        transactionSummary.setAdditionalAttributes(transaction.getAdditionalAttributes());
        transactionSummary.setAttributes(transaction.getAttributes());
        transactionSummary.setAllocationType(allocationType.equals("NAT") ? AllocationType.NAT : AllocationType.NER);
        transactionSummary.setAllocationYear(transaction.getAllocationYear());
        transactionSummary.setCanBeReversed(transaction.isCanBeReversed());
        transactionSummary.setReference(transaction.getReference());
        transactionSummary.setExecutionDateTime(transaction.getExecutionDateTime());
        return transactionSummary;
    }
     public SignedTransactionSummary createSignedTransactionSummary(ReturnExcessAllocationTransactionSummary transaction,SignatureInfo signatureInfo,
                                                              String allocationType, Long quantity) {
        SignedTransactionSummary transactionSummary = new SignedTransactionSummary();
        transactionSummary.setSignatureInfo(signatureInfo);
        transactionSummary.setIdentifier(AllocationType.NAT.equals(AllocationType.valueOf(allocationType))  ? transaction.getNatReturnTransactionIdentifier() : transaction.getNerReturnTransactionIdentifier());
        transactionSummary.setReversedIdentifier(transaction.getReversedIdentifier());
        transactionSummary.setTransactionConnectionSummary(transaction.getTransactionConnectionSummary());
        transactionSummary.setType(transaction.getType());
        transactionSummary.setStatus(transaction.getStatus());
        transactionSummary.setQuantity(quantity);
        transactionSummary.setAcquiringAccountIdentifier(AllocationType.NAT.equals(AllocationType.valueOf(allocationType)) ? transaction.getNatAcquiringAccountInfo().getIdentifier() : transaction.getNerAcquiringAccountInfo().getIdentifier());
        transactionSummary.setAcquiringAccountType(transaction.getAcquiringAccountType());
        transactionSummary.setAcquiringAccountRegistryCode(AllocationType.NAT.equals(AllocationType.valueOf(allocationType)) ? transaction.getNatAcquiringAccountInfo().getRegistryCode() : transaction.getNerAcquiringAccountInfo().getRegistryCode());
        transactionSummary.setAcquiringAccountFullIdentifier(AllocationType.NAT.equals(AllocationType.valueOf(allocationType)) ? transaction.getNatAcquiringAccountInfo().getFullIdentifier() : transaction.getNerAcquiringAccountInfo().getFullIdentifier());
        transactionSummary.setToBeReplacedBlocksAccountFullIdentifier(transaction.getToBeReplacedBlocksAccountFullIdentifier());
        transactionSummary.setAcquiringAccountCommitmentPeriod(transaction.getAcquiringAccountCommitmentPeriod());
        transactionSummary.setAcquiringAccountName(transaction.getAcquiringAccountName());
        transactionSummary.setExternalAcquiringAccount(transaction.isExternalAcquiringAccount());
        transactionSummary.setHasAccessToAcquiringAccount(transaction.isHasAccessToAcquiringAccount());
        transactionSummary.setTransferringAccountIdentifier(transaction.getTransferringAccountIdentifier());
        transactionSummary.setTransferringAccountType(transaction.getTransferringAccountType());
        transactionSummary.setTransferringRegistryCode("UK");
        transactionSummary.setTransferringAccountFullIdentifier(transaction.getTransferringAccountFullIdentifier());
        transactionSummary.setTransferringAccountCommitmentPeriod(transaction.getTransferringAccountCommitmentPeriod());
        transactionSummary.setTransferringAccountName(transaction.getTransferringAccountName());
        transactionSummary.setExternalTransferringAccount(transaction.isExternalTransferringAccount());
        transactionSummary.setHasAccessToTransferringAccount(transaction.isHasAccessToTransferringAccount());
        transactionSummary.setStarted(transaction.getStarted());
        transactionSummary.setLastUpdated(transaction.getLastUpdated());
        transactionSummary.setItlNotification(transaction.getItlNotification());
        transactionSummary.setBlocks(transaction.getBlocks());
        transactionSummary.setResponses(transaction.getResponses());
        transactionSummary.setUnitType(transaction.getUnitType());
        transactionSummary.setTaskIdentifier(transaction.getTaskIdentifier());
        transactionSummary.setComment(transaction.getComment());
        if (AllocationType.NER.equals(AllocationType.valueOf(allocationType))) {
            Map<String,Serializable> attrs = Optional.ofNullable(transaction.getAdditionalAttributes()).orElse(new HashMap<>());
            attrs.put(ExcessAllocationProcessor.RELATED_NAT_TRANSACTION_IDENTIFER, transaction.getNatReturnTransactionIdentifier());
            transactionSummary.setAdditionalAttributes(attrs);
        } else if (AllocationType.NAT.equals(AllocationType.valueOf(allocationType))) {
            Map<String,Serializable> attrs = Optional.ofNullable(transaction.getAdditionalAttributes()).orElse(new HashMap<>());
            attrs.put(ExcessAllocationProcessor.IS_TRIGGERED_BY_NER_FINALIZATION, true);
            transactionSummary.setAdditionalAttributes(attrs);
        }
        transactionSummary.setAttributes(transaction.getAttributes());
        transactionSummary.setAllocationType(allocationType.equals("NAT") ? AllocationType.NAT : AllocationType.NER);
        transactionSummary.setAllocationYear(transaction.getAllocationYear());
        transactionSummary.setCanBeReversed(transaction.isCanBeReversed());
        transactionSummary.setReference(transaction.getReference());
        transactionSummary.setExecutionDateTime(transaction.getExecutionDateTime());
        return transactionSummary;
    }



    private void modifyTransactionBlocks(TransactionSummary transactionSummary, Long quantity) {
        transactionSummary.getBlocks().forEach(transactionBlockSummary -> {
            transactionBlockSummary.setQuantity(String.valueOf(quantity));
        });
    }

}
