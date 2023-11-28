package gov.uk.ets.compliance.domain.events;

import gov.uk.ets.compliance.domain.ComplianceState;

public interface DynamicComplianceUpdateEvent extends CanBeMarkedAsProcessed {
    ComplianceState updateComplianceState(ComplianceState state);
}
