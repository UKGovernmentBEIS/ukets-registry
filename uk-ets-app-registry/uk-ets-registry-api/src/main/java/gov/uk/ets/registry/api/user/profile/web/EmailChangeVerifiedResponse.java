package gov.uk.ets.registry.api.user.profile.web;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Setter
@Getter
public class EmailChangeVerifiedResponse {
    private Long requestId;
    private Boolean tokenExpired;
}
