package gov.uk.ets.registry.api.alerts.web.model;

import gov.uk.ets.registry.api.itl.notice.web.model.ITLNoticeResult;
import gov.uk.ets.registry.api.itl.reconciliation.web.model.ITLReconciliationDTO;
import gov.uk.ets.registry.api.reconciliation.web.ReconciliationDTO;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
@EqualsAndHashCode
public class AlertsResponse {

    private List<ITLNoticeResult> searchResponseResults;
    private ITLReconciliationDTO itlReconcileDTO;
    private ReconciliationDTO reconciliationDTO;
}
