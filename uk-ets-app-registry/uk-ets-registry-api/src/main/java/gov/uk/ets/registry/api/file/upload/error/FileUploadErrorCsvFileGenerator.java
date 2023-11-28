package gov.uk.ets.registry.api.file.upload.error;

import gov.uk.ets.registry.api.file.upload.allocationtable.error.AllocationTableUploadBusinessError;
import gov.uk.ets.registry.api.file.upload.emissionstable.error.EmissionsUploadBusinessError;
import java.util.List;
import lombok.Builder;
import org.apache.commons.collections4.ListUtils;

/**
 * This class knows how to write a csv file with the errors found during
 * Excel file upload.
 * 
 * @author P35036
 *
 */
@Builder
public class FileUploadErrorCsvFileGenerator {

	private final List<EmissionsUploadBusinessError> emissionsFileContentErrors;
	private final List<AllocationTableUploadBusinessError> allocationFileContentErrors;

	/**
	 * Errors identified in the content of the Excel file will be included in a
	 * .csv file which the user will be able to download. The .csv file will
	 * consist of the following columns:
	 * <ol>
	 * <li>Row number</li>
	 * <li>Operator ID</li>
	 * <li>BR code</li>
	 * <li>Error message</li>
	 * </ol>
	 */
	public String generateCSVErrorFileContent() {
		// Prepare the verified emissions errors.csv
		StringBuilder sb = new StringBuilder();
		writeHeader(sb);
		writeErrors(sb);
		return sb.toString();
	}

	private void writeErrors(StringBuilder sb) {
		for(EmissionsUploadBusinessError error: ListUtils.emptyIfNull(emissionsFileContentErrors)) {
			sb.append(System.getProperty("line.separator"));
			sb.append(String.join(",",
			                      Integer.toString(error.getRowNumber()), error.getOperatorId(),
			                      Integer.toString(error.getError().getCode()), error.getErrorMessage()));
		}
		for(AllocationTableUploadBusinessError error: ListUtils.emptyIfNull(allocationFileContentErrors)) {
			sb.append(System.getProperty("line.separator"));
			sb.append(String.join(",",
			                      Integer.toString(error.getRowNumber()), error.getOperatorId(),
			                      Integer.toString(error.getError().getCode()), error.getErrorMessage()));
		}
	}

	private void writeHeader(StringBuilder sb) {
		sb.append(String.join(",", "Row number", "Operator ID", "BR code", "Error message"));
	}

	/**
	 * The name of the file will be formed by combining the 'Error_' with the
	 * file name of the uploaded emissions' table file.
	 * @param originalFilename the original file name
	 * @return the concatenated string
	 */
	public String generateCSVErrorFileName(String originalFilename) {
		return ("Error_" + originalFilename).replace("xlsx", "csv");
	}
}
