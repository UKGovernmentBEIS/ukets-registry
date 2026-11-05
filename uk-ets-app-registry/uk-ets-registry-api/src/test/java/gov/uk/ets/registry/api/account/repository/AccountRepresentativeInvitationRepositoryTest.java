package gov.uk.ets.registry.api.account.repository;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.AccountHolderRepresentative;
import gov.uk.ets.registry.api.account.domain.AccountRepresentativeInvitation;
import gov.uk.ets.registry.api.account.domain.types.AccountContactType;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.common.model.entities.Contact;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create"
})
class AccountRepresentativeInvitationRepositoryTest {

    private static final long ACCOUNT_IDENTIFIER = 10000001L;
    private static final String CONTACT_EMAIL = "contact@email.com";
    private static final String ALT_CONTACT_EMAIL = "altcontact@email.com";

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AccountRepresentativeInvitationRepository accountRepresentativeInvitationRepository;

    private Long accountId;
    private Long representativeId;
    private Long altRepresentativeId;

    @BeforeEach
    void setUp() {
        AccountHolder accountHolder = new AccountHolder();
        accountHolder.setIdentifier(10000036L);
        accountHolder.setType(AccountHolderType.ORGANISATION);
        accountHolder.setName("Organisation");
        entityManager.persist(accountHolder);
        flushAndClear();

        Contact contact = new Contact();
        contact.setContactName("contact");
        contact.setEmailAddress(CONTACT_EMAIL);
        entityManager.persist(contact);

        Contact contact2 = new Contact();
        contact2.setContactName("contact");
        contact2.setEmailAddress(ALT_CONTACT_EMAIL);
        entityManager.persist(contact2);
        flushAndClear();

        AccountHolderRepresentative accountHolderRepresentative = new AccountHolderRepresentative();
        accountHolderRepresentative.setAccountHolder(accountHolder);
        accountHolderRepresentative.setFirstName("Firstname");
        accountHolderRepresentative.setLastName("Lastname");
        accountHolderRepresentative.setAccountContactType(AccountContactType.PRIMARY);
        accountHolderRepresentative.setBirthDate(Date.valueOf(LocalDate.of(1990, 1, 1)));
        accountHolderRepresentative.setContact(contact);
        entityManager.persist(accountHolderRepresentative);

        AccountHolderRepresentative altAccountHolderRepresentative = new AccountHolderRepresentative();
        altAccountHolderRepresentative.setAccountHolder(accountHolder);
        altAccountHolderRepresentative.setFirstName("Firstname");
        altAccountHolderRepresentative.setLastName("Lastname");
        altAccountHolderRepresentative.setAccountContactType(AccountContactType.ALTERNATIVE);
        altAccountHolderRepresentative.setBirthDate(Date.valueOf(LocalDate.of(1990, 2, 2)));
        altAccountHolderRepresentative.setContact(contact2);
        entityManager.persist(altAccountHolderRepresentative);

        Account account = new Account();
        account.setAccountName("account");
        account.setRegistryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);
        account.setIdentifier(ACCOUNT_IDENTIFIER);
        account.setAccountHolder(accountHolder);
        entityManager.persist(account);
        flushAndClear();
        AccountRepresentativeInvitation accountRepresentativeInvitation = AccountRepresentativeInvitation.builder()
                .account(account)
                .accountHolderRepresentative(accountHolderRepresentative)
                .invitedDate(LocalDateTime.of(2026, 1, 1, 12, 0))
                .invitationStatus(true)
                .build();
        entityManager.persist(accountRepresentativeInvitation);

        AccountRepresentativeInvitation accountRepresentativeInvitation2 = AccountRepresentativeInvitation.builder()
                .account(account)
                .accountHolderRepresentative(altAccountHolderRepresentative)
                .invitedDate(LocalDateTime.of(2026, 2, 2, 12, 0))
                .invitationStatus(false)
                .build();
        entityManager.persist(accountRepresentativeInvitation2);

        accountId = account.getId();
        representativeId = accountHolderRepresentative.getId();
        altRepresentativeId = altAccountHolderRepresentative.getId();

        flushAndClear();
    }

    @Test
    void findByRepresentativeIdAndAccountId() {

        final Optional<AccountRepresentativeInvitation> actual =
                accountRepresentativeInvitationRepository.findByRepresentativeIdAndAccountId(representativeId, accountId);

        assertThat(actual).isPresent();
        assertThat(actual.get().getInvitedDate()).isEqualTo(LocalDateTime.of(2026, 1, 1, 12, 0));
        assertTrue(actual.get().isInvitationStatus());
    }

    @Test
    void findByRepresentativeIdAndAccountId_returnsEmptyWhenNoInvitationExists() {

        Optional<AccountRepresentativeInvitation> result =
                accountRepresentativeInvitationRepository.findByRepresentativeIdAndAccountId(
                        999999L,
                        999999L
                );

        assertThat(result).isEmpty();
    }

    @Test
    void findByRepresentativeIdInAndAccountId() {

        final List<AccountRepresentativeInvitation> actual =
                accountRepresentativeInvitationRepository
                        .findByRepresentativeIdInAndAccountId(List.of(representativeId, altRepresentativeId), accountId);

        assertThat(actual).isNotEmpty();
        assertThat(actual).extracting(AccountRepresentativeInvitation::getInvitedDate)
                .containsExactlyInAnyOrder(LocalDateTime.of(2026, 1, 1, 12, 0),
                        LocalDateTime.of(2026, 2, 2, 12, 0));
        assertThat(actual).extracting(AccountRepresentativeInvitation::isInvitationStatus)
                .containsExactly(true, false);
    }

    @Test
    void findByAccount_IdentifierAndAccountHolderRepresentative_Contact_EmailAddressIn() {

        final List<AccountRepresentativeInvitation> actual =
                accountRepresentativeInvitationRepository
                        .findByAccount_IdentifierAndAccountHolderRepresentative_Contact_EmailAddressIn(ACCOUNT_IDENTIFIER,
                                Set.of(CONTACT_EMAIL, "other@email.com"));

        assertThat(actual).extracting(AccountRepresentativeInvitation::getInvitedDate).containsExactly(LocalDateTime.of(2026, 1, 1, 12, 0));
        assertThat(actual).extracting(AccountRepresentativeInvitation::isInvitationStatus).containsExactly(true);
    }

    @Test
    void findByAccount_IdentifierAndAccountHolderRepresentative_Contact_EmailAddressIn_Two_Results() {

        final List<AccountRepresentativeInvitation> actual =
                accountRepresentativeInvitationRepository
                        .findByAccount_IdentifierAndAccountHolderRepresentative_Contact_EmailAddressIn(ACCOUNT_IDENTIFIER,
                                Set.of(CONTACT_EMAIL, ALT_CONTACT_EMAIL));

        assertThat(actual).extracting(AccountRepresentativeInvitation::getInvitedDate).containsExactlyInAnyOrder(LocalDateTime.of(2026, 1, 1, 12, 0),
                                                                                                                 LocalDateTime.of(2026, 2, 2, 12, 0));
        assertThat(actual).extracting(AccountRepresentativeInvitation::isInvitationStatus).containsExactlyInAnyOrder(true,false);
    }
    
    @Test
    void findByAccountIdentifierAndContactTypeAndEmailAddressShouldFind() {

        final Optional<AccountRepresentativeInvitation> actual =
                accountRepresentativeInvitationRepository
                        .findByAccountIdentifierAndContactTypeAndEmailAddress(ACCOUNT_IDENTIFIER,AccountContactType.PRIMARY,CONTACT_EMAIL);

        assertThat(actual).isPresent();
    }

    @Test
    void findByAccountIdentifierAndContactTypeAndEmailAddressShouldNotFind() {

        final Optional<AccountRepresentativeInvitation> actual =
                accountRepresentativeInvitationRepository
                        .findByAccountIdentifierAndContactTypeAndEmailAddress(ACCOUNT_IDENTIFIER,AccountContactType.PRIMARY,ALT_CONTACT_EMAIL);

        assertThat(actual).isEmpty();
    }    
    
    @Test
    void findByAccount_IdentifierAndAccountHolderRepresentative_Contact_EmailAddressIn_returnsEmptyWhenNoInvitationExists() {

        final List<AccountRepresentativeInvitation> actual =
                accountRepresentativeInvitationRepository
                        .findByAccount_IdentifierAndAccountHolderRepresentative_Contact_EmailAddressIn(99999L,
                                Set.of(CONTACT_EMAIL, "other@email.com"));

        assertThat(actual).isEmpty();
    }

    @Test
    void findByAccount_IdentifierAndAccountHolderRepresentative_Contact_EmailAddressIn_returnsEmptyWhenWrongEmail() {

        final List<AccountRepresentativeInvitation> actual =
                accountRepresentativeInvitationRepository
                        .findByAccount_IdentifierAndAccountHolderRepresentative_Contact_EmailAddressIn(ACCOUNT_IDENTIFIER,
                                Set.of("wrong@email.com"));

        assertThat(actual).isEmpty();
    }

    @Test
    void countByAccount_IdentifierAndInvitationStatus() {

        final Long activeCount = accountRepresentativeInvitationRepository
                .countByAccount_IdentifierAndInvitationStatus(ACCOUNT_IDENTIFIER, true);

        final Long disabledCount = accountRepresentativeInvitationRepository
                .countByAccount_IdentifierAndInvitationStatus(ACCOUNT_IDENTIFIER, false);

        assertThat(activeCount).isEqualTo(1L);
        assertThat(disabledCount).isEqualTo(1L);
    }

    @Test
    void countByAccount_IdentifierAndInvitationStatus_returnsZeroWhenNoInvitationExists() {

        final Long activeCount = accountRepresentativeInvitationRepository
                .countByAccount_IdentifierAndInvitationStatus(1L, true);

        assertThat(activeCount).isEqualTo(0L);
    }

    @Test
    void deleteByAccountHolderRepresentative_Id_shouldDeleteInvitation() {
        accountRepresentativeInvitationRepository.deleteByAccountHolderRepresentative_Id(altRepresentativeId);
        flushAndClear();
        final Optional<AccountRepresentativeInvitation> actual =
                accountRepresentativeInvitationRepository.findByRepresentativeIdAndAccountId(altRepresentativeId, accountId);
        assertThat(actual).isEmpty();
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
