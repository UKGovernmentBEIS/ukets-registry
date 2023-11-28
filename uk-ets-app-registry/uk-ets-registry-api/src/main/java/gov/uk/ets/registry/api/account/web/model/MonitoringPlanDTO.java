package gov.uk.ets.registry.api.account.web.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * The monitoring plan transfer object.
 */
@Getter
@Setter
public class MonitoringPlanDTO implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = -5429203322112840544L;

    /**
     * The id.
     */
    @NotNull
    @Size(max = 256, message = "Monitoring Plan ID must not exceed 256 characters.")
    private String id;

}
