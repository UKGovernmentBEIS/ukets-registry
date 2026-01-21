package gov.uk.ets.registry.api.compliance.messaging;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import gov.uk.ets.registry.api.compliance.messaging.events.outgoing.ChangeYearEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import net.javacrumbs.shedlock.core.LockAssert;

/**
 * Note that this does not test the scheduler but rather the logic inside the
 * execute method.
 * 
 * @author P35036
 *
 */
@DisplayName("Change Year Event for Compliance Status tests")
@DataJpaTest
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create",
    "spring.jpa.properties.hibernate.globally_quoted_identifiers=true",
    "spring.jpa.properties.hibernate.globally_quoted_identifiers_skip_column_definitions=true"})
class ChangeYearEventSchedulerTest {

    private ChangeYearEventScheduler changeYearEventScheduler;

    @Mock
    private ComplianceEventService service;
    @Mock
    private AccountRepository accountRepository;

    private Account account;
    private CompliantEntity compliantEntity;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        // We need this due to LockAssert.assertLocked() inside the execute
        LockAssert.TestHelper.makeAllAssertsPass(true);
        changeYearEventScheduler = new ChangeYearEventScheduler(service, accountRepository);

        account = new Account();
        account.setIdentifier(10001L);
        account.setFullIdentifier("UK-100-10001-325");
        account.setAccountName("Test-Account-1");
        account.setAccountStatus(AccountStatus.OPEN);
        account.setComplianceStatus(ComplianceStatus.A);

        compliantEntity = new Installation();
        compliantEntity.setAccount(account);
        compliantEntity.setIdentifier(123L);
        compliantEntity.setRegulator(RegulatorType.EA);

        account.setCompliantEntity(compliantEntity);
    }

    @Test
    @DisplayName("Send a Change year event")
    void changeYearEvent() {
        Mockito.when(
                accountRepository.findActiveAccountsWithCompliantEntities())
                .thenReturn(List.of(account));
        
        //Run the Scheduler
        changeYearEventScheduler.execute();

        verify(service, times(1)).processEvent(any(ChangeYearEvent.class));
    }

}
