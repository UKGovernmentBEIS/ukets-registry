package gov.uk.ets.registry.api.user.admin.shared;

import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * The user search criteria data transfer object
 */
@Getter
@Setter
public class KeycloakUserSearchCriteria {
    /**
     * The user name or id search parameter
     */
    @Size(min = 3)
    private String nameOrUserId;
    /**
     * The user status search parameter
     */
    private String status;
    /**
     * The user email search parameter
     */
    @Size(min = 3)
    private String email;
    /**
     * The user's last sign in from search parameter
     */
    private String lastSignInFrom;
    /**
     * The user's last sign in to search parameter
     */
    private String lastSignInTo;
    /**
     * The user's role name search parameter
     */
    private String role;
    /**
     * The sorting parameter
     */
    private String sortField;
    /**
     * The sorting direction parameter, ASC or DESC
     */
    private String sortDirection;
    /**
     * The results page number
     */
    private Integer page;
    /**
     * The results page size
     */
    private Long pageSize;

}
