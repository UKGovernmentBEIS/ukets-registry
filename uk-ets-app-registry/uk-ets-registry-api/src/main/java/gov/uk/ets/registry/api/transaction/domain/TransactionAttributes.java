package gov.uk.ets.registry.api.transaction.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TransactionAttributes {
    @JsonProperty("AllocationYear")
    private int allocationYear;
    @JsonProperty("AllocationType")
    private String allocationType;
    @JsonProperty("RelatedNATTransactionIdentifer")
    private String relatedNATTransactionIdentifer;
    @JsonProperty("TriggeredByFinalisation")
    private boolean triggeredByFinalisation;
}
