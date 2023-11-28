package gov.uk.ets.registry.api.account.web.model;

import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountHoldingsSummaryResultDTO {

    private Long totalAvailableQuantity = 0L;
    private Long totalReservedQuantity = 0L;
    private List<AccountHoldingsSummaryDTO> items = new ArrayList<>();
    private UnitType reservedUnitType;
    private UnitType availableUnitType;
    private ComplianceStatus currentComplianceStatus;
    private boolean shouldMeetEmissionsTarget;
}
