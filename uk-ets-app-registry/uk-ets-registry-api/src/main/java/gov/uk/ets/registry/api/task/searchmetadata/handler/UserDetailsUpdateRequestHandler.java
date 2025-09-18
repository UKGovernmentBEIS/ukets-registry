package gov.uk.ets.registry.api.task.searchmetadata.handler;


import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.searchmetadata.domain.types.MetadataName;
import gov.uk.ets.registry.api.task.searchmetadata.utils.QueryUtils;
import gov.uk.ets.registry.api.user.admin.web.model.UserDetailsDTO;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.hibernate.event.spi.AbstractEvent;
import org.hibernate.event.spi.PostUpdateEvent;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserDetailsUpdateRequestHandler implements SearchMetadataHandler {

    private final Mapper mapper;

    @Override
    public boolean canHandle(Task task) {
        return RequestType.USER_DETAILS_UPDATE_REQUEST == task.getType() && task.getBefore() != null;
    }

    @Override
    public void handle(Task task, AbstractEvent abstractEvent) {
        UserDetailsDTO before = mapper.convertToPojo(task.getBefore(), UserDetailsDTO.class);
        UserDetailsDTO difference = mapper.convertToPojo(task.getDifference(), UserDetailsDTO.class);

        if (abstractEvent instanceof PostUpdateEvent) {
           executeUpdate(abstractEvent,QueryUtils.getQueryForUserDetailsUpdate(),
                   QueryUtils.createQueryParamsForUserUpdateDetails(before.getUrid(),
                                                                    getFullName(before,difference),
                                                                    getKnownAs(before,difference)));
        } else {
            executeUpdate(abstractEvent,QueryUtils.getQuery(true,abstractEvent),
                    QueryUtils.createQueryParamsForUserMetadata(task.getId(),
                            String.valueOf(MetadataName.USER_ID_NAME_KNOWN_AS),
                            before.getUrid(),
                            abstractEvent));
        }
    }

    private String getFullName(UserDetailsDTO before, UserDetailsDTO difference) {
        String firstName = Optional.ofNullable(difference.getFirstName()).orElse(before.getFirstName());
        String lastName = Optional.ofNullable(difference.getLastName()).orElse(before.getLastName());
        return firstName + " " + lastName;
    }

    private String getKnownAs(UserDetailsDTO before, UserDetailsDTO difference) {
        return Stream.of(difference.getAlsoKnownAs(), before.getAlsoKnownAs())
            .filter(Objects::nonNull)
            .findFirst()
            .orElse("");
    }
}
