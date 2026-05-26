package gov.uk.ets.registry.api.regulatornotice.repository;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.regulatornotice.domain.RegulatorNotice;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create"
})
class RegulatorNoticeRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private RegulatorNoticeRepository regulatorNoticeRepository;

    @Test
    void existsByAccount_IdentifierAndStatus_should_return_true() {
        final Long accountIdentifier = 1L;
        final Account account = getOperatorHoldingAccount("name", accountIdentifier, RegistryAccountType.OPERATOR_HOLDING_ACCOUNT, AccountType.OPERATOR_HOLDING_ACCOUNT, AccountStatus.OPEN);
        entityManager.persist(account);
        RegulatorNotice regulatorNotice = getRegulatorNotice(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED, RequestType.REGULATOR_NOTICE, "processType");
        regulatorNotice.setAccount(account);
        entityManager.persist(regulatorNotice);

        final boolean result = regulatorNoticeRepository.existsByAccount_IdentifierAndStatus(accountIdentifier, RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        assertTrue(result);
    }

    @Test
    void existsByAccount_IdentifierAndStatus_wrong_status_should_return_false() {
        final Long accountIdentifier = 1L;
        final Account account = getOperatorHoldingAccount("acc name", accountIdentifier, RegistryAccountType.OPERATOR_HOLDING_ACCOUNT, AccountType.OPERATOR_HOLDING_ACCOUNT, AccountStatus.OPEN);
        entityManager.persist(account);
        RegulatorNotice regulatorNotice = getRegulatorNotice(RequestStateEnum.APPROVED, RequestType.REGULATOR_NOTICE, "processType");
        regulatorNotice.setAccount(account);
        entityManager.persist(regulatorNotice);

        final boolean result = regulatorNoticeRepository.existsByAccount_IdentifierAndStatus(accountIdentifier, RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        assertFalse(result);
    }

    @Test
    void existsByAccount_IdentifierAndStatus_wrong_identifier_should_return_false() {
        final Long accountIdentifier = 1L;
        final Account account = getOperatorHoldingAccount("acc name", accountIdentifier, RegistryAccountType.OPERATOR_HOLDING_ACCOUNT, AccountType.OPERATOR_HOLDING_ACCOUNT, AccountStatus.OPEN);
        entityManager.persist(account);
        RegulatorNotice regulatorNotice = getRegulatorNotice(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED, RequestType.REGULATOR_NOTICE, "processType");
        regulatorNotice.setAccount(account);
        entityManager.persist(regulatorNotice);

        final boolean result = regulatorNoticeRepository.existsByAccount_IdentifierAndStatus(2L, RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        assertFalse(result);
    }

    private Account getOperatorHoldingAccount(String name, Long operatorId, RegistryAccountType registryAccountType,
                                              AccountType kyotoType, AccountStatus status) {
        Account account = new Account();
        account.setAccountName(name);
        account.setAccountStatus(status);
        account.setRegistryAccountType(registryAccountType);
        account.setKyotoAccountType(kyotoType.getKyotoType());
        account.setRegistryAccountType(kyotoType.getRegistryType());
        account.setRegistryCode(kyotoType.getRegistryCode());
        account.setBillingAddressSameAsAccountHolderAddress(true);
        account.setFullIdentifier(String.format("%s-%d-%d-%d-%d", kyotoType.getRegistryCode(), kyotoType.getKyotoCode(), account.getIdentifier(), account.getCommitmentPeriodCode(), account.getCheckDigits()));
        account.setApprovalOfSecondAuthorisedRepresentativeIsRequired(true);
        account.setIdentifier(operatorId);
        account.setCommitmentPeriodCode(2);
        account.setCheckDigits(44);

        return account;
    }

    private RegulatorNotice getRegulatorNotice(RequestStateEnum status, RequestType type, String processType) {
        RegulatorNotice regulatorNotice = new RegulatorNotice();
        regulatorNotice.setStatus(status);
        regulatorNotice.setType(type);
        regulatorNotice.setProcessType(processType);

        return regulatorNotice;
    }
}
