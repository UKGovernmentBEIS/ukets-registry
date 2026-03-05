package gov.uk.ets.registry.api.helper.integration;

import org.springframework.stereotype.Component;
import uk.gov.netz.integration.model.regulatornotice.RegulatorNoticeEvent;

@Component
public class RegulatorNoticeTestHelper {

    public RegulatorNoticeEvent regulatorNoticeEvent(String registryId, String notificationType,
                                                     String fileName, byte[] fileData) {
        RegulatorNoticeEvent event = new RegulatorNoticeEvent();
        event.setRegistryId(registryId);
        event.setType(notificationType);
        event.setFileName(fileName);
        event.setFileData(fileData);
        return event;
    }
}
