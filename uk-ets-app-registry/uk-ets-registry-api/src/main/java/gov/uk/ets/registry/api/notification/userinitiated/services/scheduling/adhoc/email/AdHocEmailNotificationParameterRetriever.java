package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.adhoc.email;

import gov.uk.ets.registry.api.file.upload.error.FileUploadException;
import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.NotificationParameterHolder;
import gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.NotificationParameterRetriever;
import gov.uk.ets.registry.api.notification.userinitiated.util.IdentifierGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static gov.uk.ets.registry.api.file.upload.emissionstable.services.EmissionsTableUploadProcessor.ERROR_PROCESSING_FILE;

@Service
@RequiredArgsConstructor
@Log4j2
public class AdHocEmailNotificationParameterRetriever implements NotificationParameterRetriever {

    /**
     * For each recipient of the notification, creates a {@link NotificationParameterHolder} by retrieving
     * appropriate parameter data and recipient email.
     */
    public List<NotificationParameterHolder> getNotificationParameters(Notification notification) {
        try (InputStream multiPartInputStream = new ByteArrayInputStream(notification.getUploadedFile().getFileData())) {
            ReadableWorkbook wb = new ReadableWorkbook(multiPartInputStream);
            Sheet sheet = wb.getFirstSheet();
            List<Row> sheetList = sheet.read();
            List<Cell> headers = sheetList.get(0).getCells(0, sheetList.get(0).getCellCount());
            return sheetList.stream()
                .skip(1)
                .map(row -> IntStream.range(0, headers.size())
                    .boxed()
                    .collect(Collectors.toMap(
                        i -> headers.get(i).getText(),               // header names
                        index -> (Object) row.getCellText(index),    // corresponding cell values
                        (v1, v2) -> v1,                              // merge function (in case of key collision)
                        LinkedHashMap::new                           // preserve insertion order
                    )))
                .map(headerValues -> NotificationParameterHolder.builder()
                    .notificationId(notification.getId())
                    .notificationInstanceId(IdentifierGenerator.generate(notification))
                    .csvRowData(headerValues)
                    .build())
                .collect(Collectors.toList());
        } catch (Exception exception) {
            throw new FileUploadException(ERROR_PROCESSING_FILE, exception);
        }
    }

    @Override
    public NotificationParameterHolder getDefaultParameterHolder(Notification notification) {
        return getNotificationParameters(notification)
            .stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Notification parameter not found for AdHocEmail"));
    }
}

