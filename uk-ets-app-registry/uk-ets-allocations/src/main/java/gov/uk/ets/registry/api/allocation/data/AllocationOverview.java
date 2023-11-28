package gov.uk.ets.registry.api.allocation.data;

import gov.uk.ets.registry.api.allocation.type.AllocationCategory;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an allocation overview.
 */
@Setter
@Getter
public class AllocationOverview implements Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -1560117570227378652L;

    /**
     * The allocation year.
     */
    private Integer year;

    /**
     * The allocation category.
     */
    private AllocationCategory category;

    /**
     * The total quantity to allocate.
     */
    private Long totalQuantity;

    /**
     * The total numbers.
     */
    private AllocationOverviewRow total;

    /**
     * The allocation overview rows.
     */
    private Map<AllocationType, AllocationOverviewRow> rows = new EnumMap<>(AllocationType.class);

    /**
     * Returns the NAT overview.
     * @return the NAT overview.
     */
    public AllocationOverviewRow getInstallations() {
        return rows.get(AllocationType.NAT);
    }

    /**
     * Returns the NAVAT overview.
     * @return the NAVAT overview.
     */
    public AllocationOverviewRow getAircraftOperators() {
        return rows.get(AllocationType.NAVAT);
    }

    /**
     * Returns the NER overview.
     * @return the NER overview.
     */
    public AllocationOverviewRow getInstallationsNewEntrants() {
        return rows.get(AllocationType.NER);
    }

    /**
     * The beneficiary recipients of the allocation.
     */
    private List<AllocationSummary> beneficiaryRecipients;

    /**
     * Adds an allocation row.
     * @param type The type.
     * @param row The row.
     */
    public void add(AllocationType type, AllocationOverviewRow row) {
        rows.put(type, row);
    }

}
