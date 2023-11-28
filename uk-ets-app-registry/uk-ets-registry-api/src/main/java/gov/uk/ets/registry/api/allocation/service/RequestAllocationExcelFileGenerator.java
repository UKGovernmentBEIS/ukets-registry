package gov.uk.ets.registry.api.allocation.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AircraftOperator;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.allocation.data.AllocationSummary;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.common.ConversionService;
import gov.uk.ets.registry.api.common.error.UkEtsException;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.file.upload.types.FileStatus;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

@Service
@RequiredArgsConstructor
@Log4j2
public class RequestAllocationExcelFileGenerator {

    private final ConversionService conversionService;
    private final UploadedFilesRepository uploadedFilesRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public void generateExcel(Task task, String fileName,
                              List<AllocationSummary> beneficiaryRecipients) {
        byte[] excel = generateFile(beneficiaryRecipients, false);
        saveFile(excel, fileName, task);
    }

    public byte[] generateExtendedExcel(byte[] excel) {
        try {
            List<AllocationSummary> beneficiaryRecipients = getBeneficiaryRecipients(excel);
            return generateFile(beneficiaryRecipients, true);
        } catch (IOException e) {
            throw new UkEtsException("Error while processing the file");
        }
    }

    /**
     * key = column number
     * value = header title
     */
    static final Map<Integer, String> headers = Map.of(
        0, "Account",
        1, "Identifier",
        2, "Plan",
        3, "Quantity to allocate",
        4, "Entitlement",
        5, "Already Allocated"
    );

    /**
     * key = column number
     * value = header title
     */
    static final Map<Integer, String> headersExtended = Map.of(
        6, "Account Holder",
        7, "Permit or Monitoring Plan ID",
        8, "Installation Name",
        9, "Regulator"
    );

    public byte[] generateFile(
        List<AllocationSummary> beneficiaryRecipients, boolean extended) {

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            Workbook wb = new Workbook(stream, "uk-ets", "1.0");
            Worksheet ws = wb.newWorksheet("Data");

            headers.forEach((key, value) -> writeValue(ws, value, 0, key));

            if (extended) {
                headersExtended.forEach((key, value) -> writeValue(ws, value, 0, key));
            }

            for (int i = 0; i < beneficiaryRecipients.size(); i++) {
                AllocationSummary summary = beneficiaryRecipients.get(i);
                writeValue(ws, summary.getAccountFullIdentifier(), i + 1, 0);
                writeValue(ws, summary.getIdentifier(), i + 1, 1);
                writeValue(ws, summary.getType().toString(), i + 1, 2);
                writeValue(ws, summary.getRemaining(), i + 1, 3);
                writeValue(ws, summary.getEntitlement(), i + 1, 4);
                writeValue(ws, summary.getAllocated(), i + 1, 5);

                if (extended) {
                    String permitOrMonitoringPlanId = "";
                    String installationName = "";

                    Account account =
                        accountRepository.findByFullIdentifier(summary.getAccountFullIdentifier()).orElse(null);

                    if (account != null) {
                        if (RegistryAccountType.OPERATOR_HOLDING_ACCOUNT.equals(account.getRegistryAccountType())) {
                            Installation installation = (Installation) Hibernate.unproxy(account.getCompliantEntity());
                            permitOrMonitoringPlanId = installation.getPermitIdentifier();
                            installationName = installation.getInstallationName();
                        } else if (RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT
                            .equals(account.getRegistryAccountType())) {
                            AircraftOperator aircraftOperator =
                                (AircraftOperator) Hibernate.unproxy(account.getCompliantEntity());
                            permitOrMonitoringPlanId = aircraftOperator.getMonitoringPlanIdentifier();
                        }
                        writeValue(ws, account.getAccountHolder().actualName(), i + 1, 6);
                        writeValue(ws, permitOrMonitoringPlanId, i + 1, 7);
                        writeValue(ws, installationName, i + 1, 8);
                        writeValue(ws, account.getCompliantEntity().getRegulator().toString(), i + 1, 9);
                    }
                }
            }

            wb.finish();
            return stream.toByteArray();
        } catch (IOException e) {
            // this is used to rollback transaction in case of Excel generation failure
            throw new UkEtsException(e);
        }
    }

    private void writeValue(Worksheet ws, Object value, int row, int column) {
        if (value == null) {
            ws.value(row, column, "");
        } else if (value instanceof String) {
            ws.value(row, column, defaultIfBlank((String) value, ""));
        } else if (value instanceof Boolean) {
            ws.value(row, column, (Boolean) value);
        } else if (value instanceof Number) {
            ws.value(row, column, (Number) value);
        } else if (value instanceof Date) {
            ws.value(row, column, (Date) value);
        } else if (value instanceof LocalDate) {
            ws.value(row, column, (LocalDate) value);
            ws.style(row, column).format("yyyy-MM-dd").set();
        } else if (value instanceof LocalDateTime) {
            ws.value(row, column, (LocalDateTime) value);
            ws.style(row, column).format("yyyy-MM-dd H:mm:ss").set();
        } else {
            throw new RuntimeException(String.format("data type not supported :%s", value.getClass()));
        }
    }

    private void saveFile(byte[] fileData, String fileName,
                          Task task) {
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFileName(fileName);
        uploadedFile.setFileData(fileData);
        uploadedFile.setFileStatus(FileStatus.SUBMITTED);
        uploadedFile.setFileSize(conversionService.convertByteAmountToHumanReadable(fileData.length));
        uploadedFile.setCreationDate(LocalDateTime.now());
        uploadedFile.setTask(task);
        uploadedFilesRepository.save(uploadedFile);
    }

    private List<AllocationSummary> getBeneficiaryRecipients(byte[] bytes) throws IOException {
        List<AllocationSummary> beneficiaryRecipients = new ArrayList<>();

        InputStream inputStream = new ByteArrayInputStream(bytes);
        ReadableWorkbook wb = new ReadableWorkbook(inputStream);

        Sheet sheet = wb.getFirstSheet();
        List<Row> rows = sheet.read();

        rows.stream()
            .skip(1)
            .forEach(row -> {
                AllocationSummary summary = new AllocationSummary();
                summary.setAccountFullIdentifier(row.getCell(0).getText());
                summary.setIdentifier(((BigDecimal)row.getCell(1).getValue()).longValue());
                summary.setType(AllocationType.parse(row.getCell(2).getText()));
                summary.setRemaining(((BigDecimal)row.getCell(3).getValue()).longValue());
                summary.setEntitlement(((BigDecimal)row.getCell(4).getValue()).longValue());
                summary.setAllocated(((BigDecimal)row.getCell(5).getValue()).longValue());
                beneficiaryRecipients.add(summary);
            });

        return beneficiaryRecipients;
    }
}
