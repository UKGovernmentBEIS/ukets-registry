package gov.uk.ets.reports.generator.kyotoprotocol.nonsef.r5itl;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an invalid unit.
 *
 * @author kattoulp
 *
 */
@Getter
@Setter
@Builder
public class InvalidUnitLine {

    private String serialNumber;
    private String unitType;
    private Long quantity;
    private String transactionNumber;

}
