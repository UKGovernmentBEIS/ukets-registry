package gov.uk.ets.registry.api.user.domain;
import java.util.List;
import java.util.Set;

/**
 * Repository for {@link UserWorkContact} entities
 */
public interface UserWorkContactRepository {
    /**
     * Fetches the personal user info of the users of the passed urids
     * @param urids List of unique user business identifiers
     * @return The {@link UserWorkContact} personal user infos
     */
    List<UserWorkContact> fetch(Set<String> urids, boolean withServiceAccount);
}
