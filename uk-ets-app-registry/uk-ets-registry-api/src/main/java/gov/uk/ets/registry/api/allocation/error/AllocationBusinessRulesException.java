package gov.uk.ets.registry.api.allocation.error;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class AllocationBusinessRulesException extends RuntimeException {

    private List<AllocationError> allocationErrorList = new ArrayList<>();

    public static AllocationBusinessRulesException create(AllocationError allocationError) {
        AllocationBusinessRulesException allocationBusinessRulesException = new AllocationBusinessRulesException();
        allocationBusinessRulesException.addError(allocationError);
        return allocationBusinessRulesException;
    }

    public void addError(AllocationError error) {
        this.allocationErrorList.add(error);
    }

    public void addErrors(Collection<AllocationError> error) {
        this.allocationErrorList.addAll(error);
    }
}
