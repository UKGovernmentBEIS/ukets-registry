package gov.uk.ets.registry.api.file.upload.emissionstable.services;

import gov.uk.ets.registry.api.compliance.repository.ExcludeEmissionsRepository;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.stereotype.Service;

import gov.uk.ets.registry.api.account.repository.CompliantEntityRepository;
import gov.uk.ets.registry.api.file.upload.emissionstable.error.EmissionsUploadBusinessError;
import gov.uk.ets.registry.api.file.upload.wrappers.EmissionsTableContentValidationWrapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmissionsTableExcelFileValidationService {


	private final CompliantEntityRepository compliantEntityRepository;
	private final ExcludeEmissionsRepository excludeEmissionsRepository;
	
	
	/**
	 * Method for the validation of the file content.
	 *
	 * @param multiPartInputStream
	 *            the input stream of the uploaded file
	 * @throws IOException
	 *             if I/O interruption occurs
	 */
	public List<EmissionsUploadBusinessError> validateFileContent(InputStream multiPartInputStream) throws IOException {
    	EmissionsTableContentValidationWrapper wrapper = new EmissionsTableContentValidationWrapper(
    		compliantEntityRepository.findAllIdentifiersFetchAccountStatusAndYears(),
			excludeEmissionsRepository.findAll(),
			LocalDate.now().getYear());



		try(ReadableWorkbook wb = new ReadableWorkbook(multiPartInputStream)) {
			Sheet sheet = wb.getFirstSheet();

			// if (sheetList.isEmpty()) {
			// throw EmissionsTableBusinessRulesException
			// .create(EmissionsTableError.INVALID_TABLE_EMPTY);
			// }
			
			wrapper.validate(sheet.read());


			multiPartInputStream.reset();		
		}

		return wrapper.getFileContentExceptions();
	}
	
}
