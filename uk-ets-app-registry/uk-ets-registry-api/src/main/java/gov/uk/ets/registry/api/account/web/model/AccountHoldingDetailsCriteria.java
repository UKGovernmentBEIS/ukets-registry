package gov.uk.ets.registry.api.account.web.model;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountHoldingDetailsCriteria implements Serializable {
    @NotNull
    private Long accountId;
    @NotNull
    private String unit;
    @NotNull
    private Integer originalPeriodCode;
    @NotNull
    private Integer applicablePeriodCode;
    private Boolean subjectToSop;
}
