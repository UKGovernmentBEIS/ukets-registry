package gov.uk.ets.registry.api.file.upload.allocationtable.error;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode(of = {"rowNumber","error"})
public class AllocationTableUploadBusinessError implements Comparable<AllocationTableUploadBusinessError> {

	private AllocationTableError error;
	private int rowNumber;
	private String operatorId;
	private String errorMessage;
	
	@Override
	public int compareTo(AllocationTableUploadBusinessError o) {
		int result = Integer.compare(rowNumber, o.rowNumber);
		
		if(result == 0) {
			result = error.compareTo(o.error);
		}
		return result;
	}
}
