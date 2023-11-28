package gov.uk.ets.registry.api.task.searchmetadata.utils;

import org.hibernate.event.spi.AbstractEvent;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostUpdateEvent;

import java.util.HashMap;
import java.util.Map;

public class QueryUtils {

    private static final String DEFAULT_INSERT_QUERY =
            "INSERT INTO task_search_metadata (id, task_id, metadata_name, metadata_value) " +
            "VALUES (nextval('task_search_metadata_seq'), :task_id, :metadata_name, :metadata_value)";

    private static final String DEFAULT_UPDATE_QUERY =
            "UPDATE task_search_metadata SET metadata_value = :metadata_value " +
            "WHERE task_id = :task_id and metadata_name = :metadata_name";

    private static final String USER_METADATA_INSERT_QUERY =
            "INSERT INTO task_search_metadata (id,task_id, metadata_name, metadata_value) " +
            "SELECT nextval('task_search_metadata_seq'),:taskId, :metadataName, CONCAT(urid,', ',first_name, ' ', last_name, ', ', known_as) " +
            "FROM users WHERE users.urid = :urid";
    private static final String USER_METADATA_UPDATE_QUERY = "UPDATE task_search_metadata " +
            "SET metadata_value = (SELECT CONCAT(users.urid, ', ', users.first_name, ' ', users.last_name, ', ', users.known_as) FROM users WHERE users.urid = :urid) " +
            "WHERE trim(split_part(metadata_value, ',', 1)) = :urid";

    private static final String USER_METADATA_USER_DETAILS_UPDATE_QUERY = "UPDATE task_search_metadata " +
            "SET metadata_value = CONCAT(:urid, ', ', :fullName, ', ', :knownAs) " +
            "WHERE trim(split_part(metadata_value, ',', 1)) = :urid";

    public static Map<String, Object> createDefaultQueryParams(Long taskId, String name, String value) {
        Map<String, Object> params = new HashMap<>();
        params.put("task_id", taskId);
        params.put("metadata_name", name);
        params.put("metadata_value", value);
        return params;
    }

    public static Map<String, Object> createQueryParamsForUserMetadata(Long taskId, String metadataName, String urid,AbstractEvent abstractEvent) {
        Map<String, Object> params = new HashMap<>();

        if (abstractEvent == null || abstractEvent instanceof PostInsertEvent) {
            params.put("taskId", taskId);
            params.put("metadataName", metadataName);
        }
        params.put("urid", urid);
        return params;
    }

    public static Map<String, Object> createQueryParamsForUserUpdateDetails(String urid, String fullName, String knownAs) {
        Map<String, Object> params = new HashMap<>();
        params.put("urid", urid);
        params.put("fullName", fullName);
        params.put("knownAs", knownAs);
        return params;
    }

    public static String getQuery(boolean isUserQuery, AbstractEvent abstractEvent) {
        if (abstractEvent instanceof PostInsertEvent && isUserQuery) {
            return USER_METADATA_INSERT_QUERY;
        } else if (abstractEvent instanceof PostUpdateEvent && isUserQuery) {
            return USER_METADATA_UPDATE_QUERY;
        } else if (abstractEvent instanceof PostInsertEvent) {
            return DEFAULT_INSERT_QUERY;
        } else {
            return DEFAULT_UPDATE_QUERY;
        }
    }

    public static String getQueryForUserDetailsUpdate() {
        return USER_METADATA_USER_DETAILS_UPDATE_QUERY;
    }


}
