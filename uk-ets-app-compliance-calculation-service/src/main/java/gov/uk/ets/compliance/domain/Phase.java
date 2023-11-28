package gov.uk.ets.compliance.domain;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Phase {

    Date startDate;
    Date endDate;
    String name;
    String description;
}
