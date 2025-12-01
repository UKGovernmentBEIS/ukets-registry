package gov.uk.ets.registry.api.account.repository;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.MetsAccountContact;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.web.model.accountcontact.MetsAccountContactType;
import gov.uk.ets.registry.api.account.web.model.accountcontact.OperatorType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create"
})
class MetsAccountContactRepositoryTest {

    private static final long ACCOUNT_IDENTIFIER_1 = 10000001L;
    private static final long ACCOUNT_IDENTIFIER_2 = 10000002L;
    private static final long ACCOUNT_IDENTIFIER_3 = 10000003L;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AccountContactRepository accountContactRepository;


    @BeforeEach
    void setUp() {
        AccountHolder accountHolder = new AccountHolder();
        accountHolder.setIdentifier(10000036L);
        accountHolder.setType(AccountHolderType.ORGANISATION);
        accountHolder.setName("Organisation");
        entityManager.persist(accountHolder);
        flushAndClear();


        Account account1 = new Account();
        account1.setAccountName("account1");
        account1.setRegistryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);
        account1.setIdentifier(ACCOUNT_IDENTIFIER_1);
        account1.setAccountHolder(accountHolder);
        Account account2 = new Account();
        account2.setAccountName("account2");
        account2.setRegistryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);
        account2.setIdentifier(ACCOUNT_IDENTIFIER_2);
        account2.setAccountHolder(accountHolder);
        Account account3 = new Account();
        account3.setAccountName("account2");
        account3.setRegistryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);
        account3.setIdentifier(ACCOUNT_IDENTIFIER_3);
        account3.setAccountHolder(accountHolder);
        entityManager.persist(account1);
        entityManager.persist(account2);
        entityManager.persist(account3);
        flushAndClear();
        final MetsAccountContact metsAccountContact1 = createAccountContact("contact 1", "contact1@email.com",
                Set.of(MetsAccountContactType.PRIMARY, MetsAccountContactType.SERVICE), account1);
        final MetsAccountContact metsAccountContact2 = createAccountContact("contact 2", "contact2@email.com",
                Set.of(MetsAccountContactType.SECONDARY), account1);
        final MetsAccountContact metsAccountContact3 = createAccountContact("contact 3", "contact3@email.com",
                Set.of(), account2);
        entityManager.persist(metsAccountContact1);
        entityManager.persist(metsAccountContact2);
        entityManager.persist(metsAccountContact3);
        flushAndClear();
    }

    private MetsAccountContact createAccountContact(String name, String emailAddress,
                                                    Set<MetsAccountContactType> contactTypes, Account account) {
        return MetsAccountContact.builder()
                .name(name)
                .emailAddress(emailAddress)
                .countryCode1("(UK) 44")
                .phoneNumber1("123456789")
                .operatorType(OperatorType.OPERATOR_ADMIN)
                .invitedOn(LocalDateTime.now())
                .contactTypes(contactTypes)
                .account(account)
                .build();
    }

    @Test
    void getAccountHoldersByIdentifier() {
        List<MetsAccountContact> metsAccountContacts1 =
                accountContactRepository.findByAccountIdentifier(ACCOUNT_IDENTIFIER_1);
        List<MetsAccountContact> metsAccountContacts2 =
                accountContactRepository.findByAccountIdentifier(ACCOUNT_IDENTIFIER_2);
        List<MetsAccountContact> metsAccountContacts3 =
                accountContactRepository.findByAccountIdentifier(ACCOUNT_IDENTIFIER_3);

        assertThat(metsAccountContacts1).extracting(MetsAccountContact::getName)
                .containsExactlyInAnyOrder("contact 1", "contact 2");
        assertThat(metsAccountContacts1).extracting(MetsAccountContact::getEmailAddress)
                .containsExactlyInAnyOrder("contact1@email.com", "contact2@email.com");
        assertThat(metsAccountContacts1).extracting(MetsAccountContact::getContactTypes)
                .containsExactlyInAnyOrder(Set.of(MetsAccountContactType.PRIMARY, MetsAccountContactType.SERVICE), Set.of(MetsAccountContactType.SECONDARY));
        assertThat(metsAccountContacts2).extracting(MetsAccountContact::getName).containsExactly("contact 3");
        assertThat(metsAccountContacts2).extracting(MetsAccountContact::getEmailAddress).containsExactly("contact3@email.com");
        assertThat(metsAccountContacts2).hasSize(1);
        assertThat(metsAccountContacts2.get(0).getContactTypes()).isEmpty();
        assertThat(metsAccountContacts3).isEmpty();
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
