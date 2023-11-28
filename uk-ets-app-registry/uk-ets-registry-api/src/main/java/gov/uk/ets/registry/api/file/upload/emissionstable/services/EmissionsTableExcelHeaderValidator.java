package gov.uk.ets.registry.api.file.upload.emissionstable.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.Row;

import gov.uk.ets.registry.api.file.upload.emissionstable.error.EmissionsTableBusinessRulesException;
import gov.uk.ets.registry.api.file.upload.emissionstable.error.EmissionsTableError;
import gov.uk.ets.registry.api.file.upload.emissionstable.error.EmissionsUploadBusinessError;

public class EmissionsTableExcelHeaderValidator {

	//Contains the submitted excel headers in uppercase
	private List<String> headers;
	
	private static final int ZERO = 0;
	private static final int COLUMN_NOT_EXISTS = -1;
	
	//The column names
	public static final String IDENTIFIER_COLUMN = "Operator ID";
	public static final String YEAR_COLUMN = "Year";
	public static final String EMISSIONS_COLUMN = "Emissions";



	public EmissionsTableExcelHeaderValidator(Row header) {
		headers = header.getCells(ZERO, header.getCellCount()).stream()
		        .filter(Objects::nonNull)
		        .map(Cell::getText)
		        .map(String::toUpperCase)
		        .collect(Collectors.toList());
	}
	
    public void validate() {

    	List<EmissionsUploadBusinessError>  errors = Stream.
    	of(IDENTIFIER_COLUMN,YEAR_COLUMN,EMISSIONS_COLUMN).
    	filter(t -> !headers.contains(t.toUpperCase())).
    	map(this::toEmissionsUploadBusinessError).
    	collect(Collectors.toList());
		
		if(!errors.isEmpty()) {
			EmissionsTableBusinessRulesException exception = new EmissionsTableBusinessRulesException();
			exception.addErrors(errors);
			throw exception;
		}
    }
    
    private EmissionsUploadBusinessError toEmissionsUploadBusinessError(String headerName) {
		return EmissionsUploadBusinessError.builder()
			        .error(EmissionsTableError.ERROR_5003).rowNumber(ZERO)
			        .errorMessage(String.format(
			                EmissionsTableError.ERROR_5003.getMessageTemplate(),
			                headerName)).build();
		
    }
    
    //Returns the zero based column number of the Header.
    public int getColumnNumber(String headerName) {
    	Optional<String> headerNameOptional = Optional.ofNullable(headerName);
    	
    	if(headerNameOptional.isPresent()) {
        	return headers.indexOf(headerNameOptional.get().toUpperCase());
    	}

    	return COLUMN_NOT_EXISTS;
    }

	public int getIdentifierColumnIndex() {
    	return headers.indexOf(IDENTIFIER_COLUMN.toUpperCase());
	}

	public int getYearColumnIndex() {
    	return headers.indexOf(YEAR_COLUMN.toUpperCase());
	}

	public int getEmissionsColumnIndex() {
    	return headers.indexOf(EMISSIONS_COLUMN.toUpperCase());
	}
    
    
}
