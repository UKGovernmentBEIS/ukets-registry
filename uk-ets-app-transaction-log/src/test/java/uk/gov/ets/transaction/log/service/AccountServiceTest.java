package uk.gov.ets.transaction.log.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import uk.gov.ets.transaction.log.domain.Account;
import uk.gov.ets.transaction.log.messaging.types.AccountNotification;
import uk.gov.ets.transaction.log.messaging.types.AccountOpeningAnswer;
import uk.gov.ets.transaction.log.repository.AccountRepository;

@DisplayName("Testing account related service methods")
public class AccountServiceTest {

    private AccountService accountService;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private KafkaTemplate<String, AccountOpeningAnswer> kafkaTemplate;
    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        accountService = new AccountService(accountRepository, kafkaTemplate);
    }
    
    @Test
    void acceptAccountNotification() {
        Mockito.when(accountRepository.save(Mockito.any(Account.class))).thenAnswer(i -> i.getArguments()[0]);
        AccountNotification accountNotification = new AccountNotification();
        Account account = accountService.acceptAccountOpeningRequest(accountNotification);
        assertNotNull(account);
    }
    
    @Test
    void toAccount() {
        Date now = new Date();
        AccountNotification accountNotification = new AccountNotification();
        accountNotification.setAccountName("UK Auction Account");
        accountNotification.setCheckDigits(68);
        accountNotification.setCommitmentPeriodCode(2);
        accountNotification.setFullIdentifier("UK-100-10000988-0-68");
        accountNotification.setIdentifier(10000988L);
        accountNotification.setOpeningDate(now);
        
        Account account = accountService.toAccount(accountNotification);
        assertEquals(accountNotification.getAccountName(),account.getAccountName());
        assertEquals(accountNotification.getCheckDigits(),account.getCheckDigits());
        assertEquals(accountNotification.getCommitmentPeriodCode(),account.getCommitmentPeriodCode());
        assertEquals(accountNotification.getFullIdentifier(),account.getFullIdentifier());
        assertEquals(accountNotification.getIdentifier(),account.getIdentifier());
        assertEquals(accountNotification.getOpeningDate(),account.getOpeningDate());
    }
}
