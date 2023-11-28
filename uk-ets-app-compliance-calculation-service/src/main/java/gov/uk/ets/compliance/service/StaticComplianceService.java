package gov.uk.ets.compliance.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.compliance.domain.ComplianceState;
import gov.uk.ets.compliance.domain.DynamicCompliance;
import gov.uk.ets.compliance.domain.DynamicComplianceEntity;
import gov.uk.ets.compliance.domain.events.base.StaticComplianceRequestEvent;
import gov.uk.ets.compliance.repository.DynamicComplianceRepository;

public class StaticComplianceService extends ComplianceServiceBase {

    public StaticComplianceService(DynamicComplianceRepository dynamicComplianceRepository, ObjectMapper objectMapper) {
        super(dynamicComplianceRepository, objectMapper);
    }

    /**
     * Process a Static Compliance event.
     *
     * @param event the received event.
     * @return the retrieved Compliance State.
     */
    public ComplianceState processEvent(StaticComplianceRequestEvent event) {
        DynamicComplianceEntity dynamicComplianceEntity =
            findDynamicComplianceEntityByIdOrElseThrow(event.getCompliantEntityId());
        DynamicCompliance dynamicCompliance =
                deserializeToDynamicCompliance(dynamicComplianceEntity.getDynamicCompliance());
        checkForDuplicateEvent(event.getEventId(), dynamicCompliance);
        return dynamicCompliance.processStaticComplianceRequestEvent(event);
        // TODO: save something in the db?
    }
}
