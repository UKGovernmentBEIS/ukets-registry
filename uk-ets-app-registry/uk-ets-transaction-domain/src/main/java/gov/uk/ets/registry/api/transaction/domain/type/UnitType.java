package gov.uk.ets.registry.api.transaction.domain.type;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Getter;

/**
 * Holds the various unit types.
 */
@Getter
public enum UnitType {

    /**
     * Assigned Amount Unit.
     */
    AAU(1, 0),

    /**
     * Removal Unit.
     */
    RMU(2, 0, false, true),

    /**
     * Emission Reduction Unit (converted from an AAU).
     */
    ERU_FROM_AAU(3, 0, true),

    /**
     * Emission Reduction Unit (converted from an RMU).
     */
    ERU_FROM_RMU(4, 0, true),

    /**
     * Certified Emission Reduction unit.
     */
    CER(5, 0, true),

    /**
     * Temporary CER.
     */
    TCER(6, 0, true),

    /**
     * Long-term CER.
     */
    LCER(7, 0, true),

    /**
     * Non-Kyoto unit.
     */
    NON_KYOTO(0, 0),

    /**
     * Multiple unit types.
     */
    MULTIPLE(-1, -1),

    /**
     * UK Allowances.
     */
    ALLOWANCE(0, 10),

    /**
     * Unit type needed for the KP data migration from EUCR
     */
    ALLOWANCE_CP0(0, 2),

    /**
     * Unit type needed for the KP data migration from EUCR
     */
    FORMER_EUA(1, 1),

    /**
     * Unit type needed for the KP data migration from EUCR
     */
    ALLOWANCE_CHAPTER3(0, 5);

    /**
     * The primary code.
     */
    private Integer primaryCode;

    /**
     * The supplementary code.
     */
    private Integer supplementaryCode;

    /**
     * Whether this unit type is related with JI projects.
     */
    private Boolean relatedWithProject;

    /**
     * Whether this unit type is related with an environmental activity (LULUCF).
     */
    private Boolean relatedWithEnvironmentalActivity;

    /**
     * Constructor.
     *
     * @param primaryCode       The primary code.
     * @param supplementaryCode The supplementary code.
     */
    UnitType(Integer primaryCode, Integer supplementaryCode) {
        this(primaryCode, supplementaryCode, false);
    }

    /**
     * Constructor.
     *
     * @param primaryCode        The primary code.
     * @param supplementaryCode  The supplementary code.
     * @param relatedWithProject Whether this unit types is related with a project.
     */
    UnitType(Integer primaryCode, Integer supplementaryCode, Boolean relatedWithProject) {
        this(primaryCode, supplementaryCode, relatedWithProject, false);
    }

    /**
     * Constructor.
     *
     * @param primaryCode        The primary code.
     * @param supplementaryCode  The supplementary code.
     * @param relatedWithProject Whether this unit types is related with a project.
     */
    UnitType(Integer primaryCode, Integer supplementaryCode, Boolean relatedWithProject,
             Boolean relatedWithEnvironmentalActivity) {
        this.primaryCode = primaryCode;
        this.supplementaryCode = supplementaryCode;
        this.relatedWithProject = relatedWithProject;
        this.relatedWithEnvironmentalActivity = relatedWithEnvironmentalActivity;
    }

    /**
     * Returns whether it is a Kyoto unit type.
     *
     * @return false/true
     */
    public Boolean isKyoto() {
        return primaryCode > 0;
    }

    /**
     * Whether this unit type is subject to SOP.
     *
     * @return false/true.
     */
    public Boolean isSubjectToSop() {
        return AAU.equals(this) || ERU_FROM_AAU.equals(this) || ERU_FROM_RMU.equals(this);
    }

    public boolean isRelatedWithProject() {
        return this.relatedWithProject != null && this.relatedWithProject.equals(true);
    }

    /**
     * Whether this unit type is transferred to SOP.
     *
     * @return false/true
     */
    public Boolean isTransferredToSop() {
        return ERU_FROM_AAU.equals(this) || ERU_FROM_RMU.equals(this);
    }

    /**
     * Identifies the unity type based on the primary and supplementary codes.
     *
     * @param primaryCode       The primary code.
     * @param supplementaryCode The supplementary code.
     * @return a unit type.
     */
    public static UnitType of(int primaryCode, int supplementaryCode) {
        UnitType result = null;
        Optional<UnitType> optional = Stream.of(values())
            .filter(type ->
                type.getPrimaryCode().equals(primaryCode) &&
                    type.getSupplementaryCode().equals(supplementaryCode))
            .findFirst();
        if (optional.isPresent()) {
            result = optional.get();
        }
        return result;
    }

    /**
     * TODO what should we do when we have non Kyoto Protocol(supplementaryCode code 10) as far as notifications is concerned?
     * <p>
     * Identifies the unity type based on the primary for finding Kyoto Protocol codes.
     *
     * @param primaryCode The primary code.
     * @return a unit type.
     */
    public static UnitType exceptForUkAllowances(int primaryCode) {
        UnitType result = null;
        Optional<UnitType> optional = Stream.of(values())
            .filter(type ->
                type.getPrimaryCode().equals(primaryCode) &&
                    !type.getSupplementaryCode().equals(10))
            .findFirst();
        if (optional.isPresent()) {
            result = optional.get();
        }
        return result;
    }

    public static UnitType findByPrimaryCode(Integer unitTypeCode) {
        return Arrays.stream(values())
            .filter(unitType -> unitType.getPrimaryCode().equals(unitTypeCode))
            .findFirst()
            .orElse(null);
    }

}
