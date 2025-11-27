package gov.uk.ets.registry.api.integration.message;

import java.io.Serializable;
import java.time.Year;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AccountEmissionsUpdateEvent implements Serializable {

    private Long registryId;
    private Long reportableEmissions;
    private Year reportingYear;
}
