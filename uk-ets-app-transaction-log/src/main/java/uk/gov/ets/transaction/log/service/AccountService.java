package uk.gov.ets.transaction.log.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.ets.transaction.log.domain.Account;
import uk.gov.ets.transaction.log.messaging.types.AccountNotification;
import uk.gov.ets.transaction.log.messaging.types.AccountOpeningAnswer;
import uk.gov.ets.transaction.log.repository.AccountRepository;


@Service
@RequiredArgsConstructor
@Log4j2
public class AccountService {

    @Value("${kafka.account-opening-uktl.answer.topic:registry-internal-txlog-originating-account-opening-answer-topic}")
    private String accountOpeningAnswerTopic;

    private final AccountRepository accountRepository;

    private final KafkaTemplate<String, AccountOpeningAnswer> accountOpeningKafkaTemplate;

    /**
     * Process an incoming AccountNotification.
     * 
     * @param accountNotification the incoming account from the registry.
     * @return the Account.
     */
    @Transactional
    public Account acceptAccountOpeningRequest(AccountNotification accountNotification) {
        log.info("Received account opening request: {}", accountNotification);
        Account account = accountRepository.save(toAccount(accountNotification));
        sendUKTLAccountAnswer(accountNotification);
        return account;
    }

    private void sendUKTLAccountAnswer(AccountNotification accountNotification) {

        accountOpeningKafkaTemplate.send(accountOpeningAnswerTopic,
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
