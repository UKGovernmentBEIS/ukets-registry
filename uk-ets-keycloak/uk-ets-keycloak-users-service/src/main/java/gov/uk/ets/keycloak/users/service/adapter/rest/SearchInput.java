package gov.uk.ets.keycloak.users.service.adapter.rest;

import jakarta.ws.rs.QueryParam;
import lombok.Getter;
import lombok.Setter;

/**
 * The keycloak rest endpoint's query parameters
 */
@Getter
@Setter
public class SearchInput {
    /**
     * The user name or id search parameter
     */
    @QueryParam("nameOrUserId")
    private String nameOrUserId;
    /**
     * The user status search parameter
     */
    @QueryParam("status")
    private String status;
    /**
     * The user email search parameter
     */
    @QueryParam("email")
    private String email;
    /**
     * The user's last sign in from search parameter
     */
    @QueryParam("lastSignInFrom")
    private String lastSignInFrom;
    /**
     * The user's last sign in to search parameter
     */
    @QueryParam("lastSignInTo")
    private String lastSignInTo;
    /**
     * The user's role name search parameter
     */
    @QueryParam("role")
    private String role;
    /**
     * The sorting parameter
     */
    @QueryParam("sortField")
    private String sortField;
    /**
     * The sorting direction parameter, ASC or DESC
     */
    @QueryParam("sortDirection")
    private String sortDirection;
    /**
     * The results page number
     */
    @QueryParam("page")
    private Integer page;
    /**
     * The results page size
     */
    @QueryParam("pageSize")
    private Long pageSize;

    @Override
    public String toString() {
        return "SearchInput{" +
            "nameOrUserId='" + nameOrUserId + '\'' +
            ", status='" + status + '\'' +
            ", email='" + email + '\'' +
            ", lastSignInFrom='" + lastSignInFrom + '\'' +
            ", lastSignInTo='" + lastSignInTo + '\'' +
            ", role='" + role + '\'' +
            ", sortField='" + sortField + '\'' +
            ", sortDirection='" + sortDirection + '\'' +
            ", page=" + page +
            ", pageSize=" + pageSize +
            '}';
    }
}
