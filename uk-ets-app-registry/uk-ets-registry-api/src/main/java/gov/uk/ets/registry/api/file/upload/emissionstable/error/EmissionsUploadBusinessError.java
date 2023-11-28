package gov.uk.ets.registry.api.file.upload.emissionstable.error;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode(of = {"rowNumber","columnNumber","error"})
public class EmissionsUploadBusinessError implements Comparable<EmissionsUploadBusinessError> {

	private EmissionsTableError error;
	private int rowNumber;
	private int columnNumber;
	private String operatorId;
	private String errorMessage;
	
	
	@Override
	public int compareTo(EmissionsUploadBusinessError o) {
		int result = Integer.compare(rowNumber, o.rowNumber);
		
		if(result == 0) {
			result = Integer.compare(columnNumber, o.columnNumber);
			
			if(result == 0) {
				result = error.compareTo(o.error);
			}
		}
		
		return result;
	}
}
