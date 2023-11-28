package gov.uk.ets.registry.api.account.web.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

/**
 * Represents the installation type ahead search result object.
 */
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class InstallationSearchResultDTO implements Serializable {

    /**
     * Serialisation version.
     */

    private static final long serialVersionUID = 7233242966303624015L;

    /**
     * The identifier.
     */
    Long identifier;

    /**
     * The permit identifier.
     */
    String permitIdentifier;

    /**
     * The installation name.
     */
    String installationName;

}
