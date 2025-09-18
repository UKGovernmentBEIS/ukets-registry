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

    /**
     * Retrieves user work contacts in batches of 50.
     * @param urIds List of unique user business identifiers.
     * @return The list of {@link UserWorkContact} work contacts.
     */
    List<UserWorkContact> fetchUserWorkContactsInBatches(Set<String> urIds);
}
