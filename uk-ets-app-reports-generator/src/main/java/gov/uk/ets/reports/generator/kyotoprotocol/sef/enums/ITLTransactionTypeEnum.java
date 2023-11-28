package gov.uk.ets.reports.generator.kyotoprotocol.sef.enums;

/**
 * @author gkountak
 *
 */
public enum ITLTransactionTypeEnum {

    ISSUANCE(1), CONVERSION(2), EXTERNAL_TRANSFER(3), CANCELLATION(4), RETIREMENT(5), REPLACEMENT(6), CARRY_OVER(7), INTERNAL_TRANSFER(
            10);

    private int code;

    /**
     * @param code
     */
    private ITLTransactionTypeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ITLTransactionTypeEnum getFromCode(String type) {
        if(type.equals("IssuanceDecoupling") || type.equals("IssuanceCP0") || type.equals("IssueOfAAUsAndRMUs")) {
            return ISSUANCE;
        }
        if(type.equals("ConversionCP1") || type.equals("ConversionA") || type.equals("ConversionB")) {
            return CONVERSION;
        }
        if(type.equals("TransferToSOPForConversionOfERU") || type.equals("ExternalTransfer") || type.equals("ExternalTransferCP0")
                || type.equals("SurrenderKyotoUnits") || type.equals("ReversalSurrenderKyoto") || type.equals("SetAside")) {
            return EXTERNAL_TRANSFER;
        }
        if(type.equals("CancellationKyotoUnits") || type.equals("MandatoryCancellation") || type.equals("Art37Cancellation")
                || type.equals("AmbitionIncreaseCancellation") || type.equals("RetirementCP0") || type.equals("CancellationAgainstDeletion")) {
            return CANCELLATION;
        }
        if(type.equals("Retirement") || type.equals("RetirementOfSurrenderedFormerEUA")) {
            return RETIREMENT;
        }
        if(type.equals("Replacement")) {
            return REPLACEMENT;
        }
        if(type.equals("CarryOver_AAU") || type.equals("CarryOver_CER_ERU_FROM_AAU")) {
            return CARRY_OVER;
        }
        if(type.equals("AllocationOfFormerEUA") || type.equals("IssuanceOfFormerEUA") || type.equals("CancellationCP0")
            || type.equals("ConversionOfSurrenderedFormerEUA") || type.equals("CorrectiveTransactionForReversal_1")
                || type.equals("Correction") || type.equals("InternalTransfer") || type.equals("InboundTransfer") 
                || type.equals("SurrenderAllowances")) {

            return INTERNAL_TRANSFER;
        }
        return null;
    }
}

