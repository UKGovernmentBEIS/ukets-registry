package uk.gov.ets.transaction.log.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import uk.gov.ets.transaction.log.domain.Account;
import uk.gov.ets.transaction.log.messaging.types.AccountNotification;
import uk.gov.ets.transaction.log.messaging.types.AccountOpeningAnswer;
import uk.gov.ets.transaction.log.repository.AccountRepository;


@Service
@AllArgsConstructor
@Log4j2
public class AccountService {

    private final AccountRepository accountRepository;

    private final KafkaTemplate<String, AccountOpeningAnswer> kafkaTemplate;

    private static final String TXLOG_ORIGINATING_ACCOUNT_OPENING_ANSWER_TOPIC = "txlog.originating.account-opening.answer.topic";


    /**
     * Process an incoming AccountNotification.
     * 
     * @param accountNotification the incoming account from the registry.
     * @return the Account.
     */
    public Account acceptAccountOpeningRequest(AccountNotification accountNotification) {
        log.info("Received account opening request: {}", accountNotification);
        Account account = accountRepository.save(toAccount(accountNotification));
        sendUKTLAccountAnswer(accountNotification);
        return account;
    }

    private void sendUKTLAccountAnswer(AccountNotification accountNotification) {

        kafkaTemplate.send(TXLOG_ORIGINATING_ACCOUNT_OPENING_ANSWER_TOPIC,
                           AccountOpeningAnswer.builder()
                                               .oldIdentifier(accountNotification.getOldIdentifier())
                                               .identifier(accountNotification.getIdentifier())
                                               .fullIdentifier(accountNotification.getFullIdentifier())
                                               .build());
    }
    
    protected Account toAccount(AccountNotification accountNotification) {
        Account account = new Account();
        account.setAccountName(accountNotification.getAccountName());
        account.setFullIdentifier(accountNotification.getFullIdentifier());
        account.setCheckDigits(accountNotification.getCheckDigits());
        account.setCommitmentPeriodCode(accountNotification.getCommitmentPeriodCode());
        account.setIdentifier(accountNotification.getIdentifier());
        account.setOpeningDate(accountNotification.getOpeningDate());
        
        return account;
    }
}
