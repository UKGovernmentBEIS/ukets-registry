package gov.uk.ets.registry.api.authz.miners;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class MinedResource {
    @EqualsAndHashCode.Include
    private String name;
    private Set<String> urls = new HashSet<>();
    private List<MinedScope> scopes;

    /**
     * Typically, we are interested mostly on the url of a resource if any.
     * @return the url if name or name
     */
    public Set<String> urlOrName() {
        return urls.isEmpty() ? Set.of(name) : urls;
    }
}
