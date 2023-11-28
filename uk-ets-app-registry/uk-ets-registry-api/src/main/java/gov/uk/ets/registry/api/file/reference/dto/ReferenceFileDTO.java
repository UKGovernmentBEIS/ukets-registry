package gov.uk.ets.registry.api.file.reference.dto;

import gov.uk.ets.registry.api.file.reference.domain.type.ReferenceFileType;
import java.io.Serializable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode(of = "id")
public class ReferenceFileDTO implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = 8566306592766689070L;

    private Long id;
    private String name;
    private ReferenceFileType type;
}
