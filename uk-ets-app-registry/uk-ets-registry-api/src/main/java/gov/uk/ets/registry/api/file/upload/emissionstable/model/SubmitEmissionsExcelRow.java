package gov.uk.ets.registry.api.file.upload.emissionstable.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = {"identifier","year"})
public class SubmitEmissionsExcelRow implements Comparable<SubmitEmissionsExcelRow> {

	private Long identifier;
	private Integer year;
	private String emissions;
	
	
	@Override
	public int compareTo(SubmitEmissionsExcelRow o) {
		int result = Long.compare(identifier, o.identifier);
		if(result == 0) {
			result = Integer.compare(year, o.year);
		}
		return result;
	}

}
