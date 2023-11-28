package gov.uk.ets.registry.api.file.upload.allocationtable.error;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class AllocationTableBusinessRulesException extends RuntimeException {

    private final List<AllocationTableError> allocationTableErrorList = new ArrayList<>();

    public static AllocationTableBusinessRulesException create(AllocationTableError allocationTableError) {
        AllocationTableBusinessRulesException allocationTableBusinessRulesException =
            new AllocationTableBusinessRulesException();
        allocationTableBusinessRulesException.addError(allocationTableError);
        return allocationTableBusinessRulesException;
    }

    public void addError(AllocationTableError error) {
        this.allocationTableErrorList.add(error);
    }

    public void addErrors(Collection<AllocationTableError> error) {
        this.allocationTableErrorList.addAll(error);
    }
}
