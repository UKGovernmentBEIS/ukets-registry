package gov.uk.ets.registry.api.reconciliation.web;

import gov.uk.ets.registry.api.reconciliation.type.ReconciliationStatus;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReconciliationDTO {
    private Long identifier;
    private String created;
    private String updated;
    private ReconciliationStatus status;
}
