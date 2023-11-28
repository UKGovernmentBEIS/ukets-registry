package gov.uk.ets.registry.api.file.upload.wrappers;

import gov.uk.ets.registry.api.compliance.domain.ExcludeEmissionsEntry;
import gov.uk.ets.registry.api.file.upload.emissionstable.error.EmissionsTableError;
import gov.uk.ets.registry.api.file.upload.emissionstable.error.EmissionsUploadBusinessError;
import gov.uk.ets.registry.api.file.upload.emissionstable.model.SubmitEmissionsExcelRow;
import gov.uk.ets.registry.api.file.upload.emissionstable.model.SubmitEmissionsValidityInfo;
import gov.uk.ets.registry.api.file.upload.emissionstable.services.EmissionsTableExcelHeaderValidator;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.Phase;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import org.apache.commons.lang3.math.NumberUtils;
import org.dhatim.fastexcel.reader.Row;
@Getter
public class EmissionsTableContentValidationWrapper {

	// Key: Compliant Entity Identifier , Value : SubmitEmissionsValidityChecker
	private Map<Long, SubmitEmissionsValidityInfo> existingCompliantEntities;
	private List<ExcludeEmissionsEntry> excludeEmissionsEntries;
	private List<SubmitEmissionsExcelRow> emissionsExcelRows;
	private List<EmissionsUploadBusinessError> fileContentExceptions;
	private int currentYear;

	private int identifierColumnIndex = 0;
	private int yearColumnIndex = 2;
	private int emissionsColumnIndex = 3;

	public EmissionsTableContentValidationWrapper(
	        List<SubmitEmissionsValidityInfo> existingCompliantEntities,
			List<ExcludeEmissionsEntry> excludeEmissionsEntries,
	        int currentYear) {
		this.existingCompliantEntities = existingCompliantEntities.stream()
		        .collect(Collectors.toMap(
		                SubmitEmissionsValidityInfo::getIdentifier, t -> t));
		this.excludeEmissionsEntries = excludeEmissionsEntries;
		this.currentYear = currentYear;
		emissionsExcelRows = new ArrayList<>();
		fileContentExceptions = new ArrayList<>();
	}

	public void validate(List<Row> rows) {
		EmissionsTableExcelHeaderValidator headerValidator = new EmissionsTableExcelHeaderValidator(
		        rows.get(0));
		headerValidator.validate();
		this.identifierColumnIndex = headerValidator.getIdentifierColumnIndex();
		this.yearColumnIndex = headerValidator.getYearColumnIndex();
		this.emissionsColumnIndex = headerValidator.getEmissionsColumnIndex();

		// Iterate over the rows
		rows.stream().skip(1).// Skip the Header
		        filter(r -> r.getFirstNonEmptyCell().isPresent())
		        .forEach(this::validateRow);
	}

	/**
	 * Executes all required validations on an excel row uploaded by the user.
	 * 
	 * @param row
	 */
	protected void validateRow(Row row) {
		int rowNum = row.getRowNum();
		Optional<Long> compliantEntityIdOptional = getCompliantEntityId(rowNum,
		        row.getCellText(identifierColumnIndex));
		Optional<Integer> yearOptional = getEmissionYear(rowNum,
		        row.getCellText(yearColumnIndex),compliantEntityIdOptional);
		String emissions = row.getCellText(emissionsColumnIndex);

		if (compliantEntityIdOptional.isPresent() && yearOptional.isPresent()
		        && isDuplicate(rowNum,
		                new SubmitEmissionsExcelRow(
		                        compliantEntityIdOptional.get(),
		                        yearOptional.get(), emissions))) {
			return;// Do not add errors for duplicate column
		}

		if (compliantEntityIdOptional.isPresent()
		        && !compliantEntityExistsInRegistry(rowNum,
		                compliantEntityIdOptional.get())) {
			// In case the identifier does not exist , do not proceed to further
			// validations.
			return;
		}

		if (compliantEntityIdOptional.isPresent()) {
			validateCompliantEntityIdentifier(rowNum,
			        compliantEntityIdOptional.get());
		}

		if (compliantEntityIdOptional.isPresent() && yearOptional.isPresent()) {
			validateEmissionYear(rowNum, compliantEntityIdOptional.get(),
			        yearOptional.get());
		}

		validateEmissionQuantity(rowNum, emissions,
		        row.getCellText(yearColumnIndex),compliantEntityIdOptional.map(t -> t.toString()).orElse(""));

	}

	private boolean compliantEntityExistsInRegistry(int rowNumber,
	        Long compliantEntityId) {

		if (!existingCompliantEntities.containsKey(compliantEntityId)) {
			fileContentExceptions.add(EmissionsUploadBusinessError.builder()
			        .error(EmissionsTableError.ERROR_5005).rowNumber(rowNumber)
			        .columnNumber(identifierColumnIndex)
			        .operatorId(compliantEntityId.toString())
			        .errorMessage(
			                EmissionsTableError.ERROR_5005.getMessageTemplate())
			        .build());
			return false;
		}
		return true;
	}

	// Validates that CompliantEntityIdentifier exists and can be parsed
	private Optional<Long> getCompliantEntityId(int rowNumber,
	        String identifier) {
		if (Optional.ofNullable(identifier).isEmpty() || identifier.isBlank()
		        || !NumberUtils.isParsable(identifier)) {
			fileContentExceptions.add(EmissionsUploadBusinessError.builder()
			        .error(EmissionsTableError.ERROR_5014).rowNumber(rowNumber)
			        .columnNumber(identifierColumnIndex).operatorId(identifier)
			        .errorMessage(String.format(
			                EmissionsTableError.ERROR_5014.getMessageTemplate(),
			                "Operator ID"))
			        .build());
			return Optional.empty();
		} else {
			return Optional.of(Long.valueOf(identifier));
		}

	}

	private boolean isDuplicate(int rowNum,
	        SubmitEmissionsExcelRow emissionsExcelRow) {

		if (emissionsExcelRows.contains(emissionsExcelRow)) {
			fileContentExceptions.add(EmissionsUploadBusinessError.builder()
			        .error(EmissionsTableError.ERROR_5015).rowNumber(rowNum)
			        .columnNumber(identifierColumnIndex)
			        .operatorId(emissionsExcelRow.getIdentifier().toString())
			        .errorMessage(
			                EmissionsTableError.ERROR_5015.getMessageTemplate())
			        .build());
			return true;
		} else {
			emissionsExcelRows.add(emissionsExcelRow);
			return false;
		}
	}

	private void validateCompliantEntityIdentifier(int rowNumber,
	        Long compliantEntityId) {

		Optional<AccountStatus> accountStatusOptional = Optional
		        .ofNullable(existingCompliantEntities.get(compliantEntityId)
		                .getAccountStatus());

		if (accountStatusOptional.isEmpty()) {
			fileContentExceptions.add(EmissionsUploadBusinessError.builder()
			        .error(EmissionsTableError.ERROR_5006).rowNumber(rowNumber)
			        .columnNumber(identifierColumnIndex)
			        .operatorId(compliantEntityId.toString())
			        .errorMessage(
			                EmissionsTableError.ERROR_5006.getMessageTemplate())
			        .build());
		} else if (EnumSet
		        .of(AccountStatus.TRANSFER_PENDING, AccountStatus.CLOSURE_PENDING, AccountStatus.CLOSED)
		        .contains(accountStatusOptional.get())) {
			fileContentExceptions.add(EmissionsUploadBusinessError.builder()
			        .error(EmissionsTableError.ERROR_5007).rowNumber(rowNumber)
			        .columnNumber(identifierColumnIndex)
			        .operatorId(compliantEntityId.toString())
			        .errorMessage(
			                EmissionsTableError.ERROR_5007.getMessageTemplate())
			        .build());
		}
	}

	
    private Optional<Integer> getEmissionYear(int rowNum , String year , Optional<Long> compliantEntityIdOptional) {
	 if (Optional.ofNullable(year).isEmpty() ||  year.isBlank()) {
		 fileContentExceptions
		 .add(EmissionsUploadBusinessError.
				 builder().
				 error(EmissionsTableError.ERROR_5014).
				 rowNumber(rowNum).
				 columnNumber(yearColumnIndex).
				 operatorId(compliantEntityIdOptional.isPresent() ? compliantEntityIdOptional.get().toString() : "").
				 errorMessage(String.format(EmissionsTableError.ERROR_5014.getMessageTemplate(),"Year")).
				 build());
		 return Optional.empty();
	 }
	
	 if (!NumberUtils.isParsable(year)) {
		 fileContentExceptions
		 .add(EmissionsUploadBusinessError.
				 builder().
				 error(EmissionsTableError.ERROR_5009).
				 rowNumber(rowNum).
				 columnNumber(yearColumnIndex).
				 operatorId(compliantEntityIdOptional.isPresent() ? compliantEntityIdOptional.get().toString() : "").
				 errorMessage(String.format(EmissionsTableError.ERROR_5009.getMessageTemplate())).
				 build());
		 return Optional.empty();
	 }
	 
	 return Optional.of( Integer.valueOf(year));
	 
}
	
	private void validateEmissionYear(int rowNum, Long identifier,
	        Integer emissionsYear) {

		if (!Phase.PHASE_1.isYearWithinPhase(emissionsYear)) {
			fileContentExceptions.add(EmissionsUploadBusinessError.builder()
			        .error(EmissionsTableError.ERROR_5010).rowNumber(rowNum)
			        .columnNumber(yearColumnIndex)
			        .operatorId(identifier.toString())
			        .errorMessage(
			                EmissionsTableError.ERROR_5010.getMessageTemplate())
			        .build());
			return;
		}

		if (emissionsYear > this.currentYear) {
			fileContentExceptions.add(EmissionsUploadBusinessError.builder()
			        .error(EmissionsTableError.ERROR_5011).rowNumber(rowNum)
			        .columnNumber(yearColumnIndex)
			        .operatorId(identifier.toString())
			        .errorMessage(
			                EmissionsTableError.ERROR_5011.getMessageTemplate())
			        .build());
			return;
		}

		if (existingCompliantEntities.containsKey(identifier)) {
			Optional<Integer> lastYearVerifiedEmissionsOptional = Optional
			        .ofNullable(existingCompliantEntities.get(identifier)
			                .getEndYear());
			int firstYearVerifiedEmissions = existingCompliantEntities
			        .get(identifier).getStartYear();

			if (emissionsYear == this.currentYear
			        && !isLastEmissionYearEqualCurrentYear(
			                lastYearVerifiedEmissionsOptional)) {
				fileContentExceptions.add(EmissionsUploadBusinessError.builder()
				        .error(EmissionsTableError.ERROR_5012).rowNumber(rowNum)
				        .columnNumber(yearColumnIndex)
				        .operatorId(identifier.toString())
				        .errorMessage(EmissionsTableError.ERROR_5012
				                .getMessageTemplate())
				        .build());
				return;
			}

			if (emissionsYear < firstYearVerifiedEmissions
			        || lastYearVerifiedEmissionsOptional.isPresent()
			                && emissionsYear > lastYearVerifiedEmissionsOptional
			                        .get()) {
				fileContentExceptions.add(EmissionsUploadBusinessError.builder()
				        .error(EmissionsTableError.ERROR_5013).rowNumber(rowNum)
				        .columnNumber(yearColumnIndex)
				        .operatorId(identifier.toString())
				        .errorMessage(EmissionsTableError.ERROR_5013
				                .getMessageTemplate())
				        .build());
			}

			excludeEmissionsEntries.stream()
				.filter(e -> identifier.equals(e.getCompliantEntityId())
					&& emissionsYear.equals(e.getYear().intValue())
					&& e.isExcluded())
				.findFirst()
				.ifPresent(e -> fileContentExceptions.add(EmissionsUploadBusinessError.builder()
					.error(EmissionsTableError.ERROR_5016).rowNumber(rowNum)
					.columnNumber(yearColumnIndex)
					.operatorId(identifier.toString())
					.errorMessage(EmissionsTableError.ERROR_5016
						.getMessageTemplate())
					.build()));
		}

	}

	private boolean isLastEmissionYearEqualCurrentYear(
	        Optional<Integer> lastYearVerifiedEmissionsOptional) {
		if (lastYearVerifiedEmissionsOptional.isPresent()) {
			return lastYearVerifiedEmissionsOptional.get() == this.currentYear;
		}
		return false;
	}

	private void validateEmissionQuantity(int rowNum, String emissionQuantity,
	        String year,String compliantEntityId) {

		final int ZERO = 0;

		if (Optional.ofNullable(emissionQuantity).isEmpty()
		        || emissionQuantity.trim().isEmpty()) {
			return;
		}

		// this check covers the case that the quantity is a decimal number (or
		// any non-valid long)
		long quantity = NumberUtils.toLong(emissionQuantity, -1);

		if (quantity < ZERO) {
			fileContentExceptions.add(EmissionsUploadBusinessError.builder()
			        .error(EmissionsTableError.ERROR_5008).rowNumber(rowNum)
			        .operatorId(compliantEntityId)
			        .columnNumber(emissionsColumnIndex)
			        .errorMessage(String.format(
			                EmissionsTableError.ERROR_5008.getMessageTemplate(),
			                year))
			        .build());
		}
	}
}
