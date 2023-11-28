package gov.uk.ets.compliance.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.compliance.domain.DynamicCompliance;
import gov.uk.ets.compliance.domain.DynamicComplianceEntity;
import gov.uk.ets.compliance.domain.events.InitCompliantEntity;
import gov.uk.ets.compliance.domain.events.base.DynamicComplianceEvent;
import gov.uk.ets.compliance.repository.DynamicComplianceRepository;

public class DynamicComplianceService extends ComplianceServiceBase {

    public DynamicComplianceService(DynamicComplianceRepository dynamicComplianceRepository, ObjectMapper objectMapper) {
        super(dynamicComplianceRepository, objectMapper);
    }

    /**
     * Process a Compliance event.
     *
     * @param event the received event.
     * @return the updated Dynamic Compliance.
     */
    public DynamicCompliance processEvent(DynamicComplianceEvent event) {
        if (event instanceof InitCompliantEntity) {
            return this.init((InitCompliantEntity) event);
        } else {
            DynamicComplianceEntity dynamicComplianceEntity =
                findDynamicComplianceEntityByIdOrElseThrow(event.getCompliantEntityId());
            DynamicCompliance dynamicCompliance =
                deserializeToDynamicCompliance(dynamicComplianceEntity.getDynamicCompliance());
            checkForDuplicateEvent(event.getEventId(), dynamicCompliance);
            boolean processed = dynamicCompliance.processDynamicComplianceUpdateEvent(event);
            dynamicComplianceEntity.setDynamicCompliance(serializeDynamicCompliance(dynamicCompliance));
            dynamicComplianceRepository.save(dynamicComplianceEntity);
            if (!processed) {
                throw new DynamicComplianceException("Event makes the compliance state invalid");
            }
            return dynamicCompliance;

        }
    }

    private DynamicCompliance init(InitCompliantEntity initCompliantEntity) {
        DynamicCompliance dynamicCompliance = new DynamicCompliance(
                initCompliantEntity.getCompliantEntityId(),
                initCompliantEntity.getCurrentYear(),
                initCompliantEntity.getFirstYearOfVerifiedEmissions(),
                initCompliantEntity.getLastYearOfVerifiedEmissions(),
                initCompliantEntity.getDateRequested());
        DynamicComplianceEntity dynamicComplianceEntity = new DynamicComplianceEntity();
        dynamicComplianceEntity.setCompliantEntityId(initCompliantEntity.getCompliantEntityId());
        dynamicComplianceEntity.setDynamicCompliance(serializeDynamicCompliance(dynamicCompliance));
        dynamicComplianceRepository.save(dynamicComplianceEntity);
        return dynamicCompliance;
    }
}
