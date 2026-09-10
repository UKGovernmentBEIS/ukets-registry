package gov.uk.ets.registry.api.user.admin.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCRCUpdateDTO {

    /**
     * Criminal record check.
     */
    private boolean crc;

    /**
     * The criminal record check issuance date ISO string.
     */
    private String crcIssuanceDate;
}
