package gov.uk.ets.registry.api.authz.miners;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;

public interface RoleMiner {

    /**
     * Extracts role names from a policy configuration.
     * @param configuration the policy configuration
     * @return the list of role names
     */
    default List<String> mineRole(Map<String, String> configuration) {
        String elementValue = configuration.get("roles");
        List<String> keyValues = StringUtils.isEmpty(elementValue)
            ? new ArrayList<>()
            :  List.of(elementValue
            .replace("[{", "")
            .replace("\"", "")
            .replace("}]", "")
            .split(","))
            .stream()
            .map(String::strip)
            .collect(Collectors.toList());
        return keyValues
            .stream()
            .filter(kv -> kv.contains("id:"))
            .map(kv -> kv.split(":")[1])
            .collect(Collectors.toList());
    }
}
