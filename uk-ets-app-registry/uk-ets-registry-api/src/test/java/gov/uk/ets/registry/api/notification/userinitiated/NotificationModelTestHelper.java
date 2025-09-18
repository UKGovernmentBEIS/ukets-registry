package gov.uk.ets.registry.api.notification.userinitiated;

import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.file.upload.types.FileStatus;
import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationDefinition;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationSchedule;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationStatus;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationDefinitionRepository;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class NotificationModelTestHelper {

    public static final String TEST_URID = "UK1234567890";

    public static final String INACTIVE_USER_TEST_URID = "UK3456789012";
    private static final String OHA_LABEL = "ETS - Operator holding account";
    public static final String INACTIVE_USER_TEMPLATE = "Hello Mr ${user.firstName} ${user.lastName}\n\nYour user ID, is: ${user.urid}\n";

    static final String TEST_PARAMETER = "test-parameter";
    private final NotificationDefinitionRepository definitionRepository;
    private final NotificationRepository notificationRepository;
    private final UploadedFilesRepository uploadedFilesRepository;
    
    NotificationDefinition adHocDefinition;
    NotificationDefinition adHocEmailDefinition;
    NotificationDefinition inactiveUserDefinition;
    Notification complianceNotification;
    Notification adHocNotification;
    Notification adHocEmailNotification;
    Notification inactiveUserNotification;
    

    public NotificationModelTestHelper(NotificationDefinitionRepository definitionRepository,
                                       NotificationRepository notificationRepository,
                                       UploadedFilesRepository uploadedFilesRepository) {
        this.definitionRepository = definitionRepository;
        this.notificationRepository = notificationRepository;
        this.uploadedFilesRepository = uploadedFilesRepository;
    }


    public void setUp() {
        adHocDefinition = definitionRepository.findByType(NotificationType.AD_HOC).orElse(null);
        inactiveUserDefinition = definitionRepository.findByType(NotificationType.USER_INACTIVITY).orElse(null);
        
        NotificationDefinition definition2 =
            definitionRepository.findByType(NotificationType.EMISSIONS_MISSING_FOR_OHA)
                .orElse(null);

        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        complianceNotification = Notification.builder()
            .definition(definition2)
            .status(NotificationStatus.ACTIVE)
            .schedule(NotificationSchedule.builder()
                .startDateTime(now.minusDays(1))
                .endDateTime(now.plusDays(1))
                .runEveryXDays(1)
                .build())
            .creator(TEST_URID)
            .build();
        notificationRepository.save(complianceNotification);

        adHocNotification = Notification.builder()
            .definition(adHocDefinition)
            .status(NotificationStatus.ACTIVE)
            .schedule(NotificationSchedule.builder()
                .startDateTime(LocalDateTime.of(2021, 10, 1, 10, 0))
                .endDateTime(LocalDateTime.of(2021, 11, 16, 10, 0))
                .build())
            .creator(TEST_URID)
            .build();
        notificationRepository.save(adHocNotification);

        createAndSaveAdHocEmailNotification();

        notificationRepository.save(Notification.builder()
            .definition(adHocDefinition)
            .schedule(NotificationSchedule.builder()
                .startDateTime(LocalDateTime.of(2021, 9, 13, 10, 0))
                .endDateTime(LocalDateTime.of(2021, 10, 13, 10, 0))
                .build())
            .status(NotificationStatus.EXPIRED)
            .creator(TEST_URID)
            .build());

        notificationRepository.save(Notification.builder()
            .definition(adHocDefinition)
            .schedule(NotificationSchedule.builder()
                .startDateTime(LocalDateTime.of(2021, 9, 13, 10, 0))
                .endDateTime(LocalDateTime.of(2021, 10, 14, 10, 0))
                .build())
            .status(NotificationStatus.EXPIRED)
            .creator(TEST_URID)
            .build());

        notificationRepository.save(Notification.builder()
            .definition(adHocDefinition)
            .schedule(NotificationSchedule.builder()
                .startDateTime(LocalDateTime.of(2021, 11, 13, 9, 30))
                .build())
            .status(NotificationStatus.PENDING)
            .creator(TEST_URID)
            .build());

        notificationRepository.save(Notification.builder()
            .definition(adHocDefinition)
            .schedule(NotificationSchedule.builder()
                .startDateTime(LocalDateTime.of(2021, 11, 12, 9, 30))
                .endDateTime(LocalDateTime.of(2021, 11, 14, 9, 0))
                .build())
            .status(NotificationStatus.PENDING)
            .creator(TEST_URID)
            .build());

        notificationRepository.save(Notification.builder()
            .definition(adHocDefinition)
            .schedule(NotificationSchedule.builder()
                .startDateTime(LocalDateTime.of(2021, 12, 12, 9, 30))
                .build())
            .status(NotificationStatus.PENDING)
            .creator(TEST_URID)
            .build());

        adHocNotification = Notification.builder()
            .definition(adHocDefinition)
            .status(NotificationStatus.ACTIVE)
            .schedule(NotificationSchedule.builder()
                .startDateTime(LocalDateTime.of(2021, 10, 1, 10, 0))
                .endDateTime(LocalDateTime.of(2021, 11, 13, 9, 0))
                .build())
            .creator(TEST_URID)
            .build();
        notificationRepository.save(adHocNotification);
        
        inactiveUserNotification = Notification.builder()
            .definition(inactiveUserDefinition)
            .status(NotificationStatus.ACTIVE)
            .schedule(NotificationSchedule.builder()
                .startDateTime(now)
                .endDateTime(now.plusDays(2))
                .runEveryXDays(1)
                .build())
            .creator(INACTIVE_USER_TEST_URID)
                .shortText("InactiveUser Subject")
                .longText(INACTIVE_USER_TEMPLATE)
            .build();
        notificationRepository.save(inactiveUserNotification);

        notificationRepository.flush();
    }

    public Notification createAndSaveAdHocEmailNotification() {
        adHocEmailDefinition = definitionRepository.findByType(NotificationType.AD_HOC_EMAIL).orElse(null);
        return notificationRepository.save(Notification.builder()
            .definition(adHocEmailDefinition)
            .status(NotificationStatus.ACTIVE)
            .schedule(NotificationSchedule.builder()
                .startDateTime(LocalDateTime.of(2021, 10, 1, 0, 0))
                .build())
            .creator(TEST_URID)
            .uploadedFile(createUploadedFile())
            .shortText("Email Subject")
            .longText("Hi ${Name} your age is ${Age}")
            .build());
    }

    public UploadedFile createUploadedFile() {
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFileName("example.xlsx");

        // Generate XLSX content
        byte[] fileData = createSampleXlsx();

        uploadedFile.setFileData(fileData);
        uploadedFile.setFileStatus(FileStatus.SUBMITTED);
        uploadedFile.setCreationDate(LocalDateTime.now());
        uploadedFile.setFileSize(fileData.length + " bytes");

        return uploadedFilesRepository.save(uploadedFile);
    }

    private byte[] createSampleXlsx() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Sample Data");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Email", "Name", "Age"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(getHeaderCellStyle(workbook));
            }

            // Add sample data rows
            Object[][] sampleData = {
                {"john@example.com", "John", 30},
                {"alice@example.com", "Alice", 25}
            };

            for (int i = 0; i < sampleData.length; i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < sampleData[i].length; j++) {
                    row.createCell(j).setCellValue(sampleData[i][j].toString());
                }
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error creating XLSX file", e);
        }
    }

    private static CellStyle getHeaderCellStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        return headerStyle;
    }
}

