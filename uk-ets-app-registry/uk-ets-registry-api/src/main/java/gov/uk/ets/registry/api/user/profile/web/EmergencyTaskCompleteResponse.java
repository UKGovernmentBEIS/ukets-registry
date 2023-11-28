package gov.uk.ets.registry.api.user.profile.web;

import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class EmergencyTaskCompleteResponse extends TaskCompleteResponse {
    private final String email;
    private final String loginUrl;

    @Builder(builderMethodName = "emergencyTaskCompleteResponseBuilder")
    public EmergencyTaskCompleteResponse(Long requestIdentifier, String email, String loginUrl) {
        super(requestIdentifier, null);
        this.email = email;
        this.loginUrl = loginUrl;
    }
}
