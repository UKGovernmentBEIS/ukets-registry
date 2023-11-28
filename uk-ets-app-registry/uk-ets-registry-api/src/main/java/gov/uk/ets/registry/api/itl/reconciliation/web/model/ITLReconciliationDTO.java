package gov.uk.ets.registry.api.itl.reconciliation.web.model;

import gov.uk.ets.registry.api.itl.reconciliation.type.ITLReconciliationStatus;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@RequiredArgsConstructor
public class ITLReconciliationDTO {
    private final String identifier;
    private final Date created;
    private final Date updated;
    private final ITLReconciliationStatus status;
    private final boolean readOnly;
}
