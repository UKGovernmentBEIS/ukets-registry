package gov.uk.ets.registry.api.tal.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrustedAccountTaskDifference {
    @Singular
    private List<Long> ids;
}
