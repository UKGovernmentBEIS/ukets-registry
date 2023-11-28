package gov.uk.ets.registry.api.transaction.service;

import gov.uk.ets.registry.api.transaction.domain.RegistryLevel;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.EnvironmentalActivity;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryLevelType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.repository.RegistryLevelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for maintaining registry levels.
 */
@Service
public class LevelService {

    /**
     * The persistence service.
     */
    private final RegistryLevelRepository registryLevelRepository;

    /**
     * The {@link LevelService} constructor
     * @param registryLevelRepository the registryLevelRepository
     */
    public LevelService(RegistryLevelRepository registryLevelRepository) {
        this.registryLevelRepository = registryLevelRepository;
    }

    /**
     * Returns the remaining value of the level.
     * @param type The level type.
     * @param unitType The unit type.
     * @param period The commitment period.
     * @return the remaining value.
     */
    public Long getRemainingValue(RegistryLevelType type, UnitType unitType, CommitmentPeriod period) {
        return getRemainingValue(type, unitType, period, null);
    }

    /**
     * Returns the remaining value of the level.
     * @param type The level type.
     * @param unitType The unit type.
     * @param period The commitment period.
     * @param environmentalActivity The environmental activity.
     * @return the remaining value.
     */
    public Long getRemainingValue(RegistryLevelType type, UnitType unitType, CommitmentPeriod period, EnvironmentalActivity environmentalActivity) {
        RegistryLevel level = getRegistryLevel(type, unitType, period, environmentalActivity);
        return level.getInitialQuantity() - level.getConsumedQuantity() - level.getPendingQuantity();
    }

    /**
     * Reserves A level value.
     * @param value The value to reserve.
     * @param type The level type.
     * @param unitType The unit type.
     * @param period The period.
     * @param environmentalActivity The environmental activity.
     */
    @Transactional
    public void reserve(Long value, RegistryLevelType type, UnitType unitType, CommitmentPeriod period, EnvironmentalActivity environmentalActivity) {
        RegistryLevel level = getRegistryLevel(type, unitType, period, environmentalActivity);
        level.setPendingQuantity(level.getPendingQuantity() + value);
        registryLevelRepository.save(level);
    }

    /**
     * Consumes A level value.
     * @param value The value to reserve.
     * @param type The level type.
     * @param unitType The unit type.
     * @param period The period.
     * @param environmentalActivity The environmental activity.
     */
    @Transactional
    public void consume(Long value, RegistryLevelType type, UnitType unitType, CommitmentPeriod period, EnvironmentalActivity environmentalActivity) {
        RegistryLevel level = getRegistryLevel(type, unitType, period, environmentalActivity);
        level.setPendingQuantity(level.getPendingQuantity() - value);
        level.setConsumedQuantity(level.getConsumedQuantity() + value);
        registryLevelRepository.save(level);
    }

    /**
     * Releases A level value.
     * @param value The value to reserve.
     * @param type The level type.
     * @param unitType The unit type.
     * @param period The period.
     * @param environmentalActivity The environmental activity.
     */
    @Transactional
    public void release(Long value, RegistryLevelType type, UnitType unitType, CommitmentPeriod period, EnvironmentalActivity environmentalActivity) {
        RegistryLevel level = getRegistryLevel(type, unitType, period, environmentalActivity);
        level.setPendingQuantity(level.getPendingQuantity() - value);
        registryLevelRepository.save(level);
    }

    /**
     * Retrieves the registry level based on the provided criteria.
     * @param type The level type.
     * @param unitType The unit type.
     * @param period The commitment period.
     * @param environmentalActivity The environmental activity.
     * @return the level.
     */
    private RegistryLevel getRegistryLevel(RegistryLevelType type, UnitType unitType, CommitmentPeriod period, EnvironmentalActivity environmentalActivity) {
        RegistryLevel level;
        if (environmentalActivity == null) {
            level = registryLevelRepository.findByTypeAndUnitTypeAndPeriod(type, unitType, period);

        } else {
            level = registryLevelRepository.findByTypeAndUnitTypeAndPeriodAndEnvironmentalActivity(type, unitType, period, environmentalActivity);
        }
        return level;
    }

}
