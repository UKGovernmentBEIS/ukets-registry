package gov.uk.ets.registry.api.task.searchmetadata.handler;

import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.searchmetadata.domain.types.MetadataName;
import gov.uk.ets.registry.api.task.searchmetadata.utils.QueryUtils;
import lombok.RequiredArgsConstructor;
import org.hibernate.event.spi.AbstractEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountOpeningInstallationTransferRequestHandler implements SearchMetadataHandler {

    private final Mapper mapper;

    @Override
    public boolean canHandle(Task task) {
        return RequestType.ACCOUNT_OPENING_INSTALLATION_TRANSFER_REQUEST == task.getType() && task.getDifference() != null;
    }

    @Override
    public void handle(Task task, AbstractEvent abstractEvent) {
        AccountDTO diff = createPojo(task.getDifference());
        if (diff.getAuthorisedRepresentatives() != null && !diff.getAuthorisedRepresentatives().isEmpty()) {
            diff.getAuthorisedRepresentatives().forEach(ar -> executeUpdate(abstractEvent,
                    QueryUtils.getQuery(true,abstractEvent),
                    QueryUtils.createQueryParamsForUserMetadata(task.getId(),
                            String.valueOf(MetadataName.USER_ID_NAME_KNOWN_AS),
                            ar.getUrid(),
                            abstractEvent)));
        }
    }

    private AccountDTO createPojo(String difference) {
        return mapper.convertToPojo(difference, AccountDTO.class);
    }
}
