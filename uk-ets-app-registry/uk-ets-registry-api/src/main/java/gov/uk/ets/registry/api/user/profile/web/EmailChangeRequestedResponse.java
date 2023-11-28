package gov.uk.ets.registry.api.user.profile.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class EmailChangeRequestedResponse {
    private String newEmail;
}
