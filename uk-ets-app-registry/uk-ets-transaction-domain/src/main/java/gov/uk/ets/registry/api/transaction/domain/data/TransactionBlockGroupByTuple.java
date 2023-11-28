package gov.uk.ets.registry.api.transaction.domain.data;

import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.EnvironmentalActivity;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TransactionBlockGroupByTuple {
    UnitType type;
    CommitmentPeriod originalPeriod;
    CommitmentPeriod applicablePeriod;
    EnvironmentalActivity environmentalActivity;
    Boolean subjectToSop;
    String projectNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TransactionBlockGroupByTuple that = (TransactionBlockGroupByTuple) o;
        return type == that.type &&
            originalPeriod == that.originalPeriod &&
            applicablePeriod == that.applicablePeriod &&
            Objects.equals(subjectToSop, that.subjectToSop);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, originalPeriod, applicablePeriod, subjectToSop);
    }
}
