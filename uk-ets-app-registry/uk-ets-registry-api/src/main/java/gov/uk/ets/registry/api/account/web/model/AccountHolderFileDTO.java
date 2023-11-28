package gov.uk.ets.registry.api.account.web.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountHolderFileDTO implements Serializable {

    private static final long serialVersionUID = -3825788239525317346L;

    private Long id;
    private String name;
    private String type;
    private ZonedDateTime uploadedDate;

    public AccountHolderFileDTO(Long id, String name, String type, LocalDateTime uploadedDate) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.uploadedDate = uploadedDate.atZone(ZoneId.of("UTC"));
    }
}
