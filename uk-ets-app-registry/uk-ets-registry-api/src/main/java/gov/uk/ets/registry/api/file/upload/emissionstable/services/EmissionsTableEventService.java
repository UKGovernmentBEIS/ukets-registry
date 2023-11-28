package gov.uk.ets.registry.api.file.upload.emissionstable.services;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.emissionstable.model.EmissionsEntry;
import gov.uk.ets.registry.api.file.upload.emissionstable.repository.EmissionsEntryRepository;
import gov.uk.ets.registry.api.file.upload.error.FileUploadException;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.user.service.UserService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.stereotype.Service;

/**
 * This service handles all events related to emissions upload request & approval.
 * @author P35036
 *
 */
@Service
@RequiredArgsConstructor
public class EmissionsTableEventService {

    private final AccountRepository accountRepository;
    private final EventService eventService;
    private final UserService userService;
    private final EmissionsEntryRepository emissionsEntryRepository;

	/**
	 * Generate an event each time a file is uploaded or approved. 
	 * @param requestId
	 * @param comment
	 * @param eventType
	 * @param action
	 */
	public void createAndPublishEvent(String requestId, String comment,EventType eventType, String action) {
		eventService.createAndPublishEvent(requestId,userService.getCurrentUser().getUrid(), comment, eventType,action);
	}

	/**
	 * An event is generated in the event history of each account.
	 * 
	 * @param uploadedFile
	 * @param description
	 * @param eventType
	 * @param what
	 */
	public void createAndPublishEmissionsUploadedAccountEvents( byte[] uploadedFile, String description, EventType eventType,String what , Set<Long> unprocessedCompliantEntityIdentifers) {
		
		try (InputStream multiPartInputStream = new ByteArrayInputStream(uploadedFile);ReadableWorkbook wb = new ReadableWorkbook(multiPartInputStream)) {
			Sheet sheet = wb.getFirstSheet();
			// Iterate over the rows
			sheet.read().stream().skip(1)// Skip the Header
			        .filter(r -> r.getFirstNonEmptyCell().isPresent())
			        .filter(r -> !unprocessedCompliantEntityIdentifers.contains(Long.valueOf(r.getCellText(0))))
			        .forEach(r -> {
				        Optional<Account> accountOptional = accountRepository.findByCompliantEntityIdentifier(Long.valueOf(r.getCellText(0)));
				        if (accountOptional.isPresent()) {
					        eventService.createAndPublishEvent(accountOptional.get().getIdentifier().toString(),userService.getCurrentUser().getUrid(), description,eventType, what);
				        }
			        });
		} catch (IOException e) {
			throw new FileUploadException(EmissionsTableUploadProcessor.ERROR_PROCESSING_FILE, e);
		}

	}
	
	/**
	 * An event is generated in the event history of each Compliant Entity.
	 * 
	 * @param uploadedFile
	 * @param description
	 * @param eventType
	 * @param what
	 */
	public void createAndPublishEmissionsUploadedCompliantEntityEvents( byte[] uploadedFile, String description, EventType eventType,String what ,Set<Long> unprocessedCompliantEntityIdentifers) {
		
		try (InputStream multiPartInputStream = new ByteArrayInputStream(uploadedFile);ReadableWorkbook wb = new ReadableWorkbook(multiPartInputStream)) {
			Sheet sheet = wb.getFirstSheet();
			// Iterate over the rows
			sheet.read().stream().skip(1)// Skip the Header
			        .filter(r -> r.getFirstNonEmptyCell().isPresent())
	                .filter(r -> !unprocessedCompliantEntityIdentifers.contains(Long.valueOf(r.getCellText(0))))
			        .forEach(r -> {
				        eventService.createAndPublishEvent(r.getCellText(0),null, description,eventType, what);
			        });
		} catch (IOException e) {
			throw new FileUploadException(EmissionsTableUploadProcessor.ERROR_PROCESSING_FILE, e);
		}

	}

	public void createAndPublishEmissionsCompliantEntityEventsWithCurrentAndUpdatedValues(byte[] uploadedFile, String what,Set<Long> unprocessedCompliantEntityIdentifers) {

        try (InputStream multiPartInputStream = new ByteArrayInputStream(uploadedFile);ReadableWorkbook wb = new ReadableWorkbook(multiPartInputStream)) {
            Sheet sheet = wb.getFirstSheet();
            List<Row> rows = sheet.read();
            EmissionsTableExcelHeaderValidator headerValidator = new EmissionsTableExcelHeaderValidator(rows.get(0));
            // Iterate over the rows
            sheet.read().stream()
                    .skip(1)// Skip the Header
                    .filter(row -> row.getFirstNonEmptyCell().isPresent())
                    //This is due to JIRA UKETS-6607
                    .filter(r -> !unprocessedCompliantEntityIdentifers.contains(Long.valueOf(r.getCellText(0))))
                    .forEach(row -> {
                        //This finder also reads uncommited entries so we need to exclude the 
                        //current entry hence the limit 2 & skip 1 in order to get the previous entry
                        String oldValue = emissionsEntryRepository
                            .findAllByCompliantEntityIdAndYear(Long.valueOf(row.getCellText(0)),
                                    Long.valueOf(row.getCellText(headerValidator.getYearColumnIndex())))
                            .stream()
                            .sorted(Comparator.comparing(EmissionsEntry::getUploadDate).reversed())
                            .limit(2)
                            .skip(1)
                            .findFirst()
                            .filter(t -> Optional.ofNullable(t.getEmissions()).isPresent())
                            .map(t -> t.getEmissions().toString())
                            .orElse("No emissions");
                        String updatedValue = !row.getCellText(headerValidator.getEmissionsColumnIndex()).isEmpty() ?
                                row.getCellText(headerValidator.getEmissionsColumnIndex()) :  "No emissions";
                        String description = "Reporting year: " + row.getCellText(headerValidator.getYearColumnIndex())
                                + ", Old value: " + oldValue + ", Updated value: " + updatedValue;
                        String urid = userService.getCurrentUser().getUrid();
                        eventService.createAndPublishEvent(row.getCellText(0), null,
                                description, EventType.APPROVE_COMPLIANT_ENTITY_EMISSIONS, what);
                        Optional<Account> accountOptional = accountRepository.findByCompliantEntityIdentifier(Long.valueOf(row.getCellText(0)));
                        accountOptional.ifPresent(account -> 
                            eventService.createAndPublishEvent(account.getIdentifier().toString(), urid,description, EventType.APPROVE_ACCOUNT_EMISSIONS, what));
                    });
        } catch (IOException e) {
            throw new FileUploadException(EmissionsTableUploadProcessor.ERROR_PROCESSING_FILE, e);
        }

    }
}
