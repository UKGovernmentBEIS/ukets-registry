package gov.uk.ets.reports.generator.kyotoprotocol.nonsef.r4itl;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UnitConcerned {
    private String unitType;
    private String serialNumber;
    private Long quantity;
}
