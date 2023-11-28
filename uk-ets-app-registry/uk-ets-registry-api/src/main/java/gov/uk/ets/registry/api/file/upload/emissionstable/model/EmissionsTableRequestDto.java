package gov.uk.ets.registry.api.file.upload.emissionstable.model;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.registry.api.file.upload.dto.FileHeaderDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static gov.uk.ets.commons.logging.RequestParamType.DTO;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmissionsTableRequestDto {

	private String otp;
	@MDCParam(DTO)
	private FileHeaderDto fileHeader;
}
