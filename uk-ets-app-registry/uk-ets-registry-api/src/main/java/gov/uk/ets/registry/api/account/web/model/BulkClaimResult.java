package gov.uk.ets.registry.api.account.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BulkClaimResult {

    private int total;
    private int successful;
    private int failed;
}
