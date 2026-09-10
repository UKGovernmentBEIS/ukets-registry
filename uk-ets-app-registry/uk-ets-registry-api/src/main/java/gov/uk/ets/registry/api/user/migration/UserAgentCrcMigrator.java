package gov.uk.ets.registry.api.user.migration;

import static gov.uk.ets.registry.api.user.domain.UserAttributes.KEYCLOAK_ATTRIBUTE_AGENT;
import static gov.uk.ets.registry.api.user.domain.UserAttributes.KEYCLOAK_ATTRIBUTE_CRC;
import static gov.uk.ets.registry.api.user.domain.UserAttributes.KEYCLOAK_ATTRIBUTE_CRC_ISSUANCE_DATE;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.common.UserDetailsUtil;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import gov.uk.ets.registry.api.user.domain.AgentType;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ClientErrorException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.core.LockingTaskExecutor.Task;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserAgentCrcMigrator implements Task {

    public static final List<UserStatus> VALIDATED_OR_ENROLLED_STATUS =
            List.of(UserStatus.VALIDATED, UserStatus.ENROLLED);
    public static final List<AccountAccessRight> INITIATE_OR_APPROVE_RIGHTS =
            List.of(AccountAccessRight.INITIATE, AccountAccessRight.APPROVE, AccountAccessRight.INITIATE_AND_APPROVE);
    private final UserRepository userRepository;
    private final ServiceAccountAuthorizationService serviceAccountAuthorizationService;
    private final MigratorHistoryRepository migratorHistoryRepository;

    @Override
    @Transactional
    public void call() throws Throwable {
    	
    	Instant start = Instant.now();
        log.info("UserAgentCrcMigrator started");
        List<MigratorHistory> migratorHistoryList =
                migratorHistoryRepository.findByMigratorName(
                        MigratorName.USER_AGENT_CRC_MIGRATOR);

        if (!migratorHistoryList.isEmpty()) {
            log.info("[UserAgentCrcMigrator] has already been performed, skipping.");
            return;
        }

        
        Set<String> usersWithCRC = userRepository.findUsersByStatusAndActiveAccountAccessRights(
                VALIDATED_OR_ENROLLED_STATUS,
                INITIATE_OR_APPROVE_RIGHTS)
                .stream()
                .filter(u -> Objects.nonNull(u.getEnrolmentKeyDate()))
                .map(t -> t.getUrid())
                .collect(Collectors.toSet());
        
        List<User> users = userRepository.findAll();

        Set<String> failedUsers = new HashSet<>();

        users.forEach(user -> {
            user.setAgent(AgentType.NO);
            Boolean crc = Boolean.FALSE;
            Optional<Date> setCrcIssuanceDateOpt = Optional.empty();
            Optional<String> formattedCrcIssuanceDateOpt = Optional.empty();
            
            if (usersWithCRC.contains(user.getUrid())) {
                crc = Boolean.TRUE;
                setCrcIssuanceDateOpt = Optional.ofNullable(user.getEnrolmentKeyDate());
            }
            user.setCrc(crc);
            if (setCrcIssuanceDateOpt.isPresent()) {
                LocalDateTime crcIssuanceDateTime = UserDetailsUtil.userCRCIssuanceDateToDateTime(setCrcIssuanceDateOpt.get());
                formattedCrcIssuanceDateOpt = Optional.of(crcIssuanceDateTime.format(UserDetailsUtil.CRC_DATE_FORMATTER));
                user.setCrcIssuanceDate(Date.from(crcIssuanceDateTime.toInstant(ZoneOffset.UTC)));
            } else {
                user.setCrcIssuanceDate(null);
            }

            try {
                UserRepresentation userRepresentation = getKeycloakUser(user);

                if (userRepresentation != null) {
                    updateAgentCrcAttributes(userRepresentation, crc, formattedCrcIssuanceDateOpt);
                } else {
                    log.warn(
                            "[UserAgentCrcMigrator] Could not update Keycloak Agent Crc attributes for user with urid: {}",
                            user.getUrid()
                    );
                    failedUsers.add(user.getUrid());
                }

            } catch (Exception e) {
                log.error(
                        "[UserAgentCrcMigrator] Failed updating Keycloak Agent Crc attributes for user with urid: {}",
                        user.getUrid(),
                        e
                );
                failedUsers.add(user.getUrid());
            }
        });

        updateMigrationHistory();
        log.info(
                "[UserAgentCrcMigrator] Completed. Processed: {}, Failed Keycloak updates: {}",
                users.size(),
                failedUsers.size()
        );
        if (!failedUsers.isEmpty()) {
            log.warn(
                    "[UserAgentCrcMigrator] Failed users: {}",
                    failedUsers
            );
        }


        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        
        long minutes = duration.toMinutes();
        long seconds = duration.minusMinutes(minutes).getSeconds();
        
        log.info("Migration elapsed time: {} minutes {} seconds", minutes, seconds);
    }

    private UserRepresentation getKeycloakUser(User user) {
        try {
            return serviceAccountAuthorizationService.getUser(user.getIamIdentifier());
        } catch (ClientErrorException e) {
            log.warn(
                    "[UserAgentCrcMigrator] Could not retrieve Keycloak user. urid: {}, iamIdentifier: {}",
                    user.getUrid(),
                    user.getIamIdentifier(),
                    e
            );
        }
        return null;
    }

    private void updateAgentCrcAttributes(UserRepresentation userRepresentation,Boolean crc,Optional<String> formattedCrcIssuanceDateOpt) {

        Map<String, List<String>> attributes = userRepresentation.getAttributes();
        attributes.put(KEYCLOAK_ATTRIBUTE_AGENT.getAttributeName(), List.of(AgentType.NO.name()));
        attributes.put(KEYCLOAK_ATTRIBUTE_CRC.getAttributeName(), List.of(crc.toString()));
        
        if (formattedCrcIssuanceDateOpt.isPresent()) {
            attributes.put(KEYCLOAK_ATTRIBUTE_CRC_ISSUANCE_DATE.getAttributeName(), List.of(formattedCrcIssuanceDateOpt.get()));
        } else {
            attributes.remove(KEYCLOAK_ATTRIBUTE_CRC_ISSUANCE_DATE.getAttributeName());
        }
        
        serviceAccountAuthorizationService.updateUserDetails(userRepresentation);
    }

    private void updateMigrationHistory() {
        MigratorHistory migratorHistory = new MigratorHistory();
        migratorHistory.setMigratorName(MigratorName.USER_AGENT_CRC_MIGRATOR);
        migratorHistory.setCreatedOn(LocalDateTime.now());
        migratorHistoryRepository.save(migratorHistory);
    }

}
