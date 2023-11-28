package gov.uk.ets.publication.api.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FileInfoDto {

    private Long id;
    private String fileName;
    private Integer year;
    private BaseType baseType;
    private Long sectionId;
}
