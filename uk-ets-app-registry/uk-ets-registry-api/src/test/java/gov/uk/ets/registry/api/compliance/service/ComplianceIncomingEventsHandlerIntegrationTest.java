package gov.uk.ets.registry.api.compliance.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.ActivityType;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.repository.ActivityTypeRepository;
import gov.uk.ets.registry.api.account.repository.InstallationRepository;
import gov.uk.ets.registry.api.common.error.UkEtsException;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.common.test.PostgresJpaTest;
import gov.uk.ets.registry.api.compliance.domain.StaticComplianceStatus;
import gov.uk.ets.registry.api.compliance.messaging.events.incoming.ComplianceCalculatedEvent;
import gov.uk.ets.registry.api.compliance.messaging.events.incoming.StaticComplianceRetrievedEvent;
import gov.uk.ets.registry.api.compliance.repository.StaticComplianceStatusRepository;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.user.admin.service.DisabledKeycloakUserAdministrationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@PostgresJpaTest
@Import({ComplianceIncomingEventsHandler.class, EventService.class, DisabledKeycloakUserAdministrationService.class})
class ComplianceIncomingEventsHandlerIntegrationTest {

    private static final Long TEST_INSTALLATION_IDENTIFIER = 123456L;
    private static final Long TEST_ACCOUNT_IDENTIFIER = 123L;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private InstallationRepository installationRepository;
    @Autowired
    private ActivityTypeRepository activityTypeRepository;
    @Autowired
    private StaticComplianceStatusRepository staticComplianceStatusRepository;
    @Autowired
    private EventService eventService;

    @Autowired
    private ComplianceIncomingEventsHandler cut;

    Installation installation;
    Account account;
    StaticComplianceStatus staticComplianceStatus;
    StaticComplianceStatus updatedStaticComplianceStatus;

    @BeforeEach
    public void setUp() {
        account = new Account();
        account.setIdentifier(TEST_ACCOUNT_IDENTIFIER);
        account.setFullIdentifier(TEST_ACCOUNT_IDENTIFIER.toString());
        account.setAccountName("TEST_ACCOUNT");
        account.setStatus(Status.ACTIVE);
        account.setOpeningDate(new Date());
        account.setAccountStatus(AccountStatus.OPEN);
        account.setCommitmentPeriodCode(1);
        account.setAccountType("INSTALLATION");
        account.setCheckDigits(2);

        accountRepository.saveAndFlush(account);

        installation = new Installation();
        installation.setIdentifier(TEST_INSTALLATION_IDENTIFIER);
        installation.setStartYear(2021);
        installation.setPermitIdentifier("123");
        installation.setAccount(account);

        installationRepository.saveAndFlush(installation);
        ActivityType activityType = new ActivityType();
        activityType.setDescription("COMBUSTION_OF_FUELS");
        activityType.setInstallation(installation);
        installation.setActivityTypes(Set.of(activityType));
        activityTypeRepository.saveAndFlush(activityType);
        account.setCompliantEntity(installation);
        accountRepository.saveAndFlush(account);

        staticComplianceStatus = new StaticComplianceStatus();
        staticComplianceStatus.setCompliantEntity(installation);
        staticComplianceStatus.setComplianceStatus(ComplianceStatus.A);
        staticComplianceStatus.setYear(2021L);
        staticComplianceStatusRepository.saveAndFlush(staticComplianceStatus);

    }

    @AfterEach
    public void tearDown() {
        installationRepository.delete(installation);
        accountRepository.delete(account);
        staticComplianceStatusRepository.delete(staticComplianceStatus);
        if (updatedStaticComplianceStatus != null) {
            staticComplianceStatusRepository.delete(updatedStaticComplianceStatus);
        }
    }

    @Test
    public void shouldUpdateDynamicComplianceStatusWhenComplianceEntityExists() {

        cut.processDynamicComplianceOutcome(ComplianceCalculatedEvent.builder()
            .compliantEntityId(TEST_INSTALLATION_IDENTIFIER)
            .status(ComplianceStatus.C)
            .build());

        Account updatedAccount = accountRepository.findByCompliantEntityIdentifier(TEST_INSTALLATION_IDENTIFIER).get();

        assertThat(updatedAccount.getComplianceStatus()).isEqualTo(ComplianceStatus.C);
    }

    @Test
    public void shouldThrowWhenCompliantEntityDoesNotExist() {

        assertThrows(UkEtsException.class,
            () -> cut.processDynamicComplianceOutcome(ComplianceCalculatedEvent.builder()
                .compliantEntityId(1000L)
                .status(ComplianceStatus.C)
                .build()));
    }

    @Test
    public void shouldUpdateExistingStaticComplianceStatus() {


        LocalDateTime now = LocalDateTime.of(2021, 1, 1, 0, 1);
        long reportingYear = now.getYear() - 1;
        StaticComplianceRetrievedEvent event = StaticComplianceRetrievedEvent.builder()
            .compliantEntityId(TEST_INSTALLATION_IDENTIFIER)
            .status(ComplianceStatus.C)
            .dateTriggered(now)
            .build();

        cut.processStaticComplianceOutcome(event);

        StaticComplianceStatus updatedStaticComplianceStatus =
            staticComplianceStatusRepository.findByCompliantEntityIdentifierAndYear(TEST_INSTALLATION_IDENTIFIER,
                    reportingYear)
                .get();

        assertThat(updatedStaticComplianceStatus.getComplianceStatus()).isEqualTo(ComplianceStatus.C);
    }

    @Test
    public void shouldCreateNewEntryIfStaticComplianceStatusDoesNotExist() {
        StaticComplianceRetrievedEvent event = StaticComplianceRetrievedEvent.builder()
            .compliantEntityId(TEST_INSTALLATION_IDENTIFIER)
            .status(ComplianceStatus.C)
            .dateTriggered(LocalDateTime.of(2023, 1, 1, 0, 0))
            .build();

        cut.processStaticComplianceOutcome(event);

        updatedStaticComplianceStatus =
            staticComplianceStatusRepository.findByCompliantEntityIdentifierAndYear(TEST_INSTALLATION_IDENTIFIER, 2022L)
                .get();

        StaticComplianceStatus notUpdatedStaticComplianceStatus =
            staticComplianceStatusRepository.findByCompliantEntityIdentifierAndYear(TEST_INSTALLATION_IDENTIFIER, 2021L)
                .get();

        assertThat(updatedStaticComplianceStatus.getComplianceStatus()).isEqualTo(ComplianceStatus.C);
        assertThat(notUpdatedStaticComplianceStatus.getComplianceStatus()).isEqualTo(ComplianceStatus.A);
    }
}
