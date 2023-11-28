package gov.uk.ets.registry.api.transaction.checks;

import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import lombok.Builder;

public class AccountSummaryMock extends AccountSummary {

    /**
     * The {@link AccountSummaryMock} constructor
     * @param identifier the identifier
     * @param registryAccountType the registryAccountType
     * @param kyotoAccountType the kyotoAccountType
     * @param accountStatus the accountStatus
     * @param registryCode the registryCode
     * @param fullIdentifier the fullIdentifier
     * @param commitmentPeriod the commitmentPeriod
     * @param balance the balance
     * @param unitType the unitType
     */
    @Builder
    public AccountSummaryMock(Long identifier, RegistryAccountType registryAccountType, KyotoAccountType kyotoAccountType, AccountStatus accountStatus, String registryCode, String fullIdentifier, Integer commitmentPeriod, Long balance, UnitType unitType) {
        super(identifier, registryAccountType, kyotoAccountType, accountStatus, registryCode, fullIdentifier, commitmentPeriod, balance, unitType);
    }
}
