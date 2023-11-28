package gov.uk.ets.reports.generator.domain;

import lombok.Getter;

@Getter
public enum TransactionType {
    
        IssueOfAAUsAndRMUs("Issuance of KP units", false),
        IssueAllowances("Issuance of Allowances", true),
        TransferToSOPforFirstExtTransferAAU("Transfer to SOP for First External Transfer",false),
        CancellationKyotoUnits("Voluntary cancellation of KP units",
            false
        ),
        MandatoryCancellation("Mandatory cancellation of KP units",
            false
        ),
        Art37Cancellation("Art. 3.7ter cancellation of AAU",
            false
        ),
        AmbitionIncreaseCancellation("Ambition increase cancellation of AAU",
            false
        ),
        InternalTransfer("Internal transfer",
            false
        ),
        ExternalTransfer("External transfer",
            false
        ),
        CarryOver_AAU("Carry-over AAU",
            false
        ),
        CarryOver_CER_ERU_FROM_AAU("Carry-over CER or ERU from AAU units",
            false
        ),
        SurrenderAllowances("Surrender",
            true
        ),
        InboundTransfer("Inbound Transfer",
            false
        ),
        CentralTransferAllowances("Central Transfer",
            true
        ),
        TransferAllowances("Transfer allowances",
            true
        ),
        ClosureTransfer("Closure Transfer",
            true
        ),
        AuctionDeliveryAllowances("Transfer of allowances to auction delivery account",
            true
        ),
        Retirement("Retirement",
            false
        ),
        ConversionCP1("Conversion CP1",
            false
        ),
        ConversionA("AAUs/RMUs to ERUs prior to Transfer to SOP",
            false
        ),
        ConversionB("AAUs/RMUs to ERUs after Transfer to SOP",
            false
        ),
        TransferToSOPForConversionOfERU("Transfer to SOP for Conversion of ERU",
            false
        ),
        ExpiryDateChange("Expiry Date Change of tCER or lCER",
            false
        ),
        Replacement("Replacement of tCER or lCER",
            false
        ),
        AllocateAllowances("Allocation of allowances",
            true
        ),
        ExcessAllocation("Return excess allocation",
            true
        ),
        ExcessAuction("Return excess auction",
            true
        ),
        ReverseAllocateAllowances("Reversal of allocation of allowances",
            true
        ),
        ReverseSurrenderAllowances("Reversal of surrender of allowances",
            true
        ),
        ReverseDeletionOfAllowances("Reversal of deletion of allowances",
            true
        ),
        DeletionOfAllowances("Deletion of allowances",
            true
        ),
        BalanceInstallationTransferAllowances("Balance installation transfer allowances",
            true
        ),
        IssuanceDecoupling("Issuance Decoupling",
            false
        ),
        ExternalTransferCP0("External Transfer CP0",
            false
        ),
        AllocationOfFormerEUA("Allocation of former EUA",
            false
        ),
        IssuanceCP0("Issuance CP0",
            false
        ),
        RetirementCP0("Retirement CP0",
            false
        ),
        IssuanceOfFormerEUA("Issuance of former EUA",
            false
        ),
        CancellationCP0("Cancellation CP0",
            false
        ),
        RetirementOfSurrenderedFormerEUA("Retirement of surrendered former EUA",
            false
        ),
        ConversionOfSurrenderedFormerEUA("Conversion of surrendered former EUA",
            false
        ),
        CorrectiveTransactionForReversal_1("Corrective transaction for reversal 1",
            false
        ),
        Correction("Correction",
            false
        ),
        SurrenderKyotoUnits("Surrender Kyoto units",

            false
        ),
        CancellationAgainstDeletion("Cancellation against deletion",
            false
        ),
        ReversalSurrenderKyoto("Reversal surrender Kyoto",false),
        SetAside("Set aside", false);
    
    private final String defaultLabel;
    private final boolean isETSTransaction;
    
    TransactionType(String defaultLabel, boolean isETSTransaction) {
        this.defaultLabel = defaultLabel;
        this.isETSTransaction = isETSTransaction;
    }
}