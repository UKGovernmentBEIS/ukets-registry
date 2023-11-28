package gov.uk.ets.registry.api.account.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.AircraftOperator;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.account.domain.types.PermitStatus;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.file.upload.emissionstable.model.SubmitEmissionsValidityInfo;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddAccountCommand;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddAccountHolderCommand;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddAircraftEntityToAccountCommand;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddInstallationEntityToAccountCommand;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;

@DataJpaTest
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create"})
public class CompliantEntityRepositoryTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private CompliantEntityRepository compliantEntityRepository;

    private AircraftOperator aircraftOperator;
    private AircraftOperator unlinkedAircraftOperator;
    
    @BeforeEach
    void setUp() {
        AccountModelTestHelper accountHelper = new AccountModelTestHelper(entityManager);
        
        AddAccountHolderCommand addAccountHolderCommand = AddAccountHolderCommand.builder()
                                                         .accountHolderType(AccountHolderType.ORGANISATION)
                                                         .identifier(100001L)
                                                         .name("Test account holder name")
                                                         .status(Status.ACTIVE)
                                                         .build();
        AccountHolder accountHolder = accountHelper.addAccountHolder(addAccountHolderCommand);

        AddAccountCommand addAccountCommand = AddAccountCommand.builder()
                                                               .accountHolder(accountHolder)
                                                               .accountId(10001L)
                                                               .fullIdentifier("UK-100-10001-325")
                                                               .accountName("Test-Account-1")
                                                               .accountStatus(AccountStatus.OPEN)
                                                               .complianceStatus(ComplianceStatus.A)
                                                               .build();
        Account account1 = accountHelper.addAccount(addAccountCommand);

        
        AddInstallationEntityToAccountCommand addInstallationEntityToAccountCommand = AddInstallationEntityToAccountCommand.builder()
                .account(account1)
                .identifier(123L)
                .name("Test installation")
                .permitId("permit-id")
                .regulatorType(RegulatorType.EA)
                .build();
        accountHelper.addInstallationToAccount(addInstallationEntityToAccountCommand);
        
        addAccountCommand = AddAccountCommand.builder()
                .accountHolder(accountHolder)
                .accountId(10002L)
                .fullIdentifier("UK-100-10002-326")
                .accountName("Test-Account-2")
                .accountStatus(AccountStatus.OPEN)
                .complianceStatus(ComplianceStatus.EXCLUDED)
                .build();
        
        Account account2 = accountHelper.addAccount(addAccountCommand);
        
        AddAircraftEntityToAccountCommand addAircraftEntityToAccountCommand = AddAircraftEntityToAccountCommand.builder()
                .account(account2)
                .identifier(333L)
                .name("Test installation")
                .monitoringPlanId("plan-id")
                .regulatorType(RegulatorType.OPRED)
                .build();
        accountHelper.addAircraftToAccount(addAircraftEntityToAccountCommand);
        
        unlinkedAircraftOperator = new AircraftOperator();
        unlinkedAircraftOperator.setIdentifier(433L);
        unlinkedAircraftOperator.setRegulator(RegulatorType.NRW);
        unlinkedAircraftOperator.setStartYear(2020);
        unlinkedAircraftOperator.setEndYear(2025);
        unlinkedAircraftOperator.setPermitStatus(PermitStatus.ACTIVE);
        unlinkedAircraftOperator.setStatus(Status.ACTIVE);
        entityManager.persist(unlinkedAircraftOperator);
    }	
	
	@DisplayName("Query findAllIdentifiersFetchAccountStatusAndYears executes with success.")
	@Test
	void findAllIdentifiersFetchAccountStatus() {

		List<SubmitEmissionsValidityInfo> result = compliantEntityRepository.findAllIdentifiersFetchAccountStatusAndYears();
		assertNotNull(result);
	}
	
	@DisplayName("Query findAllIdentifiersFetchAccountStatusAndYears fetches all entities.")
	@Test
	void findAllIdentifiersFetchAccountStatusAndYearsShouldFetchEverything() {

		List<SubmitEmissionsValidityInfo> result = compliantEntityRepository.findAllIdentifiersFetchAccountStatusAndYears();

		assertNotNull(result);
		assertEquals(3,result.size());
		assertEquals(1,result.stream().filter(t-> Objects.isNull(t.getAccountStatus())).count());
		assertEquals(2,result.stream().filter(t-> Objects.nonNull(t.getAccountStatus())).count());
		assertEquals(3,result.stream().filter(t-> Objects.nonNull(t.getStartYear())).count());
		assertEquals(3,result.stream().filter(t-> Objects.nonNull(t.getEndYear())).count());
	}
}
