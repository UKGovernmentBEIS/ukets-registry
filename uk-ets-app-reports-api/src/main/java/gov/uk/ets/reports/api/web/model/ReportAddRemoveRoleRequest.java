package gov.uk.ets.reports.api.web.model;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.reports.model.ReportRequestingRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static gov.uk.ets.commons.logging.RequestParamType.USER_ID;

@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
@Builder
@AllArgsConstructor
public class ReportAddRemoveRoleRequest {
    
    /**
     * The role of the user that requested the role change.
     */
    private ReportRequestingRole requestingRole;
    
    /**
     * The keycloak id of the user that will change role.
     */
    @MDCParam(USER_ID)
    private String userId;
}
