package gov.uk.ets.registry.api.file.upload.allocationtable.services;

import com.google.re2j.Matcher;
import com.google.re2j.Pattern;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.repository.AircraftOperatorRepository;
import gov.uk.ets.registry.api.account.repository.CompliantEntityRepository;
import gov.uk.ets.registry.api.account.repository.InstallationRepository;
import gov.uk.ets.registry.api.allocation.repository.AllocationEntryRepository;
import gov.uk.ets.registry.api.allocation.service.AllocationUtils;
import gov.uk.ets.registry.api.allocation.service.AllocationYearCapService;
import gov.uk.ets.registry.api.allocation.type.AllocationCategory;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.file.upload.allocationtable.error.AllocationTableBusinessRulesException;
import gov.uk.ets.registry.api.file.upload.allocationtable.error.AllocationTableError;
import gov.uk.ets.registry.api.file.upload.allocationtable.error.AllocationTableUploadBusinessError;
import gov.uk.ets.registry.api.file.upload.allocationtable.error.AllocationTableYearException;
import gov.uk.ets.registry.api.file.upload.error.FileNameNotValidException;
import gov.uk.ets.registry.api.file.upload.wrappers.AllocationTableContentValidationWrapper;
import gov.uk.ets.registry.api.transaction.domain.data.IssuanceBlockSummary;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class AllocationTableExcelFileValidationService {
    private final InstallationRepository installationRepository;
    private final AircraftOperatorRepository aircraftOperatorRepository;
    private final AllocationYearCapService allocationYearCapService;
    private final AllocationEntryRepository allocationEntryRepository;
    private final CompliantEntityRepository compliantEntityRepository;
    private final AllocationUtils allocationUtils;

    private static final Pattern ALLOCATION_TABLE_FILE_NAME_PATTERN =
        Pattern.compile("^UK(_NAT|_NAVAT|_NER)_(20[0-9]{2}_20[0-9]{2})_(\\w+)$");

    /**
     * Method for the validation of the file name. <br>
     *
     * @param fileName the file name
     * @return the file name if successful, otherwise it throws a
     *     {@link gov.uk.ets.registry.api.file.upload.error.FileNameNotValidException}
     */
    public String[] validateFileName(String fileName) {

        Matcher matcher = ALLOCATION_TABLE_FILE_NAME_PATTERN.matcher(
            FilenameUtils.getBaseName(fileName.toUpperCase()));

        if (!matcher.matches()) {
            throw new FileNameNotValidException(
                "Not a valid allocation table name, " +
                    "should follow the pattern UK_<NAT/NAVAT/NER>_<ALLOCATION_YEAR_RANGE>_<MD5 checksum> " +
                    "(and optionally)<_suffix>.xlsx");
        }
        return FilenameUtils.getBaseName(fileName).split("_");
    }

    /**
     * Method for the validation of the file content.
     *
     * @param multiPartInputStream the input stream of the uploaded file
     * @param wrapper              the wrapper that contains data for the validation of the file content
     * @throws IOException if I/O interruption occurs
     */
    public List<AllocationTableUploadBusinessError> validateFileContent(
        InputStream multiPartInputStream, AllocationTableContentValidationWrapper wrapper) throws IOException {

        if (!wrapper.getFileNameArray()[4].equalsIgnoreCase(wrapper.getChecksum())) {
            throw new FileNameNotValidException("MD5 checksum is invalid");
        }

        AllocationCategory allocationCategory = AllocationType.parse(wrapper.getFileNameArray()[1]).getCategory();
        boolean pendingTaskExists = allocationUtils.pendingAllocationTableTaskExists(allocationCategory);

        if (pendingTaskExists) {
            throw AllocationTableBusinessRulesException.create(
                AllocationTableError.PENDING_ALLOCATION_TABLE_TASK_APPROVAL);
        }

        if (allocationUtils.hasPendingAllocationOrTransactions(allocationCategory)) {
            throw AllocationTableBusinessRulesException
                .create(AllocationTableError.PENDING_ALLOCATION_REQUEST_TASK_OR_ALLOCATION_JOB);
        }

        ReadableWorkbook wb = new ReadableWorkbook(multiPartInputStream);
        Sheet sheet = wb.getFirstSheet();
        List<Row> sheetList = sheet.read();

        if (sheetList.isEmpty()) {
            throw AllocationTableBusinessRulesException.create(AllocationTableError.INVALID_TABLE_EMPTY);
        }

        validateFileHeadersBusinessRules(sheetList.get(0).getCells(0, sheetList.get(0).getCellCount()), wrapper);
        sheetList.stream()
            .skip(1)//Header
            .filter(r -> r.getFirstNonEmptyCell().isPresent())//Ignore empty IDs
            .forEach(row -> {
                wrapper.getAllocationTableCompliantEntityIdentifiers().add(row.getCellText(0));
                for (int i = wrapper.getMinimumTableYearColumn(); i <= wrapper.getMaximumTableYearColumn(); i++) {
                    if (StringUtils.isBlank(row.getCellText(i)) || !NumberUtils.isParsable(row.getCellText(i)) || Integer.parseInt(row.getCellText(i)) < 0) {                        
                        wrapper.getFileContentExceptions().add(
                            AllocationTableUploadBusinessError
                                .builder()
                                .error(AllocationTableError.INVALID_QUANTITY)
                                .rowNumber(row.getRowNum())
                                .operatorId(row.getCellText(0))
                                .errorMessage(String.format(AllocationTableError.INVALID_QUANTITY.getMessage(),wrapper.getYears().get(i-wrapper.getMinimumTableYearColumn())))
                                .build());     
                    }
                }
                if (!wrapper.getFileContentExceptions().isEmpty()) {
                    return;  
                }

                validateColumnIdBusinessRules(wrapper,
                    row.getCellText(0),
                    row.getCells(wrapper.getMinimumTableYearColumn(),
                        wrapper.getMaximumTableYearColumn() + 1), row.getRowNum());
                validateYearColumnsBusinessRules(wrapper, row.getCells(wrapper.getMinimumTableYearColumn(),
                    wrapper.getMaximumTableYearColumn() + 1),
                    row.getCellText(0), row.getRowNum());
            });
        //AllocationTableCompliantEntityIdentifiers --> Added from excel file Ids
        //AllocationEntries -- > From DB
        if (!wrapper.getAllocationTableCompliantEntityIdentifiers().containsAll(wrapper.getAllocationEntries())) {
            
            //We want the values that are in set AllocationEntries 
            //that don't exist in AllocationTableCompliantEntityIdentifiers.
            Set<String> differenceSet = new HashSet<>(wrapper.getAllocationEntries());
            differenceSet.removeAll(wrapper.getAllocationTableCompliantEntityIdentifiers());
            String missingIdentifiers = differenceSet.stream().collect(Collectors.joining(","));
            
            wrapper.getFileContentExceptions().add(
                AllocationTableUploadBusinessError.builder()
                                                  .error(AllocationTableError.MISSING_ID)
                                                  .errorMessage(String.format(AllocationTableError.MISSING_ID.getMessage(),missingIdentifiers))
                                                  .build());
        }
        multiPartInputStream.reset();
        return wrapper.getFileContentExceptions();
    }


    private void validateFileHeadersBusinessRules(List<Cell> cells, AllocationTableContentValidationWrapper wrapper) {

        List<String> cellsAsStringValues = cells.stream()
            .filter(Objects::nonNull)
            .map(Cell::getText)
            .collect(Collectors.toList());

        switch (wrapper.getFileNameArray()[1]) {
            case "NAT":
            case "NER":
                if (!cellsAsStringValues.get(0).equalsIgnoreCase("installation id")) {
                    throw AllocationTableBusinessRulesException
                        .create(AllocationTableError.INVALID_COLUMNS_INSTALLATION);
                }
                wrapper.setCompliantEntityIdentifiers(installationRepository.findAllInstallations());
                break;
            case "NAVAT":
                if (!cellsAsStringValues.get(0).equalsIgnoreCase("operator id")) {
                    throw AllocationTableBusinessRulesException.create(AllocationTableError.INVALID_COLUMNS_OPERATOR);
                }
                wrapper.setCompliantEntityIdentifiers(aircraftOperatorRepository.findAllAircraftOperators());
                break;
            default:
        }

        List<String> rangeOfYearsList = IntStream.rangeClosed(
            Integer.parseInt(wrapper.getFileNameArray()[2]),
            Integer.parseInt(wrapper.getFileNameArray()[3])).mapToObj(String::valueOf).collect(Collectors.toList());


        if (!cellsAsStringValues.containsAll(rangeOfYearsList)) {
            throw AllocationTableBusinessRulesException.create(
                AllocationTableError.INVALID_COLUMNS_YEAR_MISSING);
        }
        //Set the years as found inside the excel
        wrapper.setYears(cellsAsStringValues.stream().skip(3).collect(Collectors.toList()));
        
        wrapper.setMinimumTableYearColumn(
            IntStream.range(0, cells.size())
                .filter(cell ->
                    wrapper.getFileNameArray()[2].equals(cellsAsStringValues.get(cell)))
                .findFirst()
                .orElseThrow(() -> new AllocationTableYearException(wrapper.getFileNameArray()[2])));

        wrapper.setMaximumTableYearColumn(
            IntStream.range(0, cells.size())
                .filter(cell ->
                    wrapper.getFileNameArray()[3].equals(cellsAsStringValues.get(cell)))
                .findFirst()
                .orElseThrow(() -> new AllocationTableYearException(wrapper.getFileNameArray()[3])));
        
        Set<String> yearsInFilenameNotInExcel = new HashSet<>(rangeOfYearsList);
        yearsInFilenameNotInExcel.removeAll(wrapper.getYears());
        if (!yearsInFilenameNotInExcel.isEmpty()) {
            throw new AllocationTableYearException(yearsInFilenameNotInExcel.stream().sorted().collect(Collectors.joining(",")));
        }
        
        Set<String> yearsInExcelNotInFilename = new HashSet<>(wrapper.getYears());
        yearsInExcelNotInFilename.removeAll(rangeOfYearsList);
        if (!yearsInExcelNotInFilename.isEmpty()) {
            throw new AllocationTableYearException(yearsInExcelNotInFilename.stream().sorted().collect(Collectors.joining(",")));
        }
        
        Set<String> currentPhaseYears = allocationYearCapService.getCapsForCurrentPhase()
            .stream()
            .map(IssuanceBlockSummary::getYear)
            .map(String::valueOf)
            .collect(Collectors.toSet());
        if (!currentPhaseYears.containsAll(rangeOfYearsList) || !currentPhaseYears.containsAll(wrapper.getYears())) {
            
            //We want the values that are in set rangeOfYearsList 
            //that don't exist in currentPhaseYears.
            Set<String> differenceSet = new HashSet<>(rangeOfYearsList);
            differenceSet.addAll(wrapper.getYears());
            differenceSet.removeAll(currentPhaseYears);
            String extraYears = differenceSet.stream().sorted().collect(Collectors.joining(","));
            
            wrapper.getFileContentExceptions().add(
                AllocationTableUploadBusinessError
                    .builder()
                    .error(AllocationTableError.INVALID_YEAR_PHASE)
                    .errorMessage(String.format(AllocationTableError.INVALID_YEAR_PHASE.getMessage(),extraYears))
                    .build());
        }

        wrapper.setAllocationEntries(allocationEntryRepository.retrieveAllocationEntriesByYearsAndStatus(
            rangeOfYearsList.stream()
                .map(Integer::valueOf)
                .collect(Collectors.toList()),
            wrapper.getFileNameArray()[1]).stream().map(String::valueOf).collect(Collectors.toSet()));
    }

    private void validateColumnIdBusinessRules(AllocationTableContentValidationWrapper wrapper,
                                               String idColumnCell,
                                               List<Cell> yearColumnCells, int rowNum) {

        if (yearColumnCells.stream().noneMatch(Objects::nonNull)) {
            return;
        }
        if (idColumnCell.isBlank()) {
            wrapper.getFileContentExceptions().add(
                AllocationTableUploadBusinessError
                    .builder()
                    .error(AllocationTableError.EMPTY_ID)
                    .rowNumber(rowNum)
                    .operatorId(idColumnCell)
                    .errorMessage(AllocationTableError.EMPTY_ID.getMessage())
                    .build());
        } else if (!wrapper.getCompliantEntityIdentifiers().contains(idColumnCell)) {
            wrapper.getFileContentExceptions().add(
                AllocationTableUploadBusinessError
                    .builder()
                    .error(AllocationTableError.ID_NOT_IN_REGISTRY)
                    .rowNumber(rowNum)
                    .operatorId(idColumnCell)
                    .errorMessage(AllocationTableError.ID_NOT_IN_REGISTRY.getMessage())
                    .build());

        } else if (!areAllIdsUnique(wrapper, idColumnCell)) {
            wrapper.getFileContentExceptions().add(
                AllocationTableUploadBusinessError
                    .builder()
                    .error(AllocationTableError.DUPLICATE_ID_ENTRIES)
                    .rowNumber(rowNum)
                    .operatorId(idColumnCell)
                    .errorMessage(AllocationTableError.DUPLICATE_ID_ENTRIES.getMessage())
                    .build());
        }
    }

    private void validateYearColumnsBusinessRules(AllocationTableContentValidationWrapper wrapper,
                                                  List<Cell> yearColumnCells,
                                                  String idColumnCell,
                                                  int rowNum) {

        Optional<CompliantEntity> compliantEntity;
        try {
            compliantEntity = compliantEntityRepository.findByIdentifier(Long.valueOf(idColumnCell));
            if (compliantEntity.isEmpty()) {
                return;
            }
        } catch (NumberFormatException exception) {
                return;
        }
        //Years from filename
        int[] yearsRange = IntStream
            .range(Integer.parseInt(wrapper.getFileNameArray()[2]), Integer.parseInt(wrapper.getFileNameArray()[3]) + 1)
            .toArray();

        try {
            if (!idColumnCell.isBlank()) {
                for(int i =0 ; i < yearColumnCells.size() ; i++) {
                    if(!(yearColumnCells.get(i) != null) || !(Integer.parseInt(yearColumnCells.get(i).getText()) >= 0)) {
                        wrapper.getFileContentExceptions().add(
                            AllocationTableUploadBusinessError
                                .builder()
                                .error(AllocationTableError.INVALID_QUANTITY)
                                .rowNumber(rowNum)
                                .operatorId(idColumnCell)
                                .errorMessage(String.format(AllocationTableError.INVALID_QUANTITY.getMessage(),wrapper.getYears().get(i)))
                                .build());    
                    }                  
                }

            }

            if (!idColumnCell.isBlank() && yearColumnCells.stream().anyMatch(
                i -> i != null && Integer.parseInt(i.getText()) > 0 &&
                    (yearsRange[yearColumnCells.indexOf(i)] < compliantEntity.get().getStartYear() ||
                        compliantEntity.get().getEndYear() != null &&
                            yearsRange[yearColumnCells.indexOf(i)] > compliantEntity.get().getEndYear()))) {
                wrapper.getFileContentExceptions().add(
                    AllocationTableUploadBusinessError
                        .builder()
                        .error(AllocationTableError.NON_ZERO_QUANTITY_FOR_INACTIVE_ENTITY)
                        .rowNumber(rowNum)
                        .operatorId(idColumnCell)
                        .errorMessage(AllocationTableError.NON_ZERO_QUANTITY_FOR_INACTIVE_ENTITY.getMessage())
                        .build());
            }

        } catch (NumberFormatException exception) {
            wrapper.getFileContentExceptions().add(
                AllocationTableUploadBusinessError
                    .builder()
                    .error(AllocationTableError.INVALID_QUANTITY)
                    .rowNumber(rowNum)
                    .operatorId(idColumnCell)
                    .errorMessage(AllocationTableError.INVALID_QUANTITY.getMessage())
                    .build());
        }
    }

    private boolean areAllIdsUnique(AllocationTableContentValidationWrapper wrapper, String columnId) {
        return wrapper.getUniqueIdentifiers().add(columnId);
    }
}
