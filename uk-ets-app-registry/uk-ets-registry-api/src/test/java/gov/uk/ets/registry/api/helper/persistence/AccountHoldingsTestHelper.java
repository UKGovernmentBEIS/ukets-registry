package gov.uk.ets.registry.api.helper.persistence;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.common.model.entities.Contact;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import jakarta.persistence.EntityManager;
import lombok.Builder;
import lombok.Getter;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class AccountHoldingsTestHelper {
    private EntityManager entityManager;

    public AccountHoldingsTestHelper(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void setUp(Long accountIdentifier) {
        Contact contact = new Contact();
        contact.setPhoneNumber1("12345");
        contact.setPostCode("11111");
        entityManager.persist(contact);

        AccountHolder holder = new AccountHolder();
        holder.setName("An Account Holder");
        holder.setIdentifier(89L);
        holder.setContact(contact);
        holder.setType(AccountHolderType.INDIVIDUAL);
        entityManager.persist(holder);

        Account account = new Account();
        account.setIdentifier(accountIdentifier);
        account.setFullIdentifier("UK-100-123456-324");
        account.setAccountName("Test-Account");
        account.setAccountStatus(AccountStatus.OPEN);
        account.setComplianceStatus(ComplianceStatus.A);
        account.setRegistryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);
        account.setKyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        account.setAccountHolder(holder);

        entityManager.persist(account);
    }

    public void createUnitBlock(ExpectedUnitBlockData unitBlockData) {
        UnitBlock unitBlock = new UnitBlock();
        unitBlock.setAccountIdentifier(unitBlockData.accountIdentifier);
        unitBlock.setStartBlock(unitBlockData.startSerialNumber);
        unitBlock.setEndBlock(unitBlockData.endSerialNumber);
        unitBlock.setOriginatingCountryCode(unitBlockData.originatingRegistryCode);
        unitBlock.setApplicablePeriod(unitBlockData.applicablePeriod);
        unitBlock.setOriginalPeriod(unitBlockData.originalPeriod);
        unitBlock.setSubjectToSop(unitBlockData.subjectToSop);
        unitBlock.setType(unitBlockData.unitType);
        unitBlock.setReservedForTransaction(unitBlockData.reservedForTransaction);
        entityManager.persist(unitBlock);
    }

    @Builder
    @Getter
    public static class ExpectedUnitBlockData {
        private Long accountIdentifier;
        private CommitmentPeriod applicablePeriod;
        private CommitmentPeriod originalPeriod;
        private String originatingRegistryCode;
        private UnitType unitType;
        private Boolean subjectToSop;
        private Long startSerialNumber;
        private Long endSerialNumber;
        private String reservedForTransaction;
    }

}
