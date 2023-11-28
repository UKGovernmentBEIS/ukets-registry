package gov.uk.ets.registry.api.notification.userinitiated.repository;

import gov.uk.ets.registry.api.notification.userinitiated.NotificationDefinitionDTO;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationDefinition;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationDefinitionRepository extends JpaRepository<NotificationDefinition, Long> {

    Optional<NotificationDefinition> findByType(NotificationType type);

    @Query(
        "select new gov.uk.ets.registry.api.notification.userinitiated.NotificationDefinitionDTO(nd.shortText, nd.longText, 0) " +
            "from NotificationDefinition nd " +
            "where nd.type = ?1 "
    )
    Optional<NotificationDefinitionDTO> findDtoByType(NotificationType type);
}
