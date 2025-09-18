package gov.uk.ets.registry.api.file.upload.adhoc.dto;

import gov.uk.ets.registry.api.file.upload.dto.BaseType;
import gov.uk.ets.registry.api.file.upload.dto.FileHeaderDto;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdhocNotificationFileDto extends FileHeaderDto {

    private Integer tentativeRecipients;

    public AdhocNotificationFileDto(Long id, String fileName, BaseType baseType, ZonedDateTime createdOn, Integer tentativeRecipients) {
        super(id, fileName, baseType, createdOn);
        this.tentativeRecipients = tentativeRecipients;
    }
}
