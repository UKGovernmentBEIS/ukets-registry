package gov.uk.ets.registry.api.ar.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.QAccount;
import gov.uk.ets.registry.api.account.domain.QAccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.ar.domain.ARAccountAccessRepository;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddAccountCommand;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddAccountHolderCommand;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddUserToAccountAccessCommand;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create"})
@ExtendWith(MockitoExtension.class)
class ARAccountAccessRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    private String urid = "UK98567654";
    private Long accountIdentifier = 101L;

    private AccountModelTestHelper helper;

    private ARAccountAccessRepository repository;


    @BeforeEach
    public void setup() {
        repository = new ARAccountAccessRepositoryImpl(entityManager);
        helper = new AccountModelTestHelper(entityManager);

        AddAccountHolderCommand addAccountHolderCommand1 = AddAccountHolderCommand.builder()
            .accountHolderType(AccountHolderType.ORGANISATION)
            .identifier(100L)
            .name("Test account holder name 1")
            .status(Status.ACTIVE)
            .build();
        AccountHolder accountHolder1 = helper.addAccountHolder(addAccountHolderCommand1);

        AddAccountHolderCommand addAccountHolderCommand2 = AddAccountHolderCommand.builder()
            .accountHolderType(AccountHolderType.ORGANISATION)
            .identifier(100L)
            .name("Test account holder name 2")
            .status(Status.ACTIVE)
            .build();
        AccountHolder accountHolder2 = helper.addAccountHolder(addAccountHolderCommand2);

        addAccountAndAuthorizedRepresentatives(
            AddAccountCommand.builder()
                .accountHolder(accountHolder1)
                .accountId(accountIdentifier)
                .fullIdentifier("UK-100-123asdsad456-324")
                .accountName("Test-Account")
                .accountStatus(AccountStatus.OPEN)
                .complianceStatus(ComplianceStatus.A)
                .registryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT)
                .kyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT)
                .build(),
            AddUserToAccountAccessCommand.builder()
                .enrollmentKey("XXXXXX1")
                .state(AccountAccessState.ACTIVE)
                .urid(urid)
                .build(),
            AddUserToAccountAccessCommand.builder()
                .enrollmentKey("XXXXX2")
                .state(AccountAccessState.ACTIVE)
                .urid(urid + "1")
                .build(),
            AddUserToAccountAccessCommand.builder()
                .enrollmentKey("XXXXX3")
                .state(AccountAccessState.ACTIVE)
                .urid(urid + "20")
                .right(AccountAccessRight.ROLE_BASED)
                .build(),
            AddUserToAccountAccessCommand.builder()
                .enrollmentKey("XXXXXX3")
                .state(AccountAccessState.SUSPENDED)
                .urid(urid + "2")
                .build(),
            AddUserToAccountAccessCommand.builder()
                .enrollmentKey("XXXXXX4")
                .state(AccountAccessState.REMOVED)
                .urid(urid + "3")
                .build());

        addAccountAndAuthorizedRepresentatives(
            AddAccountCommand.builder()
                .accountHolder(accountHolder2)
                .accountId(104L)
                .fullIdentifier("UK-100-123456-324")
                .accountName("Test-Account")
                .accountStatus(AccountStatus.OPEN)
                .complianceStatus(ComplianceStatus.A)
                .registryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT)
                .kyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT)
                .build(),
            AddUserToAccountAccessCommand.builder()
                .enrollmentKey("XXXXXXX4")
                .state(AccountAccessState.ACTIVE)
                .urid(urid)
                .build(),
            AddUserToAccountAccessCommand.builder()
                .enrollmentKey("XXXXXXX5")
                .state(AccountAccessState.ACTIVE)
                .urid(urid + "3")
                .build(),
            AddUserToAccountAccessCommand.builder()
                .enrollmentKey("XXXXXXX6")
                .state(AccountAccessState.SUSPENDED)
                .urid(urid + "4")
                .build());


        addAccountAndAuthorizedRepresentatives(
            AddAccountCommand.builder()
                .accountHolder(accountHolder1)
                .accountId(204L)
                .fullIdentifier("UK-100-123456-32433")
                .accountName("Test-Account")
                .accountStatus(AccountStatus.OPEN)
                .complianceStatus(ComplianceStatus.A)
                .registryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT)
                .kyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT)
                .build(),
            AddUserToAccountAccessCommand.builder()
                .enrollmentKey("XXXXXXX43")
                .state(AccountAccessState.ACTIVE)
                .urid(urid + "10")
                .build(),
            AddUserToAccountAccessCommand.builder()
                .enrollmentKey("XXXXXXX53")
                .state(AccountAccessState.ACTIVE)
                .urid(urid + "11")
                .build(),
            AddUserToAccountAccessCommand.builder()
                .enrollmentKey("XXXXXXX63")
                .state(AccountAccessState.SUSPENDED)
                .urid(urid + "12")
                .build());

        addAccountAndAuthorizedRepresentatives(
            AddAccountCommand.builder()
                .accountHolder(accountHolder1)
                .accountId(205L)
                .fullIdentifier("UK-100-123456-3243322")
                .accountName("Test-Account")
                .accountStatus(AccountStatus.OPEN)
                .complianceStatus(ComplianceStatus.A)
                .registryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT)
                .kyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT)
                .build(),
            AddUserToAccountAccessCommand.builder()
                .enrollmentKey("XXXXXXX43WW")
                .state(AccountAccessState.ACTIVE)
                .urid(urid)
                .build(),
            AddUserToAccountAccessCommand.builder()
                .enrollmentKey("XXXXXXX53WW")
                .state(AccountAccessState.ACTIVE)
                .urid(urid + "11")
                .build(),
            AddUserToAccountAccessCommand.builder()
                .enrollmentKey("XXXXXXX63WW")
                .state(AccountAccessState.SUSPENDED)
                .urid(urid + "12")
                .build());

        addAccountAndAuthorizedRepresentatives(
            AddAccountCommand.builder()
                .accountHolder(accountHolder1)
                .accountId(10432L)
                .fullIdentifier("nonenrolledUK-100-123456-324")
                .accountName("Test-Account")
                .accountStatus(AccountStatus.OPEN)
                .complianceStatus(ComplianceStatus.A)
                .registryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT)
                .kyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT)
                .build(),
            AddUserToAccountAccessCommand.builder()
                .enrollmentKey("XXXXXXX4")
                .state(AccountAccessState.ACTIVE)
                .urid(urid)
                .build(),
            AddUserToAccountAccessCommand.builder()
                .enrollmentKey("nonenrolled3XXXXXXX5")
                .state(AccountAccessState.ACTIVE)
                .urid(urid + "nonenrolled3")
                .userStatus(UserStatus.REGISTERED)
                .build());
    }

    @Test
    @DisplayName("The user selects from a list of non-suspended ARs from accounts under which the user is an AR.")
    void fetchOtherARs() {
        // when
        List<AccountAccess> result = repository.fetchArsForAccount(accountIdentifier, urid);

        // then
        assertNotNull(result);
        assertTrue(result.size() > 0);
        assertNotEquals(2, result.size());
        result.stream().forEach(ar -> {
            assertNotSame(AccountAccessState.SUSPENDED, ar.getState());
            assertNotSame(accountIdentifier, ar.getAccount().getIdentifier());
        });

        //Should not contain ACTIVE or SUSPENDED from the same account
        //Get All Excluded Authorized Representatives Of Account
        List<AccountAccess> activeOrSuspendedRepresentativesOfTheAccount =
            getActiveOrSuspendedRepresentativesOfAccount(accountIdentifier);
        activeOrSuspendedRepresentativesOfTheAccount.stream().forEach(ar -> assertFalse(result.contains(ar)));

        //Make sure the result does not contain self.
        assertFalse(result.stream().anyMatch(accountAccess -> accountAccess.getUser().getUrid().equals(urid)));
    }


    @Test
    @DisplayName("The user selects from a list of non-suspended ARs from accounts under the same Account Holder of the given account.")
    void fetchSameAccountHolderARs() {
        // when
        List<AccountAccess> result = repository.fetchArsForAccount(accountIdentifier, null);

        // then
        assertNotNull(result);
        assertTrue(result.size() > 0);
        assertNotEquals(3, result.size());
        result.stream().forEach(ar -> {
            assertNotSame(AccountAccessState.SUSPENDED, ar.getState());
            assertNotSame(accountIdentifier, ar.getAccount().getIdentifier());
        });

        //Should not contain ACTIVE or SUSPENDED from the same account
        //Get All Excluded Authorized Representatives Of Account
        List<AccountAccess> activeOrSuspendedRepresentativesOfTheAccount =
            getActiveOrSuspendedRepresentativesOfAccount(accountIdentifier);
        activeOrSuspendedRepresentativesOfTheAccount.stream().forEach(ar -> assertFalse(result.contains(ar)));
    }

    @ParameterizedTest
    @MethodSource("provideAccessStateForFetchARs")
    void fetchARs(AccountAccessState state) {
        List<AccountAccess> allAccountAccessesInAccount = getARsOfTheAccount(accountIdentifier);
        assertTrue(allAccountAccessesInAccount.size() > 0);
        List<AccountAccess> result = repository.fetchARs(accountIdentifier, state);
        if (state == null) {
            assertEquals(allAccountAccessesInAccount.size(), result.size());
            allAccountAccessesInAccount.stream().forEach(accountAccess -> assertTrue(result.contains(accountAccess)));
        } else {
            Stream<AccountAccess> sameStateExpectedResult =
                allAccountAccessesInAccount.stream().filter(accountAccess -> accountAccess.getState() == state);
            sameStateExpectedResult.forEach(r -> assertTrue(result.contains(r)));
        }
    }

    //Query the account_access for Representatives in ACTIVE or SUSPENDED state
    private List<AccountAccess> getActiveOrSuspendedRepresentativesOfAccount(long accountIdentifier) {
        return new JPAQuery<AccountAccess>(entityManager)
            .from(QAccountAccess.accountAccess)
            .where(QAccountAccess.accountAccess.account.identifier.eq(accountIdentifier).
                and(QAccountAccess.accountAccess.state.in(AccountAccessState.ACTIVE, AccountAccessState.SUSPENDED)))
            .fetch();
    }

    private List<AccountAccess> getARsOfTheAccount(long accountIdentifier) {
        QAccount account = new QAccount("alias");
        return new JPAQuery<AccountAccess>(entityManager)
            .from(QAccountAccess.accountAccess).join(QAccountAccess.accountAccess.account, account)
            .on(account.identifier.eq(accountIdentifier))
            .where(QAccountAccess.accountAccess.state.ne(AccountAccessState.REMOVED)
                .and(QAccountAccess.accountAccess.right.ne(AccountAccessRight.ROLE_BASED)))
            .fetch();
    }

    private static Stream<Arguments> provideAccessStateForFetchARs() {
        return Stream.of(Arguments.of(AccountAccessState.ACTIVE), Arguments.of(AccountAccessState.SUSPENDED), null);
    }


    private void addAccountAndAuthorizedRepresentatives(AddAccountCommand addAccountCommand,
                                                        AddUserToAccountAccessCommand... addUserToAccountAccessCommands) {
        Account newAccount = helper.addAccount(addAccountCommand);
        Arrays.stream(addUserToAccountAccessCommands).forEach(addUserToAccountAccessCommand -> {
            addUserToAccountAccessCommand.setAccount(newAccount);
            helper.addUserToAccountAccess(addUserToAccountAccessCommand);
        });
    }
}
