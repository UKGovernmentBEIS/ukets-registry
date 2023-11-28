package gov.uk.ets.registry.api.transaction.domain.data;

import static gov.uk.ets.commons.logging.RequestParamType.TRANSACTION_ID;

import java.io.*;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a summary of a Nat and Ner Return Excess Allocation transaction.
 */
@Getter
@Setter
@EqualsAndHashCode(of = {"natReturnTransactionIdentifier","nerReturnTransactionIdentifier"}, callSuper = false)
@ToString
@Builder(builderClassName = "returnExcessAllocationTransactionSummaryBuilder", builderMethodName = "returnExcessAllocationTransactionSummaryBuilder")
@NoArgsConstructor
@AllArgsConstructor
public class ReturnExcessAllocationTransactionSummary extends TransactionSummary implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = -4192453403381624717L;

    /**
     * The unique business identifier, e.g. GB40140 for the NAT return.
     */
    @MDCParam(TRANSACTION_ID)
    private String natReturnTransactionIdentifier;

    /**
     * The unique business identifier, e.g. GB40140 for the NER return.
     */
    @MDCParam(TRANSACTION_ID)
    private String nerReturnTransactionIdentifier;

    /**
     * The total NAT quantity transferred in the context of this transaction.
     */
    private Long natQuantity;

    /**
     * The total NER quantity transferred in the context of this transaction.
     */
    private Long nerQuantity;    
    
    /**
     * NAT Acquiring account: The unique account business identifier, e.g. 10455.
     */
    private Long natAcquiringAccountIdentifier;

    /**
     * NER Acquiring account: The unique account business identifier, e.g. 10455.
     */
    private Long nerAcquiringAccountIdentifier;
    
    /**
     * NAT Acquiring account: The Kyoto account type, e.g. PARTY_HOLDING_ACCOUNT.
     */
    private KyotoAccountType natAcquiringAccountType;

    /**
     * NER Acquiring account: The Kyoto account type, e.g. PARTY_HOLDING_ACCOUNT.
     */
    private KyotoAccountType nerAcquiringAccountType;


    /**
     * NAT Acquiring account: The full account identifier, e.g. GB-100-10455-0-61, JP-100-23213 etc.
     */
    private String natAcquiringAccountFullIdentifier;

    /**
     * NER Acquiring account: The full account identifier, e.g. GB-100-10455-0-61, JP-100-23213 etc.
     */
    private String nerAcquiringAccountFullIdentifier;


    /**
     * The display name of the acquiring account.
     */
    private String natAcquiringAccountName;

    /**
     * The display name of the acquiring account.
     */
    private String nerAcquiringAccountName;
    
    /**
     * 
     */
    private ReturnExcessAllocationType returnExcessAllocationType;
    
    public enum ReturnExcessAllocationType {
        NAT,
        NER,
        NAVAT,
        NAT_AND_NER;
    }

    /**
     * The NAT acquiring account info.
     */
    private AcquiringAccountInfo natAcquiringAccountInfo;

    /**
     * The NER acquiring account info.
     */
    private AcquiringAccountInfo nerAcquiringAccountInfo;
}
