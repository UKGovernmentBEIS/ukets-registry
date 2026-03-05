package gov.uk.ets.registry.api.integration.service.regulatornotice;

import gov.uk.ets.registry.api.common.ConversionService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.file.upload.types.FileStatus;
import gov.uk.ets.registry.api.integration.consumer.OperationEvent;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import gov.uk.ets.registry.api.integration.service.RegistryIntegrationHeadersUtil;
import gov.uk.ets.registry.api.regulatornotice.domain.RegulatorNotice;
import gov.uk.ets.registry.api.regulatornotice.service.RegulatorNoticeService;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.regulatornotice.RegulatorNoticeEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class RegulatorNoticeEventService {

    private static final String EVENT_ACTION = "Regulator notice via IP";
    private static final String EVENT_COMMENT_FORMAT = "%s regulator notice via IP";

    private final EventService eventService;
    private final RegulatorNoticeEventValidator eventValidator;
    private final RegistryIntegrationHeadersUtil util;
    private final RegulatorNoticeService regulatorNoticeService;
    private final UploadedFilesRepository uploadedFilesRepository;
    private final ConversionService conversionService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public RegulatorNoticeResult process(RegulatorNoticeEvent event, Map<String, Object> headers) {

        log.info("Received event {} with value {} and headers {}",
                OperationEvent.REGULATOR_NOTICE, event, headers);

        String correlationId = util.getCorrelationId(headers);
        List<IntegrationEventErrorDetails> errors = eventValidator.validate(event);
        if (errors.isEmpty()) {
            process(event, util.getSourceSystem(headers));
            log.info("Event {} with correlationId: {} from {} and value {} was processed successfully.",
                    OperationEvent.REGULATOR_NOTICE, correlationId, util.getSourceSystem(headers), event);
            log.info("""
                            Operator ID: {},
                            Notice Type: {},
                            File: {}
                            """,
                    event.getRegistryId(),
                    event.getType(),
                    event.getFileName());
            return new RegulatorNoticeResult(event.getRegistryId());
        } else {
            log.warn(
                    "Event {} with correlationId: {} from {} and value {} has to following errors {} and was not processed successfully.",
                    OperationEvent.REGULATOR_NOTICE, correlationId, util.getSourceSystem(headers), event,
                    errors);
        }

        return new RegulatorNoticeResult(errors);
    }

    private void process(RegulatorNoticeEvent event, SourceSystem sourceSystem) {
        final Long registryId = Long.valueOf(event.getRegistryId());
        final Long regulatorNoticeIdentifier =
                regulatorNoticeService.createRegulatorNotice(registryId, event.getType());
        if (event.getFileData() != null && event.getFileData().length > 0) {
            RegulatorNotice regulatorNotice = regulatorNoticeService.findByIdentifier(regulatorNoticeIdentifier);
            saveRegulatorNoticeFile(event.getFileData(), event.getFileName(), regulatorNotice);
        }
        eventService.createAndPublishEvent(event.getRegistryId(), null,
                String.format(EVENT_COMMENT_FORMAT, sourceSystem), EventType.REGULATOR_NOTICE, EVENT_ACTION);
    }

    private void saveRegulatorNoticeFile(byte[] fileData, String fileName,
                                         Task task) {
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFileData(fileData);
        uploadedFile.setFileName(fileName);
        uploadedFile.setFileStatus(FileStatus.SUBMITTED);
        uploadedFile.setCreationDate(LocalDateTime.now());
        uploadedFile.setTask(task);
        uploadedFile.setFileSize(conversionService.convertByteAmountToHumanReadable(fileData.length));
        uploadedFilesRepository.save(uploadedFile);
    }
}
