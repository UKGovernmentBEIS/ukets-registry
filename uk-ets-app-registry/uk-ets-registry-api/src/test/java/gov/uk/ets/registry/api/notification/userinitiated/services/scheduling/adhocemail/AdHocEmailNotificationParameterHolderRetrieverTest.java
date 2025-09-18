package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.adhocemail;

import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationDefinition;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.NotificationParameterHolder;
import gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.adhoc.email.AdHocEmailNotificationParameterRetriever;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class AdHocEmailNotificationParameterHolderRetrieverTest {

    @Mock
    private Notification notification;

    @Mock
    private NotificationDefinition notificationDefinition;

    @Mock
    private UploadedFile uploadedFile;

    @InjectMocks
    private AdHocEmailNotificationParameterRetriever retriever;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetNotificationParameters() throws Exception {
        // Mocking file data (CSV-like content in an Excel sheet)
        byte[] fileData = createMockExcelFile();
        when(notification.getUploadedFile()).thenReturn(uploadedFile);
        when(uploadedFile.getFileData()).thenReturn(fileData);
        when(notification.getId()).thenReturn(1L);
        when(notification.getDefinition()).thenReturn(notificationDefinition);
        when(notificationDefinition.getTypeId()).thenReturn(123);

        // Act
        List<NotificationParameterHolder> result = retriever.getNotificationParameters(notification);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1L, result.get(0).getNotificationId());
        assertNotNull(result.get(0).getCsvRowData());
        assertEquals("Value1", result.get(0).getCsvRowData().get("Header1"));
    }

    private byte[] createMockExcelFile() throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Header1");
            headerRow.createCell(1).setCellValue("Header2");

            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue("Value1");
            dataRow.createCell(1).setCellValue("Value2");

            workbook.write(baos);
            return baos.toByteArray();
        }
    }
}

