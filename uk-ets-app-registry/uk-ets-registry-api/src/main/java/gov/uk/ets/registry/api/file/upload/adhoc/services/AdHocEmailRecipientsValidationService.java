package gov.uk.ets.registry.api.file.upload.adhoc.services;

import gov.uk.ets.registry.api.file.upload.adhoc.services.error.AdHocEmailRecipientsError;
import gov.uk.ets.registry.api.user.admin.service.UserAdministrationService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdHocEmailRecipientsValidationService {

    private final UserAdministrationService userAdministrationService;

    public AdhocFileValidationWrapper validateFileContent(InputStream multiPartInputStream) throws IOException {
        final Map<AdHocEmailRecipientsError, Set<Integer>> errorRowsMapping = new LinkedHashMap<>();
        try (ReadableWorkbook workbook = new ReadableWorkbook(multiPartInputStream)) {
            Sheet sheet = workbook.getFirstSheet();
            List<Row> sheetList = sheet.read();
            // Validate headers
            if (sheetList.isEmpty() || sheetList.get(0) == null) {
                errorRowsMapping.computeIfAbsent(AdHocEmailRecipientsError.MISSING_HEADER, k -> new LinkedHashSet<>()).add(0);
                return new AdhocFileValidationWrapper(errorRowsMapping, null);
            }
            int numberOfHeaders = sheetList.get(0).getCellCount();
            validateColumnHeaders(sheetList.get(0).getCells(0, numberOfHeaders), errorRowsMapping);
            // Validate if there are any data rows
            if (sheetList.size() <= 1) {
                errorRowsMapping.computeIfAbsent(AdHocEmailRecipientsError.INVALID_RECIPIENT, k -> new LinkedHashSet<>()).add(0);
                return new AdhocFileValidationWrapper(errorRowsMapping, null);
            }
            // Set to track unique rows
            final Set<String> uniqueRows = new HashSet<>();
            // Validate each row
            for (int rowNumber = 1; rowNumber < sheetList.size(); rowNumber++) {
                Row row = sheetList.get(rowNumber);
                validateRow(row, rowNumber, uniqueRows, numberOfHeaders, errorRowsMapping);
            }
            return new AdhocFileValidationWrapper(errorRowsMapping, uniqueRows.size());
        }
    }

    private void validateRow(Row row, int rowNumber, Set<String> uniqueRows, int numberOfHeaders, Map<AdHocEmailRecipientsError, Set<Integer>> errorRowsMapping) {
        if (row == null) {
            errorRowsMapping.computeIfAbsent(AdHocEmailRecipientsError.INVALID_RECIPIENT, k -> new LinkedHashSet<>()).add(rowNumber);
            return;
        }
        // Validate email
        String email = row.getCellText(0);
        if (email == null || email.isBlank()) {
            errorRowsMapping.computeIfAbsent(AdHocEmailRecipientsError.INVALID_RECIPIENT, k -> new LinkedHashSet<>()).add(rowNumber);
            return;
        }
        // Check for duplicate rows
        String rowContent = row.getCells(0, Math.min(numberOfHeaders, row.getCellCount())).stream()
            .map(cell -> cell != null ? cell.getText() : "")
            .collect(Collectors.joining("|"));
        if (!uniqueRows.add(rowContent)) {
            errorRowsMapping.computeIfAbsent(AdHocEmailRecipientsError.DUPLICATE_RECIPIENTS, k -> new LinkedHashSet<>()).add(rowNumber);
        }
        // Check if email exists in the database
        if (userAdministrationService.findByEmail(email).isEmpty()) {
            errorRowsMapping.computeIfAbsent(AdHocEmailRecipientsError.INVALID_RECIPIENT, k -> new LinkedHashSet<>()).add(rowNumber);
        }
    }

    private void validateColumnHeaders(List<Cell> cells, Map<AdHocEmailRecipientsError, Set<Integer>> errorRowsMapping) {
        if (cells == null || cells.isEmpty()) {
            errorRowsMapping.computeIfAbsent(AdHocEmailRecipientsError.MISSING_HEADER, k -> new LinkedHashSet<>()).add(0);
            return;
        }
        Set<String> headers = new HashSet<>();
        for (Cell cell : cells) {
            if (cell == null || cell.getValue() == null) {
                errorRowsMapping.computeIfAbsent(AdHocEmailRecipientsError.MISSING_HEADER, k -> new LinkedHashSet<>()).add(0);
                return;
            }
            String header = (String) cell.getValue();
            // Ensure no spaces in headers
            if (header.contains(" ")) {
                errorRowsMapping.computeIfAbsent(AdHocEmailRecipientsError.INVALID_HEADER_SPACES_NOT_ALLOWED, k -> new LinkedHashSet<>()).add(0);
            }
            // Ensure unique headers
            if (!headers.add(header)) {
                errorRowsMapping.computeIfAbsent(AdHocEmailRecipientsError.DUPLICATE_COLUMNS, k -> new LinkedHashSet<>()).add(0);
            }
        }
    }
}
