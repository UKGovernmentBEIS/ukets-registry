package gov.uk.ets.reports.generator.kyotoprotocol.nonsef.r2itl;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UnitInvolved {
    private String transactionIdentifier;
    private String unitType;
    private String serialNumber;
    private Long quantity;
}
