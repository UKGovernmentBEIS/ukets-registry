package gov.uk.ets.registry.api.integration.service.regulatornotice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode
@Builder
@AllArgsConstructor
public class RegulatorNoticeResult {

    private String registryId;
    @Builder.Default
    private List<IntegrationEventErrorDetails> errors = new ArrayList<>();

    public RegulatorNoticeResult(String registryId) {
        this.registryId = registryId;
        this.errors = new ArrayList<>();
    }

    public RegulatorNoticeResult(List<IntegrationEventErrorDetails> errors) {
        this.errors = errors;
    }
}
