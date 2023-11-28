package gov.uk.ets.reports.generator.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountAccess {
    private String state;
    private String accessRights;
}
