package gov.uk.ets.registry.api.user.admin.service;

import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.user.admin.repository.KeycloakNonRegisteredUsersRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class NonRegisteredUsersService {

    /** The time in hours we want to keep unregister users in keycloak from the Scheduler trigger time.*/
    @Value("${business.property.cleanup.non.registered.users.created.before.hours:24}")
    private Long createdBeforeHours;
    
    private final KeycloakNonRegisteredUsersRepository keycloakNonRegisteredUsersRepository;
    private final ServiceAccountAuthorizationService serviceAccountAuthorizationService;
    
    public void cleanUp() {
        ZonedDateTime createdBefore = LocalDateTime.now().minusHours(createdBeforeHours).atZone(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        long beforeDateTime = createdBefore.toInstant().toEpochMilli();
        log.info("Querying non registered users created before: {} ", formatter.format(createdBefore));
        List<String> userIds = keycloakNonRegisteredUsersRepository
            .fetchNonRegisteredUsersCreatedBefore(beforeDateTime, accessToken());
        
        if (Objects.isNull(userIds) || userIds.isEmpty()) {
            log.info("No users in pending registration created before {} found.",formatter.format(createdBefore));            
        }

        for (String keycloakUserId:userIds) {
            log.info("Deleting non registered user with keycloak-id: {} ", keycloakUserId);
            serviceAccountAuthorizationService.deleteUser(keycloakUserId);
        }
    }
    
    private String accessToken() {
        return serviceAccountAuthorizationService.obtainAccessToken().getToken();
    }

}
