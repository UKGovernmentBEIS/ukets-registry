package gov.uk.ets.registry.api.account.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.user.domain.User;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create"
})
public class AccountAccessRepositoryTest {

    public static final long TEST_ACCOUNT_IDENTIFIER = 1222L;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AccountAccessRepository accountAccessRepository;

    private String PRIMARY_AUTH_REP_IAM_IDENTIFIER = "aa6cf338-9be5-4ed5-b5a2-c8a1550b60dd";
    private String SECONDARY_AUTH_REP_IAM_IDENTIFIER = "f2aeabe7-7af6-44bb-a132-2b5cad2ae42b";
    private String ADD_AUTH_REP_1_IAM_IDENTIFIER = "cfd7b704-f861-4444-b430-818201dc0fd9";
    private String ADD_AUTH_REP_2_IAM_IDENTIFIER = "bf77b072-8946-4284-bc0b-2248a321ba8b";

    @BeforeEach
    public void setUp() throws Exception {
        User initiator = getInitiator();
        entityManager.persist(initiator);
        User claimant = getClaimant();
        entityManager.persist(claimant);
        User primaryRepresentative = getPrimaryAuthorizedRepresentative();
        entityManager.persist(primaryRepresentative);
        User secondaryRepresentative = getSecondaryAuthorizedRepresentative();
        entityManager.persist(secondaryRepresentative);
        User additionalRepresentative1 = getAdditionalAuthorizedRepresentative_1();
        entityManager.persist(additionalRepresentative1);
        User additionalRepresentative2 = getAdditionalAuthorizedRepresentative_2();
        entityManager.persist(additionalRepresentative2);

        AccountHolder accountHolder = getAccountHolder();
        entityManager.persist(accountHolder);

        Account account = getAccount();
        account.setAccountHolder(accountHolder);
        entityManager.persist(account);

        AccountAccess access1 = new AccountAccess();
        access1.setUser(primaryRepresentative);
        access1.setState(AccountAccessState.ACTIVE);
        access1.setRight(AccountAccessRight.INITIATE_AND_APPROVE);
        access1.setAccount(account);
        entityManager.persist(access1);

        AccountAccess access2 = new AccountAccess();
        access2.setUser(secondaryRepresentative);
        access2.setState(AccountAccessState.ACTIVE);
        access2.setRight(AccountAccessRight.INITIATE);
        access2.setAccount(account);
        entityManager.persist(access2);

        AccountAccess access3 = new AccountAccess();
        access3.setUser(additionalRepresentative1);
        access3.setState(AccountAccessState.SUSPENDED);
        access3.setRight(AccountAccessRight.APPROVE);
        access3.setAccount(account);
        entityManager.persist(access3);

        AccountAccess access4 = new AccountAccess();
        access4.setUser(additionalRepresentative2);
        access4.setState(AccountAccessState.ACTIVE);
        access4.setRight(AccountAccessRight.INITIATE_AND_APPROVE);
        access4.setAccount(account);
        entityManager.persist(access4);

        AccountAccess access5 = new AccountAccess();
        access5.setUser(additionalRepresentative2);
        access5.setState(AccountAccessState.ACTIVE);
        access5.setRight(AccountAccessRight.ROLE_BASED);
        access5.setAccount(account);
        entityManager.persist(access5);


        Task task = new Task();
        task.setRequestId(1089L);
        task.setType(RequestType.ACCOUNT_OPENING_REQUEST);
        task.setStatus(RequestStateEnum.APPROVED);
        task.setInitiatedBy(initiator);
        task.setClaimedBy(claimant);
        task.setAccount(account);
        //TODO PRepare data
//        task.setTransactionIdentifier(List.of("transactionId"));
        task.setInitiatedDate(
            Date.from((LocalDate.of(2020, 02, 19).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));
        task.setClaimedDate(
            Date.from((LocalDate.of(2020, 02, 20).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));
        task.setCompletedDate(
            Date.from((LocalDate.of(2020, 02, 21).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));

        entityManager.persist(task);
    }

    @Test
    public void findByUserIamIdentifier() {
        List<AccountAccess> results = accountAccessRepository.findByUserIamIdentifier(PRIMARY_AUTH_REP_IAM_IDENTIFIER);
        assertThat(results).hasSize(1);
    }

    @Test
    public void findAuthorizedRepresentativesByUserIamIdentifier() {
        List<AccountAccess> results =
            accountAccessRepository.findRelatedARsByUserIamIdentifier(SECONDARY_AUTH_REP_IAM_IDENTIFIER);
        assertThat(results).hasSize(3);
        //Make sure user is excluded
        results.forEach(u -> assertNotEquals(u.getUser().getIamIdentifier(), SECONDARY_AUTH_REP_IAM_IDENTIFIER));
    }

    @Test
    public void shouldRetrieveOnlyARs() {

        List<AccountAccess> accountAccesses =
            accountAccessRepository.finARsByAccount_Identifier(TEST_ACCOUNT_IDENTIFIER);

        assertThat(accountAccesses).hasSize(4);

    }
    
    @Test
    void deleteByUserAndRight() {
        //Make sure the user has at least 1 access
        List<AccountAccess> results = accountAccessRepository.findByUserIamIdentifier(ADD_AUTH_REP_2_IAM_IDENTIFIER);
        assertThat(results).hasSize(2);
        
        Long numberOfRowsDeleted =
            accountAccessRepository.deleteByUserAndRight(results.stream().findFirst().get().getUser(),
                AccountAccessRight.ROLE_BASED);
        assertEquals(1,numberOfRowsDeleted);
        
        //Make sure the user has no access
        results = accountAccessRepository.findByUserIamIdentifier(ADD_AUTH_REP_2_IAM_IDENTIFIER);
        assertThat(results).hasSize(1);

    }

    private Account getAccount() {
        Account account = new Account();
        account.setAccountName("claimant iamIdentifier");
        account.setAccountStatus(AccountStatus.OPEN);
        account.setRegistryAccountType(RegistryAccountType.NONE);
        final AccountType type = AccountType.PERSON_HOLDING_ACCOUNT;
        account.setKyotoAccountType(type.getKyotoType());
        account.setRegistryAccountType(type.getRegistryType());
        account.setRegistryCode(type.getRegistryCode());
        account.setBillingAddressSameAsAccountHolderAddress(true);
        account.setApprovalOfSecondAuthorisedRepresentativeIsRequired(true);
        account.setIdentifier(TEST_ACCOUNT_IDENTIFIER);
        account.setCommitmentPeriodCode(2);
        account.setCheckDigits(44);
        account.setFullIdentifier(
            String.format("%s-%d-%d-%d-%d", type.getRegistryCode(), type.getKyotoCode(), account.getIdentifier(),
                account.getCommitmentPeriodCode(), account.getCheckDigits()));

        return account;
    }

    private AccountHolder getAccountHolder() {
        AccountHolder holder = new AccountHolder();
        holder.setIdentifier(1229L);
        holder.setType(AccountHolderType.INDIVIDUAL);
        holder.setName("Account Holder Name");

        return holder;
    }

    private User getPrimaryAuthorizedRepresentative() {
        User representative = new User();
        representative.setIamIdentifier(PRIMARY_AUTH_REP_IAM_IDENTIFIER);
        representative.setFirstName("Primary Auth FirstName");
        representative.setLastName("Primary Auth LastName");
        representative.setDisclosedName(Utils.concat(" ", representative.getFirstName(), representative.getLastName()));
        return representative;
    }

    private User getSecondaryAuthorizedRepresentative() {
        User representative = new User();
        representative.setIamIdentifier(SECONDARY_AUTH_REP_IAM_IDENTIFIER);
        representative.setFirstName("Secondary Auth FirstName");
        representative.setLastName("Secindary Auth LastName");
        representative.setDisclosedName(Utils.concat(" ", representative.getFirstName(), representative.getLastName()));
        return representative;
    }

    private User getAdditionalAuthorizedRepresentative_1() {
        User representative = new User();
        representative.setIamIdentifier(ADD_AUTH_REP_1_IAM_IDENTIFIER);
        representative.setFirstName("Additional Auth 1 FirstName");
        representative.setLastName("Additional Auth 1 LastName");
        representative.setDisclosedName(Utils.concat(" ", representative.getFirstName(), representative.getLastName()));
        return representative;
    }

    private User getAdditionalAuthorizedRepresentative_2() {
        User representative = new User();
        representative.setIamIdentifier(ADD_AUTH_REP_2_IAM_IDENTIFIER);
        representative.setFirstName("Additional Auth 2 FirstName");
        representative.setLastName("Additional Auth 2 LastName");
        representative.setDisclosedName(Utils.concat(" ", representative.getFirstName(), representative.getLastName()));
        return representative;
    }

    private User getClaimant() {
        User claimant = new User();
        claimant.setIamIdentifier("claimant iamIdentifier");
        claimant.setFirstName("Claimant FirstName");
        claimant.setLastName("Claimant LastName");
        claimant.setDisclosedName("Registry Administrator");
        return claimant;
    }

    private User getInitiator() {
        User initiator = new User();
        initiator.setIamIdentifier("Initiator iamIdentifier");
        initiator.setFirstName("Initiator FirstName");
        initiator.setLastName("Initiator LastName");
        initiator.setDisclosedName(Utils.concat(" ", initiator.getFirstName(), initiator.getLastName()));
        return initiator;
    }
}
