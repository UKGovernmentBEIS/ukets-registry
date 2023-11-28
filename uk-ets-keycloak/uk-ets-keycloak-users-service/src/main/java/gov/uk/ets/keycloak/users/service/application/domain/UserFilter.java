package gov.uk.ets.keycloak.users.service.application.domain;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

/**
 * Value object for filtering users.
 */
@Getter
@Builder
public class UserFilter {
    /**
     * The name or user id filter
     */
    private String nameOrUserId;
    /**
     * The user state/status filter
     */
    private List<String> statuses;
    /**
     * The user email filter
     */
    private String email;
    /**
     * The user's last sign-in date from filter.
     */
    private LocalDateTime lastSignInFrom;
    /**
     * The user's last sign-in date until filter.
     */
    private LocalDateTime lastSignInTo;
    /**
     * The user roles filter
     */
    private List<String> roles;
    /**
     * The roles that are excluded
     */
    private List<String> excludedRoles;
    /**
     * The order by field info
     */
    private String sortField;
    /**
     * The order direction of results (ASC or DESC)
     */
    private String sortingDirection;
    /**
     * The page of results
     */
    private Integer page;
    /**
     * The results page size
     */
    private Long pageSize;

    @Override
    public String toString() {
        return "UserFilter{" +
            "nameOrUserId='" + nameOrUserId + '\'' +
            ", statuses='" + statuses + '\'' +
            ", email='" + email + '\'' +
            ", lastSignInFrom=" + lastSignInFrom +
            ", lastSignInTo=" + lastSignInTo +
            ", roles=" + roles +
            ", excludedRoles=" + excludedRoles +
            ", sortField='" + sortField + '\'' +
            ", sortingDirection='" + sortingDirection + '\'' +
            ", page=" + page +
            ", pageSize=" + pageSize +
            '}';
    }
}
