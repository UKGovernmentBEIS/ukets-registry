package gov.uk.ets.registry.api.task.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.TaskTransaction;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.domain.types.TaskStatus;
import gov.uk.ets.registry.api.task.searchmetadata.domain.TaskSearchMetadata;
import gov.uk.ets.registry.api.task.searchmetadata.domain.types.MetadataName;
import gov.uk.ets.registry.api.task.shared.EndUserSearch;
import gov.uk.ets.registry.api.task.shared.TaskProjection;
import gov.uk.ets.registry.api.task.shared.TaskSearchCriteria;
import gov.uk.ets.registry.api.transaction.domain.AccountBasicInfo;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.user.domain.IamUserRole;
import gov.uk.ets.registry.api.user.domain.IamUserRoleRepository;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create"})
@Transactional
class TaskRepositoryTest {

    private static final String TEST_IAM_ID = "12123-1221-412-412241-142421";
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private IamUserRoleRepository iamUserRoleRepository;

    private String USER_SEARCH_AUTH_REP_1_IAM_IDENTIFIER = "aa6cf338-9be5-4ed5-b5a2-c8a1550b60dd";
    private String USER_SEARCH_AUTH_REP_2_IAM_IDENTIFIER = "a13ccc71-ac68-42d2-821e-e1a5b639e315";
    private String USER_SEARCH_AUTH_REP_3_IAM_IDENTIFIER = "4fcd29a8-1aa8-4b66-9bef-dc290b9d66c1";

    User admin;

    @BeforeEach
    public void setUp() throws Exception {
        prepareAdminSearchTestData();
        prepareUserSearchTestData();
    }

    private void prepareAdminSearchTestData() {
        User initiator = getAdminSearchInitiator();
        IamUserRole role = iamUserRoleRepository.save(new IamUserRole("test-iam-id", "authorized-representative"));
        initiator.addUserRole(role);
        entityManager.persist(initiator);

        User initiator2 = getAdminSearchInitiatorWithKnownAsName();
        IamUserRole role2 = iamUserRoleRepository.save(new IamUserRole("test-iam-id2", "authorized-representative2"));
        initiator2.addUserRole(role2);
        entityManager.persist(initiator2);

        User claimant = getAdminSearchClaimant();
        entityManager.persist(claimant);
        User claimant2 = getAdminSearchClaimantWithKnownAsName();
        entityManager.persist(claimant2);
        User representative = getAdminSearchRepresentative();
        User representative2 = getAdminSearchRepresentative2();
        entityManager.persist(representative);
        User printEnrolmentLetterInitiator = getPrintEnrolmentLetterInitiator();
        entityManager.persist(printEnrolmentLetterInitiator);
        User printEnrolmentLetterClaimant = getPrintEnrolmentLetterClaimant();
        entityManager.persist(printEnrolmentLetterClaimant);

        AccountHolder adminSearchAccountHolder = getAdminSearchAccountHolder();
        entityManager.persist(adminSearchAccountHolder);


        admin = new User();
        admin.setIamIdentifier(TEST_IAM_ID);
        entityManager.persist(admin);

        Account adminSearchAccount = getAdminSearchAccount();
        adminSearchAccount.setAccountHolder(adminSearchAccountHolder);
        entityManager.persist(adminSearchAccount);

        AccountAccess access = new AccountAccess();
        access.setUser(representative);
        access.setState(AccountAccessState.ACTIVE);
        access.setRight(AccountAccessRight.INITIATE_AND_APPROVE);
        access.setAccount(adminSearchAccount);
        entityManager.persist(access);

        AccountAccess access2 = new AccountAccess();
        access2.setUser(admin);
        access2.setState(AccountAccessState.ACTIVE);
        access2.setRight(AccountAccessRight.ROLE_BASED);
        access2.setAccount(adminSearchAccount);
        entityManager.persist(access2);

        Task accountOpeningTask = new Task();
        accountOpeningTask.setRequestId(1089L);
        accountOpeningTask.setType(RequestType.ACCOUNT_OPENING_REQUEST);
        accountOpeningTask.setStatus(RequestStateEnum.APPROVED);
        accountOpeningTask.setInitiatedBy(initiator);
        accountOpeningTask.setClaimedBy(claimant);
        accountOpeningTask.setAccount(adminSearchAccount);
        accountOpeningTask.setInitiatedDate(
            Date.from((LocalDate.of(2020, 02, 19).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));
        accountOpeningTask.setClaimedDate(
            Date.from((LocalDate.of(2020, 02, 20).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));
        accountOpeningTask.setCompletedDate(
            Date.from((LocalDate.of(2020, 02, 21).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));

        TaskTransaction taskTransaction = new TaskTransaction();
        taskTransaction.setTask(accountOpeningTask);
        taskTransaction.setTransactionIdentifier("transactionId");

        accountOpeningTask.setTransactionIdentifiers(List.of(taskTransaction));
        entityManager.persist(accountOpeningTask);
        entityManager.persist(taskTransaction);

        Task printEnrolmentLetter = new Task();
        printEnrolmentLetter.setRequestId(1090L);
        printEnrolmentLetter.setType(RequestType.PRINT_ENROLMENT_LETTER_REQUEST);
        printEnrolmentLetter.setStatus(RequestStateEnum.APPROVED);
        printEnrolmentLetter.setInitiatedBy(printEnrolmentLetterInitiator);
        printEnrolmentLetter.setClaimedBy(printEnrolmentLetterClaimant);
        printEnrolmentLetter.setAccount(adminSearchAccount);
        printEnrolmentLetter.setParentTask(accountOpeningTask);
        printEnrolmentLetter.setInitiatedDate(
            Date.from((LocalDate.of(2020, 03, 25).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));
        printEnrolmentLetter.setClaimedDate(
            Date.from((LocalDate.of(2020, 03, 26).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));
        printEnrolmentLetter.setCompletedDate(
            Date.from((LocalDate.of(2020, 03, 27).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));

        entityManager.persist(printEnrolmentLetter);

        Task taskForAdminWithKnownAs = new Task();
        taskForAdminWithKnownAs.setRequestId(2500L);
        taskForAdminWithKnownAs.setType(RequestType.USER_DETAILS_UPDATE_REQUEST);
        taskForAdminWithKnownAs.setStatus(RequestStateEnum.APPROVED);
        taskForAdminWithKnownAs.setInitiatedBy(initiator2);
        taskForAdminWithKnownAs.setClaimedBy(claimant2);
        taskForAdminWithKnownAs.setInitiatedDate(
            Date.from((LocalDate.of(2020, 01, 22).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));
        taskForAdminWithKnownAs.setClaimedDate(
            Date.from((LocalDate.of(2020, 01, 23).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));
        taskForAdminWithKnownAs.setCompletedDate(
            Date.from((LocalDate.of(2020, 01, 24).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));


        TaskSearchMetadata printEnrolmentLetterMetadata = new TaskSearchMetadata();
        printEnrolmentLetterMetadata.setTask(printEnrolmentLetter);
        printEnrolmentLetterMetadata.setMetadataName(MetadataName.USER_ID_NAME_KNOWN_AS);
        printEnrolmentLetterMetadata.setMetadataValue(
                representative.getUrid().concat(", ") +
                representative.getFirstName().concat(" ").concat(representative.getLastName()).concat(", ") +
                representative.getKnownAs());

        entityManager.persist(printEnrolmentLetterMetadata);



        TaskSearchMetadata accountOpeningMetadata = new TaskSearchMetadata();
        accountOpeningMetadata.setTask(accountOpeningTask);
        accountOpeningMetadata.setMetadataName(MetadataName.USER_ID_NAME_KNOWN_AS);
        accountOpeningMetadata.setMetadataValue(
                representative.getUrid().concat(", ") +
                representative.getFirstName().concat(" ").concat(representative.getLastName()).concat(", ") +
                representative.getKnownAs());


        entityManager.persist(accountOpeningMetadata);

        TaskSearchMetadata accountOpeningMetadata2 = new TaskSearchMetadata();
        accountOpeningMetadata2.setTask(accountOpeningTask);
        accountOpeningMetadata2.setMetadataName(MetadataName.USER_ID_NAME_KNOWN_AS);
        accountOpeningMetadata2.setMetadataValue(
                representative2.getUrid().concat(", ") +
                representative2.getFirstName().concat(" ").concat(representative2.getLastName()).concat(", ") +
                representative2.getKnownAs());

        entityManager.persist(accountOpeningMetadata2);

        entityManager.persist(taskForAdminWithKnownAs);
    }

    private void prepareUserSearchTestData() {
        User userSearchInitiator = getUserSearchInitiator();
        entityManager.persist(userSearchInitiator);
        User userSearchClaimant = getUserSearchClaimant();
        entityManager.persist(userSearchClaimant);
        User userSearchRepresentative_1 = getUserSearchRepresentative_1();
        entityManager.persist(userSearchRepresentative_1);
        User userSearchRepresentative_2 = getUserSearchRepresentative_2();
        entityManager.persist(userSearchRepresentative_2);
        User userSearchRepresentative_3 = getUserSearchRepresentative_3();
        entityManager.persist(userSearchRepresentative_3);
        User userSearchPrintEnrolmentLetterInitiator = getUserSearchPrintEnrolmentLetterInitiator();
        entityManager.persist(userSearchPrintEnrolmentLetterInitiator);
        User userSearchPrintEnrolmentLetterClaimant = getUserSearchPrintEnrolmentLetterClaimant();
        entityManager.persist(userSearchPrintEnrolmentLetterClaimant);

        AccountHolder userSearchAccountHolder = getUserSearchAccountHolder();
        entityManager.persist(userSearchAccountHolder);

        Account userSearchAccount = getUserSearchAccount1();
        userSearchAccount.setAccountHolder(userSearchAccountHolder);
        entityManager.persist(userSearchAccount);

        Account userSearchAccount2 = getUserSearchAccount2();
        userSearchAccount2.setAccountHolder(userSearchAccountHolder);
        entityManager.persist(userSearchAccount2);

        Account userSearchAccount3 = getUserSearchAccount3();
        userSearchAccount3.setAccountHolder(userSearchAccountHolder);
        entityManager.persist(userSearchAccount3);

        AccountAccess access1 = new AccountAccess();
        access1.setUser(userSearchRepresentative_1);
        access1.setState(AccountAccessState.ACTIVE);
        access1.setRight(AccountAccessRight.INITIATE_AND_APPROVE);
        access1.setAccount(userSearchAccount);
        entityManager.persist(access1);
        AccountAccess access2 = new AccountAccess();
        access2.setUser(userSearchRepresentative_2);
        access2.setState(AccountAccessState.ACTIVE);
        access2.setRight(AccountAccessRight.APPROVE);
        access2.setAccount(userSearchAccount);
        entityManager.persist(access2);
        AccountAccess access3 = new AccountAccess();
        access3.setUser(userSearchRepresentative_1);
        access3.setState(AccountAccessState.ACTIVE);
        access3.setRight(AccountAccessRight.INITIATE_AND_APPROVE);
        access3.setAccount(userSearchAccount2);
        entityManager.persist(access3);
        AccountAccess access4 = new AccountAccess();
        access4.setUser(userSearchRepresentative_2);
        access4.setState(AccountAccessState.ACTIVE);
        access4.setRight(AccountAccessRight.APPROVE);
        access4.setAccount(userSearchAccount2);
        entityManager.persist(access4);
        AccountAccess access5 = new AccountAccess();
        access5.setUser(userSearchRepresentative_3);
        access5.setState(AccountAccessState.SUSPENDED);
        access5.setRight(AccountAccessRight.INITIATE_AND_APPROVE);
        access5.setAccount(userSearchAccount3);
        entityManager.persist(access5);

        // role based account accesses are needed for admin tests:
        AccountAccess access6 = new AccountAccess();
        access6.setUser(admin);
        access6.setState(AccountAccessState.ACTIVE);
        access6.setRight(AccountAccessRight.ROLE_BASED);
        access6.setAccount(userSearchAccount);
        entityManager.persist(access6);

        AccountAccess access7 = new AccountAccess();
        access7.setUser(admin);
        access7.setState(AccountAccessState.ACTIVE);
        access7.setRight(AccountAccessRight.ROLE_BASED);
        access7.setAccount(userSearchAccount2);
        entityManager.persist(access7);

        AccountAccess access8 = new AccountAccess();
        access8.setUser(admin);
        access8.setState(AccountAccessState.ACTIVE);
        access8.setRight(AccountAccessRight.ROLE_BASED);
        access8.setAccount(userSearchAccount3);
        entityManager.persist(access8);

        Task addRepresentativeRequest = new Task();
        addRepresentativeRequest.setRequestId(1091L);
        addRepresentativeRequest.setType(RequestType.ACCOUNT_OPENING_REQUEST);
        addRepresentativeRequest.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        addRepresentativeRequest.setInitiatedBy(userSearchInitiator);
        addRepresentativeRequest.setClaimedBy(userSearchClaimant);
        addRepresentativeRequest.setAccount(userSearchAccount);
        addRepresentativeRequest.setInitiatedDate(
            Date.from((LocalDate.of(2020, 02, 24).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));
        addRepresentativeRequest.setClaimedDate(
            Date.from((LocalDate.of(2020, 02, 25).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));
        addRepresentativeRequest.setCompletedDate(
            Date.from((LocalDate.of(2020, 02, 26).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));

        entityManager.persist(addRepresentativeRequest);

        Task addRepresentativeRequest2 = new Task();
        addRepresentativeRequest2.setRequestId(1093L);
        addRepresentativeRequest2.setType(RequestType.AH_REQUESTED_DOCUMENT_UPLOAD);
        addRepresentativeRequest2.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        addRepresentativeRequest2.setInitiatedBy(userSearchInitiator);
        addRepresentativeRequest2.setClaimedBy(userSearchClaimant);
        addRepresentativeRequest2.setAccount(userSearchAccount2);
        addRepresentativeRequest2.setInitiatedDate(
            Date.from((LocalDate.of(2020, 02, 24).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));
        addRepresentativeRequest.setClaimedDate(
            Date.from((LocalDate.of(2020, 02, 25).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));
        addRepresentativeRequest.setCompletedDate(
            Date.from((LocalDate.of(2020, 02, 26).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));

        entityManager.persist(addRepresentativeRequest2);

        Task printEnrolmentLetter = new Task();
        printEnrolmentLetter.setRequestId(1092L);
        printEnrolmentLetter.setType(RequestType.PRINT_ENROLMENT_LETTER_REQUEST);
        printEnrolmentLetter.setStatus(RequestStateEnum.APPROVED);
        printEnrolmentLetter.setInitiatedBy(userSearchPrintEnrolmentLetterInitiator);
        printEnrolmentLetter.setClaimedBy(userSearchPrintEnrolmentLetterClaimant);
        printEnrolmentLetter.setAccount(userSearchAccount);
        printEnrolmentLetter.setParentTask(addRepresentativeRequest);
        printEnrolmentLetter.setInitiatedDate(
            Date.from((LocalDate.of(2020, 03, 25).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));
        printEnrolmentLetter.setClaimedDate(
            Date.from((LocalDate.of(2020, 03, 26).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));
        printEnrolmentLetter.setCompletedDate(
            Date.from((LocalDate.of(2020, 03, 27).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));

        entityManager.persist(printEnrolmentLetter);

        Task arRequestDocument = new Task();
        arRequestDocument.setRequestId(1094L);
        arRequestDocument.setType(RequestType.AR_REQUESTED_DOCUMENT_UPLOAD);
        arRequestDocument.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        arRequestDocument.setInitiatedBy(userSearchRepresentative_2);
        arRequestDocument.setClaimedBy(userSearchRepresentative_3);
        arRequestDocument.setUser(userSearchRepresentative_3);
        arRequestDocument.setInitiatedDate(
            Date.from((LocalDate.of(2020, 03, 25).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));
        arRequestDocument.setClaimedDate(
            Date.from((LocalDate.of(2020, 03, 26).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));
        arRequestDocument.setCompletedDate(
            Date.from((LocalDate.of(2020, 03, 27).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));

        entityManager.persist(arRequestDocument);

        Task addAR = new Task();
        addAR.setRequestId(1095L);
        addAR.setType(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST);
        addAR.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        addAR.setInitiatedBy(userSearchRepresentative_3);
        addAR.setClaimedBy(userSearchRepresentative_3);
        addAR.setAccount(userSearchAccount3);
        addAR.setInitiatedDate(
            Date.from((LocalDate.of(2020, 03, 25).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));
        addAR.setClaimedDate(
            Date.from((LocalDate.of(2020, 03, 26).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));
        addAR.setCompletedDate(
            Date.from((LocalDate.of(2020, 03, 27).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));

        entityManager.persist(addAR);
       
        Account surrenderAccount = getGovernmentAccount();
        entityManager.persist(surrenderAccount);
        long count = 0L;
        long taskIdentifier = 1096L;
        for (TransactionType transactionType : TransactionType.showAccountNameInsteadOfNumber()) {
            Transaction transaction = new Transaction();
            transaction.setIdentifier("UK1000" + count);
            transaction.setType(transactionType);
            transaction.setStatus(TransactionStatus.AWAITING_APPROVAL);
            transaction.setQuantity(2L);
            transaction.setUnitType(UnitType.ALLOWANCE);
            final AccountType type = AccountType.PARTY_HOLDING_ACCOUNT;
            AccountBasicInfo transferringAccount = new AccountBasicInfo();
            transferringAccount.setAccountFullIdentifier(
                String.format("%s-%d-%d-%d-%d", userSearchAccount.getRegistryCode(), type.getKyotoCode(),
                    userSearchAccount.getIdentifier(), userSearchAccount.getCommitmentPeriodCode(),
                    userSearchAccount.getCheckDigits()));
            transferringAccount.setAccountType(userSearchAccount.getKyotoAccountType());
            transferringAccount.setAccountIdentifier(userSearchAccount.getIdentifier());
            transferringAccount.setAccountRegistryCode(userSearchAccount.getRegistryCode());
            transaction.setTransferringAccount(transferringAccount);
            AccountBasicInfo acquiringAccount = new AccountBasicInfo();
            acquiringAccount.setAccountFullIdentifier(
                String.format("%s-%d-%d-%d-%d", surrenderAccount.getRegistryCode(), type.getKyotoCode(),
                    surrenderAccount.getIdentifier(), surrenderAccount.getCommitmentPeriodCode(),
                    surrenderAccount.getCheckDigits()));
            acquiringAccount.setAccountType(surrenderAccount.getKyotoAccountType());
            acquiringAccount.setAccountIdentifier(surrenderAccount.getIdentifier());
            acquiringAccount.setAccountRegistryCode(surrenderAccount.getRegistryCode());
            transaction.setAcquiringAccount(acquiringAccount);
            entityManager.persist(transaction);


            
            Task transactionProposal = new Task();
            transactionProposal.setRequestId(taskIdentifier);
            transactionProposal.setType(RequestType.TRANSACTION_REQUEST);
            transactionProposal.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
            transactionProposal.setInitiatedBy(userSearchInitiator);
            transactionProposal.setClaimedBy(userSearchClaimant);
            transactionProposal.setAccount(userSearchAccount);
            transactionProposal.setInitiatedDate(
                Date.from((LocalDate.of(2020, 03, 25).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));
            transactionProposal.setClaimedDate(
                Date.from((LocalDate.of(2020, 03, 26).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));
            transactionProposal.setCompletedDate(
                Date.from((LocalDate.of(2020, 03, 27).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));


            TaskTransaction taskTransaction = new TaskTransaction();
            taskTransaction.setTask(transactionProposal);
            taskTransaction.setTransactionIdentifier(transaction.getIdentifier());
            taskTransaction.setRecipientAccountNumber("UK-100-10000025-0-33");

            transactionProposal.setTransactionIdentifiers(List.of(taskTransaction));
            entityManager.persist(transactionProposal);
            entityManager.persist(taskTransaction);

            count++;
            taskIdentifier++;
        }
    }

    @Test
    @DisplayName("Test the recipient account number description for User and Admin task search queries. ")
    void testRecipientAccountNumberDescription() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        long taskIdentifier = 1096L;
        for (TransactionType transactionType : TransactionType.showAccountNameInsteadOfNumber()) {
            criteria.setRequestId(taskIdentifier);
            setEndUserSearchInfoAdmin(criteria);
            Page<TaskProjection> adminResults = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
            assertEquals(1, adminResults.getNumberOfElements());
            assertEquals("UK-100-10000025-0-33", adminResults.getContent().get(0).getRecipientAccountNumber());

            EndUserSearch endUserSearch = new EndUserSearch();
            endUserSearch.setAdminSearch(false);
            endUserSearch.setIamIdentifier(USER_SEARCH_AUTH_REP_1_IAM_IDENTIFIER);
            criteria.setEndUserSearch(endUserSearch);
            Page<TaskProjection> userResults = taskRepository.userSearch(criteria, PageRequest.of(0, 10));
            if (transactionType.isAccessibleToAR()) {
                assertEquals(1, userResults.getNumberOfElements());
                assertEquals("UK Government Test Account", userResults.getContent().get(0).getRecipientAccountNumber());
            } else {
                assertEquals(0, userResults.getNumberOfElements());
            }
            taskIdentifier++;
        }
    }

    @Test
    void test_findByRequest_IdIn() {

        Stream.of(1L, 2L, 3L, 4L, 5L).forEach(id -> {
            Task task = new Task();
            task.setRequestId(id);
            entityManager.persist(task);
        });

        List<Task> tasks = taskRepository.findAllByRequestIdIn(Arrays.asList(2L, 3L, 4L));
        assertEquals(3, tasks.size());
    }

    @Test
    void adminSearchByAccountNumber() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        criteria.setAccountNumber("GB-121");
        setEndUserSearchInfoAdmin(criteria);
        Page<TaskProjection> results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
        assertEquals(2, results.getNumberOfElements());

        criteria.setAccountNumber("121");
        results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
        assertEquals(2, results.getNumberOfElements());
    }

    @Test
    void adminSearchByAccountHolder() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        criteria.setAccountHolder("Admin Search Account Holder Na");
        setEndUserSearchInfoAdmin(criteria);
        Page<TaskProjection> results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
        assertEquals(2, results.getNumberOfElements());
    }

    @Test
    void adminSearchByTaskStatus() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        setEndUserSearchInfoAdmin(criteria);
        criteria.setTaskStatus(TaskStatus.COMPLETED);
        Page<TaskProjection> results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
        assertEquals(4, results.getNumberOfElements());
    }

    @Test
    void adminSearchByClaimantName() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        setEndUserSearchInfoAdmin(criteria);
        User expectedInitiator = getAdminSearchClaimant();
        Stream.of(new SearchByFirstNameLastNameTestCase(expectedInitiator.getFirstName(),
                "search by first name failed"),
            new SearchByFirstNameLastNameTestCase(expectedInitiator.getLastName(),
                "search by last name failed"),
            new SearchByFirstNameLastNameTestCase(
                expectedInitiator.getFirstName().concat(" ").concat(expectedInitiator.getLastName()),
                "search by first and last name failed")
        ).forEach(testCase -> {
            criteria.setClaimantName(testCase.term);
            Page<TaskProjection> results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
            assertEquals(1, results.getNumberOfElements(), testCase.errorMessage);
        });
    }
    
    @Test
    void adminSearchByClaimantNameWithKnownAs() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        setEndUserSearchInfoAdmin(criteria);
        User expectedInitiator = getAdminSearchClaimantWithKnownAsName();
        Stream.of(new SearchByFirstNameLastNameTestCase(expectedInitiator.getKnownAs(),
                "search by known as name failed")
        ).forEach(testCase -> {
            criteria.setClaimantName(testCase.term);
            Page<TaskProjection> results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
            assertEquals(1, results.getNumberOfElements(), testCase.errorMessage);
        });
    }

    @Test
    void adminSearchByTaskType() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        setEndUserSearchInfoAdmin(criteria);
        criteria.setTaskType(RequestType.ACCOUNT_OPENING_REQUEST);
        Page<TaskProjection> results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
        assertEquals(2, results.getNumberOfElements());
    }

    @Test
    @DisplayName("Admin user can access tasks of Suspended Accounts")
    void adminSearchByTaskTypeForSuspendedAccount() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        criteria.setTaskType(RequestType.AH_REQUESTED_DOCUMENT_UPLOAD);
        setEndUserSearchInfoAdmin(criteria);
        Page<TaskProjection> results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
        assertEquals(1, results.getNumberOfElements());
    }

    private void setEndUserSearchInfoAdmin(TaskSearchCriteria criteria) {
        EndUserSearch endUserSearch = new EndUserSearch();
        endUserSearch.setIamIdentifier(TEST_IAM_ID);
        criteria.setEndUserSearch(endUserSearch);
    }

    @Test
    @DisplayName("When admin searching with request id , spin-off tasks must also be returned")
    void adminSearchByRequestId() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        setEndUserSearchInfoAdmin(criteria);
        criteria.setRequestId(1089L);
        Page<TaskProjection> results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
        assertEquals(2, results.getNumberOfElements());
        assertTrue(results.getContent().stream().map(t -> t.getRequestId()).anyMatch(t -> t.equals(1089L)));
        assertTrue(results.getContent().stream().map(t -> t.getRequestId()).anyMatch(t -> t.equals(1090L)));
    }

    @Test
    void adminSearchByTransactionId() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        setEndUserSearchInfoAdmin(criteria);
        criteria.setTransactionId("transact");
        Page<TaskProjection> results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
        assertEquals(1, results.getNumberOfElements());
    }

    @Test
    void adminSearchByTaskOutcome() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        setEndUserSearchInfoAdmin(criteria);
        criteria.setTaskOutcome(RequestStateEnum.APPROVED);
        Page<TaskProjection> results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
        assertEquals(4, results.getNumberOfElements());
    }

    @Test
    void adminSearchByInitiatorName() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        setEndUserSearchInfoAdmin(criteria);
        User expectedInitiator = getAdminSearchInitiator();
        Stream.of(new SearchByFirstNameLastNameTestCase(expectedInitiator.getFirstName(),
                "search by first name failed"),
            new SearchByFirstNameLastNameTestCase(expectedInitiator.getLastName(),
                "search by last name failed"),
            new SearchByFirstNameLastNameTestCase(
                expectedInitiator.getFirstName().concat(" ").concat(expectedInitiator.getLastName()),
                "search by first and last name failed")
        ).forEach(testCase -> {
            criteria.setInitiatorName(testCase.term);
            Page<TaskProjection> results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
            assertEquals(1, results.getNumberOfElements(), testCase.errorMessage);
        });
    }
    
    @Test
    void adminSearchByInitiatorNameWithKnownAs() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        setEndUserSearchInfoAdmin(criteria);
        User expectedInitiator = getAdminSearchInitiatorWithKnownAsName();
        Stream.of(new SearchByFirstNameLastNameTestCase(expectedInitiator.getKnownAs(),
                "search by known as name failed")
        ).forEach(testCase -> {
            criteria.setInitiatorName(testCase.term);
            Page<TaskProjection> results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
            assertEquals(1, results.getNumberOfElements(), testCase.errorMessage);
        });
    }

    @Test
    void adminSearchByKyotoAccountType() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        setEndUserSearchInfoAdmin(criteria);
        criteria.setAccountType(AccountType.PERSON_HOLDING_ACCOUNT.toString());
        Page<TaskProjection> results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
        assertEquals(2, results.getNumberOfElements());
    }

    @Test
    void adminSearchByClaimedOn() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        setEndUserSearchInfoAdmin(criteria);
        criteria.setClaimedOnFrom(
            Date.from(LocalDate.of(2020, 02, 20).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        criteria.setClaimedOnTo(
            Date.from(LocalDate.of(2020, 02, 20).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        Page<TaskProjection> results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
        assertEquals(1, results.getNumberOfElements());
    }

    @Test
    void adminSearchByClaimedOnFrom() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        setEndUserSearchInfoAdmin(criteria);
        criteria.setClaimedOnFrom(
            Date.from(LocalDate.of(2020, 4, 15).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        Page<TaskProjection> results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
        assertEquals(0, results.getNumberOfElements());
    }

    @Test
    void adminSearchByClaimedOnTo() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        setEndUserSearchInfoAdmin(criteria);
        criteria.setClaimedOnTo(
            Date.from(LocalDate.of(2020, 01, 04).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        Page<TaskProjection> results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
        assertEquals(0, results.getNumberOfElements());
    }

    @Test
    void adminSearchByCompletedOn() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        setEndUserSearchInfoAdmin(criteria);
        criteria.setCompletedOnFrom(
            Date.from(LocalDate.of(2020, 02, 21).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        criteria.setCompletedOnTo(
            Date.from(LocalDate.of(2020, 02, 21).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        Page<TaskProjection> results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
        assertEquals(1, results.getNumberOfElements());
    }

    @Test
    void adminSearchByCompletedOnFrom() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        setEndUserSearchInfoAdmin(criteria);
        criteria.setCompletedOnFrom(
            Date.from(LocalDate.of(2020, 4, 15).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        Page<TaskProjection> results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
        assertEquals(0, results.getNumberOfElements());
    }

    @Test
    void adminSearchByCompletedOnTo() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        setEndUserSearchInfoAdmin(criteria);
        criteria.setCompletedOnTo(
            Date.from(LocalDate.of(2020, 01, 04).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        Page<TaskProjection> results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
        assertEquals(0, results.getNumberOfElements());
    }

    @Test
    void adminSearchByCreatedOn() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        setEndUserSearchInfoAdmin(criteria);
        criteria.setCreatedOnFrom(
            Date.from(LocalDate.of(2020, 02, 19).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        criteria.setCreatedOnTo(
            Date.from(LocalDate.of(2020, 02, 19).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        Page<TaskProjection> results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
        assertEquals(1, results.getNumberOfElements());
    }

    @Test
    void adminSearchByCreatedOnFrom() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        setEndUserSearchInfoAdmin(criteria);
        criteria.setCreatedOnFrom(
            Date.from(LocalDate.of(2020, 4, 15).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        Page<TaskProjection> results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
        assertEquals(0, results.getNumberOfElements());
    }

    @Test
    void adminSearchByCreatedOnTo() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        setEndUserSearchInfoAdmin(criteria);
        criteria.setCreatedOnTo(
            Date.from(LocalDate.of(2020, 01, 04).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        Page<TaskProjection> results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
        assertEquals(0, results.getNumberOfElements());
    }


    @Test
    void adminSearchByUserTasks() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        setEndUserSearchInfoAdmin(criteria);
        criteria.setExcludeUserTasks(Boolean.TRUE);
        Page<TaskProjection> results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
        assertEquals(7, results.getNumberOfElements());
    }

    @Test
    void adminSearchByUserRole() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        setEndUserSearchInfoAdmin(criteria);
        criteria.setInitiatedBy("AUTHORISED_REPRESENTATIVE");
        Page<TaskProjection> results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
        assertEquals(1, results.getNumberOfElements());
    }

    @Test
    void adminSearchByUserKnownAs() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        setEndUserSearchInfoAdmin(criteria);
        criteria.setNameOrUserId("Represent");
        Page<TaskProjection> results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
        assertEquals(2,results.getNumberOfElements());
    }

    @Test
    void adminSearchByUserId() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        setEndUserSearchInfoAdmin(criteria);
        criteria.setNameOrUserId("UK1234567890");
        Page<TaskProjection> results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
        assertEquals(1,results.getNumberOfElements());
    }

    @Test
    void adminSearchByUserName() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        setEndUserSearchInfoAdmin(criteria);
        criteria.setNameOrUserId("Logged IN Auth FirstName");
        Page<TaskProjection> results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
        assertEquals(2,results.getNumberOfElements());
    }


    @Test
    void adminSearchNoCriteria() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        setEndUserSearchInfoAdmin(criteria);
        Page<TaskProjection> results = taskRepository.adminSearch(criteria, PageRequest.of(0, 10));
        assertEquals(10, results.getNumberOfElements());
    }

    @Test
    void userSearchNoCriteria() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        EndUserSearch endUserSearch = new EndUserSearch();
        endUserSearch.setAdminSearch(false);
        endUserSearch.setIamIdentifier(USER_SEARCH_AUTH_REP_1_IAM_IDENTIFIER);
        criteria.setEndUserSearch(endUserSearch);
        Page<TaskProjection> results = taskRepository.userSearch(criteria, PageRequest.of(0, 10));
        assertEquals(5, results.getNumberOfElements());
    }

    @Test
    @DisplayName(
        "An AR with Suspended Access Right in a specific account is not allowed to view tasks of this account. " +
            "In this test case only the Request Document Upload task is visible ")
    void userSearchNoCriteriaSuspendedARFromAccount() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        EndUserSearch endUserSearch = new EndUserSearch();
        endUserSearch.setAdminSearch(false);
        endUserSearch.setIamIdentifier(USER_SEARCH_AUTH_REP_3_IAM_IDENTIFIER);
        criteria.setEndUserSearch(endUserSearch);
        criteria.setRequestId(1094L);
        Page<TaskProjection> results = taskRepository.userSearch(criteria, PageRequest.of(0, 10));
        assertEquals(1, results.getNumberOfElements());
        assertEquals(RequestType.AR_REQUESTED_DOCUMENT_UPLOAD, results.getContent().get(0).getTaskType());
        assertEquals(1094L, results.getContent().get(0).getRequestId());

        criteria.setRequestId(1095L);
        results = taskRepository.userSearch(criteria, PageRequest.of(0, 10));
        assertEquals(0, results.getNumberOfElements());
    }

    @Test
    void userSearchByClaimantName() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        EndUserSearch endUserSearch = new EndUserSearch();
        endUserSearch.setAdminSearch(false);
        endUserSearch.setIamIdentifier(USER_SEARCH_AUTH_REP_1_IAM_IDENTIFIER);
        criteria.setEndUserSearch(endUserSearch);
        User expectedClaimant = getUserSearchClaimant();
        criteria.setClaimantName(expectedClaimant.getDisclosedName());

        Page<TaskProjection> results = taskRepository.userSearch(criteria, PageRequest.of(0, 10));

        assertEquals(5, results.getNumberOfElements());
    }

    @Test
    void userSearchByInitiatorName() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        EndUserSearch endUserSearch = new EndUserSearch();
        endUserSearch.setAdminSearch(false);
        endUserSearch.setIamIdentifier(USER_SEARCH_AUTH_REP_1_IAM_IDENTIFIER);
        criteria.setEndUserSearch(endUserSearch);
        User expectedInitiator = getUserSearchInitiator();
        criteria.setInitiatorName(expectedInitiator.getDisclosedName());

        Page<TaskProjection> results = taskRepository.userSearch(criteria, PageRequest.of(0, 10));

        assertEquals(5, results.getNumberOfElements());
    }

    @Test
    void userSearchByRequestId() {

        EndUserSearch endUserSearch = new EndUserSearch();
        endUserSearch.setAdminSearch(false);
        endUserSearch.setIamIdentifier(USER_SEARCH_AUTH_REP_1_IAM_IDENTIFIER);

        TaskSearchCriteria criteria = new TaskSearchCriteria();
        criteria.setEndUserSearch(endUserSearch);
        criteria.setRequestId(1091L);

        Page<TaskProjection> results = taskRepository.userSearch(criteria, PageRequest.of(0, 10));

        assertEquals(0, results.getNumberOfElements());
    }

    @Test
    @DisplayName("Authorised representative user can not access tasks of Suspended Accounts")
    void userSearchForSuspendedAccounts() {
        EndUserSearch endUserSearch = new EndUserSearch();
        endUserSearch.setAdminSearch(false);
        endUserSearch.setIamIdentifier(USER_SEARCH_AUTH_REP_1_IAM_IDENTIFIER);

        TaskSearchCriteria criteria = new TaskSearchCriteria();
        criteria.setEndUserSearch(endUserSearch);
        criteria.setRequestId(1093L);

        Page<TaskProjection> results = taskRepository.userSearch(criteria, PageRequest.of(0, 10));

        assertEquals(0, results.getNumberOfElements());
    }

    @Test
    @DisplayName("Should find one pending TAL deletion request for a specific account.")
    void shouldFindPendingTasksByTypeAndAccount() {
        Account account = new Account();
        account.setIdentifier(100001L);
        entityManager.persist(account);

        Task task1 = new Task();
        task1.setRequestId(11111L);
        task1.setAccount(account);
        task1.setType(RequestType.ACCOUNT_OPENING_REQUEST);
        task1.setStatus(RequestStateEnum.APPROVED);

        Task task2 = new Task();
        task2.setRequestId(11112L);
        task2.setAccount(account);
        task2.setType(RequestType.DELETE_TRUSTED_ACCOUNT_REQUEST);
        task2.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);

        Task task3 = new Task();
        task3.setRequestId(11113L);
        task3.setAccount(account);
        task3.setType(RequestType.DELETE_TRUSTED_ACCOUNT_REQUEST);
        task3.setStatus(RequestStateEnum.REJECTED);
        entityManager.persist(task1);
        entityManager.persist(task2);
        entityManager.persist(task3);

        assertTrue(taskRepository.findPendingTasksByTypeAndAccount(
            RequestType.ACCOUNT_OPENING_REQUEST, account.getId()).isEmpty());
        assertEquals(1,taskRepository.findPendingTasksByTypeAndAccount(
            RequestType.DELETE_TRUSTED_ACCOUNT_REQUEST, account.getId()).size());
    }

    private static class SearchByFirstNameLastNameTestCase {
        private String errorMessage;
        private String term;

        public SearchByFirstNameLastNameTestCase(String term, String errorMessage) {
            this.errorMessage = errorMessage;
            this.term = term;
        }
    }

    private Account getAdminSearchAccount() {
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
        account.setIdentifier(1222L);
        account.setCommitmentPeriodCode(2);
        account.setCheckDigits(44);
        account.setFullIdentifier(String.format("%s-%d-%d-%d-%d", type.getRegistryCode(), type.getKyotoCode(),
            account.getIdentifier(), account.getCommitmentPeriodCode(), account.getCheckDigits()));

        return account;
    }

    private Account getUserSearchAccount1() {
        Account account = new Account();
        account.setAccountName("An account for testing user search");
        account.setAccountStatus(AccountStatus.OPEN);
        account.setRegistryAccountType(RegistryAccountType.NONE);
        final AccountType type = AccountType.PARTY_HOLDING_ACCOUNT;
        account.setKyotoAccountType(type.getKyotoType());
        account.setRegistryAccountType(type.getRegistryType());
        account.setRegistryCode(type.getRegistryCode());
        account.setBillingAddressSameAsAccountHolderAddress(true);
        account.setApprovalOfSecondAuthorisedRepresentativeIsRequired(true);
        account.setIdentifier(1223L);
        account.setCommitmentPeriodCode(2);
        account.setCheckDigits(55);
        account.setFullIdentifier(String.format("%s-%d-%d-%d-%d", type.getRegistryCode(), type.getKyotoCode(),
            account.getIdentifier(), account.getCommitmentPeriodCode(), account.getCheckDigits()));

        return account;
    }

    private Account getUserSearchAccount2() {
        Account account = new Account();
        account.setAccountName("An account for testing user search");
        account.setAccountStatus(AccountStatus.SUSPENDED);
        account.setRegistryAccountType(RegistryAccountType.NONE);
        final AccountType type = AccountType.PARTY_HOLDING_ACCOUNT;
        account.setKyotoAccountType(type.getKyotoType());
        account.setRegistryAccountType(type.getRegistryType());
        account.setRegistryCode(type.getRegistryCode());
        account.setBillingAddressSameAsAccountHolderAddress(true);
        account.setApprovalOfSecondAuthorisedRepresentativeIsRequired(true);
        account.setIdentifier(12345L);
        account.setCommitmentPeriodCode(2);
        account.setCheckDigits(56);
        account.setFullIdentifier(String.format("%s-%d-%d-%d-%d", type.getRegistryCode(), type.getKyotoCode(),
            account.getIdentifier(), account.getCommitmentPeriodCode(), account.getCheckDigits()));

        return account;
    }

    private Account getUserSearchAccount3() {
        Account account = new Account();
        account.setAccountName("An account for testing user search");
        account.setAccountStatus(AccountStatus.OPEN);
        account.setRegistryAccountType(RegistryAccountType.NONE);
        final AccountType type = AccountType.PARTY_HOLDING_ACCOUNT;
        account.setKyotoAccountType(type.getKyotoType());
        account.setRegistryAccountType(type.getRegistryType());
        account.setRegistryCode(type.getRegistryCode());
        account.setBillingAddressSameAsAccountHolderAddress(true);
        account.setApprovalOfSecondAuthorisedRepresentativeIsRequired(true);
        account.setIdentifier(1224L);
        account.setCommitmentPeriodCode(2);
        account.setCheckDigits(46);
        account.setFullIdentifier(String.format("%s-%d-%d-%d-%d", type.getRegistryCode(), type.getKyotoCode(),
            account.getIdentifier(), account.getCommitmentPeriodCode(), account.getCheckDigits()));

        return account;
    }

    private Account getGovernmentAccount() {
        Account account = new Account();
        account.setAccountName("UK Government Test Account");
        account.setAccountStatus(AccountStatus.OPEN);
        account.setRegistryAccountType(RegistryAccountType.UK_SURRENDER_ACCOUNT);
        final AccountType type = AccountType.PARTY_HOLDING_ACCOUNT;
        account.setKyotoAccountType(type.getKyotoType());
        account.setRegistryAccountType(type.getRegistryType());
        account.setRegistryCode(type.getRegistryCode());
        account.setBillingAddressSameAsAccountHolderAddress(true);
        account.setApprovalOfSecondAuthorisedRepresentativeIsRequired(true);
        account.setIdentifier(10000025L);
        account.setCommitmentPeriodCode(0);
        account.setCheckDigits(33);
        account.setFullIdentifier(String.format("%s-%d-%d-%d-%d", type.getRegistryCode(), type.getKyotoCode(),
            account.getIdentifier(), account.getCommitmentPeriodCode(), account.getCheckDigits()));

        return account;
    }

    private AccountHolder getAdminSearchAccountHolder() {
        AccountHolder holder = new AccountHolder();
        holder.setIdentifier(1229L);
        holder.setType(AccountHolderType.INDIVIDUAL);
        holder.setName("Admin Search Account Holder Name");

        return holder;
    }

    private AccountHolder getUserSearchAccountHolder() {
        AccountHolder holder = new AccountHolder();
        holder.setIdentifier(1230L);
        holder.setType(AccountHolderType.GOVERNMENT);
        holder.setName("User Search Account Holder Name");

        return holder;
    }

    private User getAdminSearchRepresentative() {
        User representative = new User();
        representative.setUrid("UK2345678910");
        representative.setFirstName("Logged IN Auth FirstName");
        representative.setLastName("Logged IN Auth LastName");
        representative.setDisclosedName(Utils.concat(" ", representative.getFirstName(), representative.getLastName()));
        representative.setKnownAs("Representative");
        return representative;
    }

    private User getAdminSearchRepresentative2() {
        User representative = new User();
        representative.setUrid("UK1234567890");
        representative.setFirstName("UK ETS Authorized");
        representative.setLastName("Representative 2");
        representative.setDisclosedName(Utils.concat(" ", representative.getFirstName(), representative.getLastName()));
        representative.setKnownAs("Representative");
        return representative;
    }

    private User getUserSearchRepresentative_1() {
        User representative = new User();
        representative.setIamIdentifier(USER_SEARCH_AUTH_REP_1_IAM_IDENTIFIER);
        representative.setState(UserStatus.ENROLLED);
        representative.setFirstName("UserSearchRepresentative_1 FirstName");
        representative.setLastName("UserSearchRepresentative_1 LastName");
        representative.setDisclosedName(Utils.concat(" ", representative.getFirstName(), representative.getLastName()));
        return representative;
    }

    private User getUserSearchRepresentative_2() {
        User representative = new User();
        representative.setIamIdentifier(USER_SEARCH_AUTH_REP_2_IAM_IDENTIFIER);
        representative.setFirstName("UserSearchRepresentative_2 FirstName");
        representative.setLastName("UserSearchRepresentative_2 LastName");
        representative.setDisclosedName(Utils.concat(" ", representative.getFirstName(), representative.getLastName()));
        return representative;
    }

    private User getUserSearchRepresentative_3() {
        User representative = new User();
        representative.setIamIdentifier(USER_SEARCH_AUTH_REP_3_IAM_IDENTIFIER);
        representative.setFirstName("UserSearchRepresentative_3 FirstName");
        representative.setLastName("UserSearchRepresentative_3 LastName");
        representative.setDisclosedName(Utils.concat(" ", representative.getFirstName(), representative.getLastName()));
        return representative;
    }

    private User getAdminSearchClaimant() {
        User claimant = new User();
        claimant.setIamIdentifier("claimant iamIdentifier");
        claimant.setFirstName("Admin Search Claimant FirstName");
        claimant.setLastName("Admin Search Claimant LastName");
        claimant.setDisclosedName("Registry Administrator");
        return claimant;
    }
    
    private User getAdminSearchClaimantWithKnownAsName() {
        User claimant = new User();
        claimant.setIamIdentifier("claimant2 iamIdentifier");
        claimant.setFirstName("Admin Search Claimant2 FirstName");
        claimant.setLastName("Admin Search Claimant2 LastName");
        claimant.setKnownAs("Claimant2 KnownAs");        
        claimant.setDisclosedName(claimant.getKnownAs());
        return claimant;
    }

    private User getUserSearchClaimant() {
        User claimant = new User();
        claimant.setIamIdentifier("claimant iamIdentifier");
        claimant.setFirstName("User Search Claimant FirstName");
        claimant.setLastName("User Search Claimant LastName");
        claimant.setDisclosedName("User Search Claimant");
        return claimant;
    }

    private User getAdminSearchInitiator() {
        User initiator = new User();
        initiator.setIamIdentifier("Initiator iamIdentifier");
        initiator.setFirstName("Admin Search Initiator FirstName");
        initiator.setLastName("Admin Search Initiator LastName");
        initiator.setDisclosedName(Utils.concat(" ", initiator.getFirstName(), initiator.getLastName()));
        return initiator;
    }
    
    private User getAdminSearchInitiatorWithKnownAsName() {
        User initiator = new User();
        initiator.setIamIdentifier("Initiator2 iamIdentifier");
        initiator.setFirstName("Admin Search Initiator2 FirstName");
        initiator.setLastName("Admin Search Initiator2 LastName");
        initiator.setKnownAs("Initiator2 KnownAs");        
        initiator.setDisclosedName(initiator.getKnownAs());
        return initiator;
    }

    private User getUserSearchInitiator() {
        User initiator = new User();
        initiator.setIamIdentifier("User Search Initiator iamIdentifier");
        initiator.setFirstName("User Search Initiator FirstName");
        initiator.setLastName("User Search Initiator LastName");
        initiator.setDisclosedName(Utils.concat(" ", initiator.getFirstName(), initiator.getLastName()));
        return initiator;
    }

    private User getPrintEnrolmentLetterInitiator() {
        User initiator = new User();
        initiator.setIamIdentifier("Print Enrolment Letter Initiator iamIdentifier");
        initiator.setFirstName("Print Enrolment Letter Initiator FirstName");
        initiator.setLastName("Print Enrolment Letter Initiator LastName");
        initiator.setDisclosedName(Utils.concat(" ", initiator.getFirstName(), initiator.getLastName()));
        return initiator;
    }

    private User getPrintEnrolmentLetterClaimant() {
        User claimant = new User();
        claimant.setIamIdentifier("Print Enrolment Letter Claimant iamIdentifier");
        claimant.setFirstName("Print Enrolment Letter Claimant FirstName");
        claimant.setLastName("Print Enrolment Letter Claimant LastName");
        claimant.setDisclosedName("Registry Administrator");
        return claimant;
    }

    private User getUserSearchPrintEnrolmentLetterClaimant() {
        User claimant = new User();
        claimant.setIamIdentifier("User Search Print Enrolment Letter Claimant iamIdentifier");
        claimant.setFirstName("User Search Print Enrolment Letter Claimant FirstName");
        claimant.setLastName("User Search Print Enrolment Letter Claimant LastName");
        claimant.setDisclosedName("Registry Administrator");
        return claimant;
    }

    private User getUserSearchPrintEnrolmentLetterInitiator() {
        User initiator = new User();
        initiator.setIamIdentifier("User Search Print Enrolment Letter Initiator iamIdentifier");
        initiator.setFirstName("User Search Print Enrolment Letter Initiator FirstName");
        initiator.setLastName("User Search Print Enrolment Letter Initiator LastName");
        initiator.setDisclosedName(Utils.concat(" ", initiator.getFirstName(), initiator.getLastName()));
        return initiator;
    }
}
