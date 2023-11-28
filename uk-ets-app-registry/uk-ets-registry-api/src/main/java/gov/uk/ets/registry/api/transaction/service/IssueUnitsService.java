package gov.uk.ets.registry.api.transaction.service;

import gov.uk.ets.registry.api.transaction.domain.data.AccountInfo;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryLevelType;
import gov.uk.ets.registry.api.transaction.repository.RegistryLevelRepository;
import gov.uk.ets.registry.api.transaction.repository.RegistryLevelRepository.RegistryLevelProjection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Transaction service.
 */
@Service
@RequiredArgsConstructor
public class IssueUnitsService {

    private final RegistryLevelRepository registryLevelRepository;

    public List<AccountInfo> getAccountForCommitmentPeriod(CommitmentPeriod commitmentPeriod) {
        return registryLevelRepository.findAccountByCommitmentPeriod(
            commitmentPeriod.getCode(), KyotoAccountType.PARTY_HOLDING_ACCOUNT, RegistryAccountType.NONE,
            List.of(AccountStatus.CLOSURE_PENDING,AccountStatus.CLOSED));
    }

    public List<RegistryLevelProjection> getUnitTypesForCommitmentPeriod(
        CommitmentPeriod commitmentPeriod, RegistryLevelType registryLevelType) {
        return registryLevelRepository.findByPeriodAndTypeOrderById(commitmentPeriod,
            registryLevelType);
    }
}
