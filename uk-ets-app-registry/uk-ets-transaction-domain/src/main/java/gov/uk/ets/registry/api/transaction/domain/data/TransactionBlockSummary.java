package gov.uk.ets.registry.api.transaction.domain.data;

import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.EnvironmentalActivity;
import gov.uk.ets.registry.api.transaction.domain.type.ProjectTrack;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Represents a summary of a transaction block.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = {"startBlock", "endBlock", "originatingCountryCode"})
@Builder
@AllArgsConstructor
public class TransactionBlockSummary implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = -2645745308090025905L;
    /**
     * The start serial number of the unit block.
     */
    private Long startBlock;
    /**
     * The end serial number of the unit block.
     */
    private Long endBlock;
    /**
     * The unit type.
     */
    private UnitType type;
    /**
     * The originating country code.
     */
    private String originatingCountryCode;
    /**
     * The original commitment period.
     */
    private CommitmentPeriod originalPeriod;
    /**
     * The applicable commitment period.
     */
    private CommitmentPeriod applicablePeriod;
    /**
     * The environmental LULUCF activity.
     */
    private EnvironmentalActivity environmentalActivity;
    /**
     * The expiry date.
     */
    private Date expiryDate;
    /**
     * The quantity.
     */
    private String quantity;
    /**
     * The available quantity.
     */
    private Long availableQuantity;
    /**
     * Whether this block is subject to SOP.
     */
    private Boolean subjectToSop = false;
    /**
     * The JI project number.
     */
    private String projectNumber;
    /**
     * The JI project track.
     */
    private ProjectTrack projectTrack;

    /**
     * The year (e.g. issuance year, year in commitment period etc.)
     */
    private Integer year;

    /**
     * The projects.
     */
    private List<String> projectNumbers;
    /**
     * The environmental activities.
     */
    private List<EnvironmentalActivity> environmentalActivities;

    /**
     * Constructor.
     *
     * @param type              The unit type.
     * @param originalPeriod    The original commitment period.
     * @param applicablePeriod  The applicable commitment period.
     * @param availableQuantity The available quantity.
     * @param subjectToSop      Whether the block is subject to SOP.
     */
    public TransactionBlockSummary(UnitType type, CommitmentPeriod originalPeriod, CommitmentPeriod applicablePeriod,
                                   Long availableQuantity, Boolean subjectToSop) {
        this(type, originalPeriod, applicablePeriod, null, availableQuantity, null, subjectToSop, null);
    }

    /**
     * Constructor.
     *
     * @param type                  The unit type.
     * @param originalPeriod        The original commitment period.
     * @param applicablePeriod      The applicable commitment period.
     * @param environmentalActivity The environmental activity.
     * @param availableQuantity     The available quantity.
     * @param subjectToSop          Whether the block is subject to SOP.
     * @param projectNumber         The project number.
     */
    @SuppressWarnings("java:S107")
    public TransactionBlockSummary(UnitType type, CommitmentPeriod originalPeriod, CommitmentPeriod applicablePeriod,
                                   EnvironmentalActivity environmentalActivity, Long availableQuantity, String quantity,
                                   Boolean subjectToSop, String projectNumber) {
        this(type, originalPeriod, applicablePeriod, environmentalActivity, availableQuantity, quantity, subjectToSop,
            projectNumber, null);
    }

    /**
     * Constructor.
     *
     * @param type                  The unit type.
     * @param originalPeriod        The original commitment period.
     * @param applicablePeriod      The applicable commitment period.
     * @param environmentalActivity The environmental activity.
     * @param availableQuantity     The available quantity.
     * @param subjectToSop          Whether the block is subject to SOP.
     * @param projectNumber         The project number.
     * @param originatingCountryCode The originating country code.
     */
    @SuppressWarnings("java:S107")
    public TransactionBlockSummary(UnitType type, CommitmentPeriod originalPeriod, CommitmentPeriod applicablePeriod,
                                   EnvironmentalActivity environmentalActivity, Long availableQuantity, String quantity,
                                   Boolean subjectToSop, String projectNumber, String originatingCountryCode) {

        this.type = type;
        this.originalPeriod = originalPeriod;
        this.applicablePeriod = applicablePeriod;
        this.environmentalActivity = environmentalActivity;
        this.availableQuantity = availableQuantity;
        this.quantity = quantity;
        this.subjectToSop = subjectToSop;
        this.projectNumber = projectNumber;
        this.originatingCountryCode = originatingCountryCode;
    }

    /**
     * Calculates the quantity of this block.
     *
     * @return a number.
     */
    public Long calculateQuantity() {
        Long result = 0L;
        if (Utils.isLong(quantity)) {
            result = NumberUtils.createLong(quantity);
        }
        return result;
    }

}
