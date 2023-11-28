package gov.uk.ets.registry.api.task.searchmetadata.handler;

import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.searchmetadata.domain.types.MetadataName;
import gov.uk.ets.registry.api.task.searchmetadata.utils.QueryUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.event.spi.AbstractEvent;
import org.hibernate.event.spi.PostInsertEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Log4j2
public class UserMappedTasksRequestHandler implements SearchMetadataHandler {

    private Set<RequestType> RELATIVE_TYPES = new HashSet<>(Arrays.asList(
            RequestType.CHANGE_TOKEN,
            RequestType.LOST_PASSWORD_AND_TOKEN,
            RequestType.LOST_TOKEN,
            RequestType.PRINT_ENROLMENT_LETTER_REQUEST,
            RequestType.REQUESTED_EMAIL_CHANGE,
            RequestType.USER_DEACTIVATION_REQUEST,
            RequestType.AR_REQUESTED_DOCUMENT_UPLOAD,
            RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST,
            RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST,
            RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST,
            RequestType.AUTHORIZED_REPRESENTATIVE_UPDATE_ACCESS_RIGHTS_REQUEST
    ));

    @Override
    public boolean canHandle(Task task) {
        return RELATIVE_TYPES.contains(task.getType()) && task.getUser() != null;
    }

    @Override
    public void handle(Task task, AbstractEvent abstractEvent) {
        if (abstractEvent instanceof PostInsertEvent) {
            executeUpdate(abstractEvent, QueryUtils.getQuery(true,abstractEvent),
                    QueryUtils.createQueryParamsForUserMetadata(task.getId(),
                            String.valueOf(MetadataName.USER_ID_NAME_KNOWN_AS),
                            task.getUser().getUrid(),
                            abstractEvent));
        }
    }
}
