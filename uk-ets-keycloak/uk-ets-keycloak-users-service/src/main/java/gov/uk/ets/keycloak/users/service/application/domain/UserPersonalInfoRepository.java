package gov.uk.ets.keycloak.users.service.application.domain;

import java.util.List;

/**
 * Repository for dealing with user personal info.
 */
public interface UserPersonalInfoRepository {
    /**
     * Retrieves personal info of the corresponded to urid list users
     * @param urids The urid list
     * @return The list of {@link UserPersonalInfo}
     */
    List<UserPersonalInfo> fetchUserPersonalInfos(List<String> urids);
}
