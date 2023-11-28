package gov.uk.ets.registry.api.file.upload.dto;

import gov.uk.ets.commons.logging.MDCParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

import static gov.uk.ets.commons.logging.RequestParamType.FILE_ID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FileHeaderDto {

    @MDCParam(FILE_ID)
    private Long id;
    private String fileName;
    private BaseType baseType;
    private ZonedDateTime createdOn;
}
