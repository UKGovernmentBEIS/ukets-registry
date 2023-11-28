package gov.uk.ets.registry.api.authz.miners;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;

/**
 * Retrieves a list of strings from a JSON array.
 */
public interface GenericMiner {

    /**
     * Retrieves a list of strings from a JSON array.
     * @param configuration the configuration
     * @param element the element we want to extract
     * @return
     */
    default List<String> mine(Map<String, String> configuration, String element) {
        String elementValue = configuration.get(element);
        return StringUtils.isEmpty(elementValue)
            ? new ArrayList<>()
            :  List.of(elementValue
                .replace("[", "")
                .replace("\"", "")
                .replace("]", "")
                .split(","))
            .stream()
            .map(String::strip)
            .collect(Collectors.toList());
    }
}
