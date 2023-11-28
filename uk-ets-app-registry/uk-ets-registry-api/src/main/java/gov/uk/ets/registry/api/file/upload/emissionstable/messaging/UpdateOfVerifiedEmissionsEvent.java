package gov.uk.ets.registry.api.file.upload.emissionstable.messaging;

import java.util.Optional;

import gov.uk.ets.registry.api.compliance.messaging.events.ComplianceEventType;
import gov.uk.ets.registry.api.compliance.messaging.events.outgoing.ComplianceOutgoingEventBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class UpdateOfVerifiedEmissionsEvent extends ComplianceOutgoingEventBase {

	private static final long serialVersionUID = 1L;

	public ComplianceEventType getType() {
        return ComplianceEventType.UPDATE_OF_VERIFIED_EMISSIONS;
    }

    private Integer year;
    private Long verifiedEmissions;

    @Override
    protected boolean doValidate() {
        return Optional.of(year).isPresent();
    }
    
}
