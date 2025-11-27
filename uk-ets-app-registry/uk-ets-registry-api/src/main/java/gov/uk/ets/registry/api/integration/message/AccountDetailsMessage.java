package gov.uk.ets.registry.api.integration.message;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AccountDetailsMessage {

    private String emitterId;
    private String installationActivityType;
    private String installationName;
    private String accountName;
    private String permitId;
    /*
     * If Permit ID is not available by METS, Registry should save the Emitter ID.
     */
    private String monitoringPlanId;
    // todo: introduce it to Registry
    private Date empPermitIssuanceDate;
    private String companyImoNumber;
    /*
     * Only following values are accepted: EA, SEPA, NRW, OPRED, DAERA
     */
    private String regulator;
    /*
     * First year of verified emission submission.
     * Cannot be less than 2021 for OAH/AOHA
     * Cannot be less than 2026 for MOHA
     */
    private Integer firstYearOfVerifiedEmissions;
}
