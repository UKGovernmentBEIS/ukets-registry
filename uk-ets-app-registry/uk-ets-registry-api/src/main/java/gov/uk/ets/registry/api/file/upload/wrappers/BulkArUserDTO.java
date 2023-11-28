package gov.uk.ets.registry.api.file.upload.wrappers;

import gov.uk.ets.registry.api.user.domain.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BulkArUserDTO {

    private String urid;
    private UserStatus userStatus;
}
