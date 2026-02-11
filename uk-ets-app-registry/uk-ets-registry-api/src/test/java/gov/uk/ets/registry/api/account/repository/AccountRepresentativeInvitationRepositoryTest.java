package gov.uk.ets.registry.api.account.repository;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.AccountHolderRepresentative;
import gov.uk.ets.registry.api.account.domain.AccountRepresentativeInvitation;
import gov.uk.ets.registry.api.account.domain.types.AccountContactType;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create"
})
class AccountRepresentativeInvitationRepositoryTest {

    private static final long ACCOUNT_IDENTIFIER = 10000001L;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AccountRepresentativeInvitationRepository accountRepresentativeInvitationRepository;

    private Long accountId;
    private Long representativeId;

    @BeforeEach
    void setUp() {
        AccountHolder accountHolder = new AccountHolder();
        accountHolder.setIdentifier(10000036L);
        accountHolder.setType(AccountHolderType.ORGANISATION);
        accountHolder.setName("Organisation");
        entityManager.persist(accountHolder);
        flushAndClear();

        AccountHolderRepresentative accountHolderRepresentative = new AccountHolderRepresentative();
        accountHolderRepresentative.setAccountHolder(accountHolder);
        accountHolderRepresentative.setFirstName("Firstname");
        accountHolderRepresentative.setLastName("Lastname");
        accountHolderRepresentative.setAccountContactType(AccountContactType.PRIMARY);
        accountHolderRepresentative.setBirthDate(Date.valueOf(LocalDate.of(1990, 1, 1)));
        entityManager.persist(accountHolderRepresentative);

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
                .build();
        entityManager.persist(accountRepresentativeInvitation);

        accountId = account.getId();
        representativeId = accountHolderRepresentative.getId();

        flushAndClear();
    }

    @Test
    void findDateByRepresentativeIdAndAccountId() {

        final Optional<LocalDateTime> actual =
                accountRepresentativeInvitationRepository.findDateByRepresentativeIdAndAccountId(representativeId, accountId);

        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo(LocalDateTime.of(2026, 1, 1, 12, 0));
    }

    @Test
    void findDateByRepresentativeIdAndAccountId_returnsEmptyWhenNoInvitationExists() {

        Optional<LocalDateTime> result =
                accountRepresentativeInvitationRepository.findDateByRepresentativeIdAndAccountId(
                        999999L,
                        999999L
                );

        assertThat(result).isEmpty();
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
