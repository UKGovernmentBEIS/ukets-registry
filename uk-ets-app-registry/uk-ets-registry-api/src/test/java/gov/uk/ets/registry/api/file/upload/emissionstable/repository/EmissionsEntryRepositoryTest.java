package gov.uk.ets.registry.api.file.upload.emissionstable.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.compliance.web.model.VerifiedEmissionsDTO;
import gov.uk.ets.registry.api.file.upload.emissionstable.model.EmissionsEntry;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddAccountCommand;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddAccountHolderCommand;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddAircraftEntityToAccountCommand;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddInstallationEntityToAccountCommand;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create",
    "spring.jpa.properties.hibernate.globally_quoted_identifiers=true",
    "spring.jpa.properties.hibernate.globally_quoted_identifiers_skip_column_definitions=true"})
public class EmissionsEntryRepositoryTest {

    private static final Long TEST_COMPLIANT_ENTITY_ID_1 = 112233L;
    private static final Long TEST_COMPLIANT_ENTITY_ID_2 = 112234L;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    EmissionsEntryRepository emissionsEntryRepository;

    private Long operatorHoldingAccountIdentifier = 10001L;
    private Long aircraftOperatorHoldingAccountIdentifier = 10002L;
    private Long installationId = 123L;
    private Long aircraftOperatorId = 333L;

    @BeforeEach
    void setUp() throws IOException {
        AccountModelTestHelper accountHelper = new AccountModelTestHelper(entityManager);

        AddAccountHolderCommand addAccountHolderCommand = AddAccountHolderCommand.builder()
                                                         .accountHolderType(AccountHolderType.ORGANISATION)
                                                         .identifier(100001L)
                                                         .name("Test account holder name")
                                                         .status(Status.ACTIVE)
                                                         .build();
        AccountHolder accountHolder = accountHelper.addAccountHolder(addAccountHolderCommand);

        //OHA
        AddAccountCommand addAccountCommand = AddAccountCommand.builder()
                                                               .accountHolder(accountHolder)
                                                               .accountId(operatorHoldingAccountIdentifier)
                                                               .fullIdentifier("UK-100-10001-325")
                                                               .accountName("Test-Account-1")
                                                               .accountStatus(AccountStatus.OPEN)
                                                               .complianceStatus(ComplianceStatus.A)
                                                               .build();
        Account account1 = accountHelper.addAccount(addAccountCommand);


        AddInstallationEntityToAccountCommand addInstallationEntityToAccountCommand = AddInstallationEntityToAccountCommand.builder()
                .account(account1)
                .identifier(installationId)
                .name("Test installation")
                .permitId("permit-id")
                .regulatorType(RegulatorType.EA)
                .build();
        accountHelper.addInstallationToAccount(addInstallationEntityToAccountCommand);

        entityManager.persist(toEmissionsEntry(installationId, 2023L, 10L, LocalDateTime.of(2025, 3, 15, 10, 30)));
        entityManager.persist(toEmissionsEntry(installationId, 2023L, 50L, LocalDateTime.of(2025, 5, 15, 10, 30)));
        entityManager.persist(toEmissionsEntry(installationId, 2023L, 100L, LocalDateTime.of(2025, 9, 15, 10, 30)));
        entityManager.persist(toEmissionsEntry(installationId, 2024L, 400L, LocalDateTime.of(2025, 2, 10, 8, 30)));
        entityManager.persist(toEmissionsEntry(installationId, 2027L, 100L, LocalDateTime.of(2025, 1, 5, 6, 15)));

        //AOHA
        addAccountCommand = AddAccountCommand.builder()
                .accountHolder(accountHolder)
                .accountId(aircraftOperatorHoldingAccountIdentifier)
                .fullIdentifier("UK-100-10002-326")
                .accountName("Test-Account-2")
                .accountStatus(AccountStatus.OPEN)
                .complianceStatus(ComplianceStatus.EXCLUDED)
                .build();

        Account account2 = accountHelper.addAccount(addAccountCommand);


        AddAircraftEntityToAccountCommand addAircraftEntityToAccountCommand = AddAircraftEntityToAccountCommand.builder()
                .account(account2)
                .identifier(aircraftOperatorId)
                .name("Test installation")
                .monitoringPlanId("plan-id")
                .regulatorType(RegulatorType.OPRED)
                .build();
        accountHelper.addAircraftToAccount(addAircraftEntityToAccountCommand);
    }

    private EmissionsEntry toEmissionsEntry(Long compliantEntityId,Long year,Long emissions,LocalDateTime uploadDate) {

    	EmissionsEntry emissionsEntry = new EmissionsEntry();
    	emissionsEntry.setCompliantEntityId(compliantEntityId);
    	emissionsEntry.setYear(year);
    	emissionsEntry.setEmissions(emissions);
    	emissionsEntry.setUploadDate(uploadDate);

    	return emissionsEntry;
    }

    @Test
    public void shouldRetrieveOnlyNonEmptyEntries() {
        EmissionsEntry negativeEmissionsEntry = new EmissionsEntry();
        negativeEmissionsEntry.setEmissions(-1L);
        negativeEmissionsEntry.setCompliantEntityId(TEST_COMPLIANT_ENTITY_ID_1);
        negativeEmissionsEntry.setYear(2021L);

        EmissionsEntry zeroEmissionsEntry = new EmissionsEntry();
        zeroEmissionsEntry.setEmissions(-0L);
        zeroEmissionsEntry.setCompliantEntityId(TEST_COMPLIANT_ENTITY_ID_1);
        zeroEmissionsEntry.setYear(2021L);

        EmissionsEntry positiveEmissionsEntry = new EmissionsEntry();
        positiveEmissionsEntry.setEmissions(1L);
        positiveEmissionsEntry.setCompliantEntityId(TEST_COMPLIANT_ENTITY_ID_1);
        positiveEmissionsEntry.setYear(2021L);

        EmissionsEntry differentCompliantEntityIdEntry = new EmissionsEntry();
        differentCompliantEntityIdEntry.setEmissions(1L);
        differentCompliantEntityIdEntry.setCompliantEntityId(TEST_COMPLIANT_ENTITY_ID_2);
        differentCompliantEntityIdEntry.setYear(2021L);

        EmissionsEntry emptyEmissionsEntry = new EmissionsEntry();
        emptyEmissionsEntry.setEmissions(null);
        emptyEmissionsEntry.setCompliantEntityId(TEST_COMPLIANT_ENTITY_ID_1);
        emptyEmissionsEntry.setYear(2021L);

        EmissionsEntry afterYearEntry = new EmissionsEntry();
        afterYearEntry.setEmissions(1L);
        afterYearEntry.setCompliantEntityId(TEST_COMPLIANT_ENTITY_ID_1);
        afterYearEntry.setYear(2023L);

        emissionsEntryRepository.saveAll(List.of(negativeEmissionsEntry, zeroEmissionsEntry, positiveEmissionsEntry,
            differentCompliantEntityIdEntry));
        emissionsEntryRepository.flush();

        List<EmissionsEntry> entries = emissionsEntryRepository.findNonEmptyEntriesBeforeYear(TEST_COMPLIANT_ENTITY_ID_1, 2022);

        assertThat(entries).hasSize(2);
        assertThat(entries).extracting(EmissionsEntry::getEmissions).containsOnly(0L, 1L);
    }

    @Test
    void findLatestByCompliantEntityIdentifier() {
    	List<VerifiedEmissionsDTO> results = emissionsEntryRepository.findLatestByCompliantEntityIdentifier(installationId);
    	assertNotNull(results);
    	assertEquals(3,results.size(),"Should select 3 entries.");
    	assertEquals(new VerifiedEmissionsDTO(installationId, 2023L, Long.toString(100L), LocalDateTime.of(2025, 9, 15, 10, 30)),results.get(0));
    }
    
    @Test
    void findByCompliantEntityIdAndYearBefore() {
        List<EmissionsEntry> results = emissionsEntryRepository.findByCompliantEntityIdAndYearBefore(installationId,2024);
        assertNotNull(results);
        assertEquals(3,results.size(),"Should select 3 entries.");
    }

    @Test
    void findTotalVerifiedEmissionsByAccountIdentifier() {
    	List<Long> totalVerifiedEmissions =
            emissionsEntryRepository.findTotalVerifiedEmissionsByAccountIdentifier(operatorHoldingAccountIdentifier);
    	assertNotNull(totalVerifiedEmissions);
    	assertEquals(3,totalVerifiedEmissions.size(),"Should report total emissions for 3 years.");
    	assertEquals(600, (Long) totalVerifiedEmissions.stream().mapToLong(t -> t).sum(),"Should" +
            " report 600 total emissions.");
    }
}
