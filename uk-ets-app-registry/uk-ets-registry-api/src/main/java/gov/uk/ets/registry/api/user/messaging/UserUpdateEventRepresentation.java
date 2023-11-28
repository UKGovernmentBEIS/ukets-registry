package gov.uk.ets.registry.api.user.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class UserUpdateEventRepresentation {
    private String id;
    //more fields can be added
}
