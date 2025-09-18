package gov.uk.ets.registry.api.allocation.web.model;


import gov.uk.ets.registry.api.allocation.type.AllocationCategory;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllocationRequest {

    @NotNull
    @Digits(integer = 4, fraction = 0, message = "Not a valid year.")
    private Integer allocationYear;

    @NotNull
    private AllocationCategory allocationCategory;
}
