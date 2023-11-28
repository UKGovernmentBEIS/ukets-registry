package gov.uk.ets.registry.api.tal.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.tal.repository.TrustedAccountRepository;
import java.util.ArrayList;
import net.javacrumbs.shedlock.core.LockAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TrustedAccountListSchedulerTest {

    @Mock
    private TrustedAccountRepository trustedAccountRepository;

    @InjectMocks
    private TrustedAccountListScheduler trustedAccountListScheduler;

    @BeforeEach
    void setUp() {
        LockAssert.TestHelper.makeAllAssertsPass(true);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void noDelayedTrustedAccountsFound() {
        when(trustedAccountRepository.findByStatusEqualsAndActivationDateBefore(any(), any()))
            .thenReturn(new ArrayList<>());
        trustedAccountListScheduler.processDelayedTrustedAccounts();
        verify(trustedAccountRepository, times(1)).findByStatusEqualsAndActivationDateBefore(any(), any());
    }
}
