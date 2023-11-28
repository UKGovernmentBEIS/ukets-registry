package gov.uk.ets.registry.api.user;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserFileDTO implements Serializable {

    private static final long serialVersionUID = -3825788239425317346L;

    private Long id;
    private String name;
    private String type;
    private ZonedDateTime uploadedDate;

    public UserFileDTO(Long id, String name, String type, LocalDateTime uploadedDate) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.uploadedDate = uploadedDate.atZone(ZoneId.of("UTC"));
    }
}
