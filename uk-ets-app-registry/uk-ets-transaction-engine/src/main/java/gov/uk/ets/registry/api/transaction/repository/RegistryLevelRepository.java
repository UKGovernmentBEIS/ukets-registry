package gov.uk.ets.registry.api.transaction.repository;

import gov.uk.ets.registry.api.transaction.domain.RegistryLevel;
import gov.uk.ets.registry.api.transaction.domain.data.AccountInfo;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.EnvironmentalActivity;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryLevelType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.util.List;
import lombok.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository for registry levels/limits/entitlements.
 */
public interface RegistryLevelRepository extends JpaRepository<RegistryLevel, Long> {

    /**
     * Returns a record of the provided type.
     *
     * @param type                  The type.
     * @param unitType              the unit type.
     * @param period                The commitment period.
     * @param environmentalActivity The environmental activity.
     * @return a value
     */
    RegistryLevel findByTypeAndUnitTypeAndPeriodAndEnvironmentalActivity(RegistryLevelType type,
                                                                         UnitType unitType, CommitmentPeriod period,
                                                                         EnvironmentalActivity environmentalActivity);

    /**
     * Returns a record of the provided type.
     *
     * @param type     The type.
     * @param unitType the unit type.
     * @param period   The commitment period.
     * @return a value
     */
    RegistryLevel findByTypeAndUnitTypeAndPeriod(RegistryLevelType type, UnitType unitType,
                                                 CommitmentPeriod period);

    List<RegistryLevelProjection> findByPeriodAndTypeOrderById(CommitmentPeriod period,
                                                               RegistryLevelType type);

    RegistryLevelProjection findByTypeAndUnitTypeAndEnvironmentalActivityAndPeriod(
        RegistryLevelType type, UnitType unitType, EnvironmentalActivity activity,
        CommitmentPeriod period);

    @Query("select new gov.uk.ets.registry.api.transaction.domain.data.AccountInfo(" +
        "      a.identifier, " +
        "      a.fullIdentifier," +
        "      a.accountName," +
        "      ''," +
        "      a.registryCode)" +
        " from Account a" +
        " where a.commitmentPeriodCode <= :commitmentPeriodCode and a.kyotoAccountType = :kyotoAccountType and a" +
        ".registryAccountType = :registryAccountType and a.accountStatus not in :accountStatus")
    List<AccountInfo> findAccountByCommitmentPeriod(
        @Param("commitmentPeriodCode") Integer commitmentPeriod,
        @Param("kyotoAccountType") KyotoAccountType kyotoAccountType,
        @Param("registryAccountType") RegistryAccountType registryAccountType,
        @Param("accountStatus") List<AccountStatus> accountStatus);

    @Value
    final class CommitmentPeriodProjection {

        CommitmentPeriod period;
    }

    @Value
    final class RegistryLevelProjection {

        Long id;
        UnitType unitType;
        EnvironmentalActivity environmentalActivity;
        Long initialQuantity;
        Long consumedQuantity;
        Long pendingQuantity;
    }
}



