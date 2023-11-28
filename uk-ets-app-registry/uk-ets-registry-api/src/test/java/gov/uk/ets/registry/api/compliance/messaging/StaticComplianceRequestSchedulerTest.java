package gov.uk.ets.registry.api.compliance.messaging;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.compliance.messaging.events.outgoing.StaticComplianceRequestEvent;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import net.javacrumbs.shedlock.core.LockAssert;
import org.apache.sis.internal.util.StandardDateFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StaticComplianceRequestSchedulerTest {

    @Mock
    private ComplianceEventService service;
    @Mock
    private AccountRepository repository;

    private StaticComplianceRequestScheduler scheduler;

    @BeforeEach
    public void setup() {
        LockAssert.TestHelper.makeAllAssertsPass(true);
        scheduler = new StaticComplianceRequestScheduler(service, repository, "04-30T23:00:00");
    }


    @Test
    void testExecute() {
        // given
        var now = LocalDateTime.now(ZoneId.of(StandardDateFormat.UTC));
        var account1 = createAccount(Date.from(now.minusYears(2).toInstant(ZoneOffset.UTC)));
        var account2 = createAccount(Date.from(now.minusYears(1).toInstant(ZoneOffset.UTC)));
        var account3 = createAccount(null);
        List<Account> accounts = List.of(account1, account2, account3);

        when(repository.findOHA_AOHAAccountsWithCompliantEntities()).thenReturn(accounts);
        when(service.skipStaticComplianceStatusRequestForEntity(account2.getCompliantEntity(), now.getYear())).thenReturn(false);
        when(service.skipStaticComplianceStatusRequestForEntity(account3.getCompliantEntity(), now.getYear())).thenReturn(false);

        // when
        scheduler.execute();

        // then
        verify(service, times(2)).processEvent(any(StaticComplianceRequestEvent.class));
    }

    private Account createAccount(Date closingDate) {
        Account account = new Account();
        account.setClosingDate(closingDate);

        CompliantEntity compliantEntity = new Installation();
        compliantEntity.setAccount(account);
        compliantEntity.setIdentifier(123L);

        account.setCompliantEntity(compliantEntity);

        return account;
    }

}
