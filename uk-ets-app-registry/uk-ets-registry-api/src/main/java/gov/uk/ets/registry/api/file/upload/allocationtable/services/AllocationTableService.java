package gov.uk.ets.registry.api.file.upload.allocationtable.services;

import static java.util.stream.Collectors.toList;

import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.repository.AircraftOperatorRepository;
import gov.uk.ets.registry.api.account.repository.CompliantEntityRepository;
import gov.uk.ets.registry.api.account.repository.InstallationRepository;
import gov.uk.ets.registry.api.allocation.configuration.AllocationConfigurationService;
import gov.uk.ets.registry.api.allocation.data.AllocationClassificationSummary;
import gov.uk.ets.registry.api.allocation.data.AllocationSummary;
import gov.uk.ets.registry.api.allocation.domain.AllocationEntry;
import gov.uk.ets.registry.api.allocation.domain.AllocationStatus;
import gov.uk.ets.registry.api.allocation.domain.AllocationYear;
import gov.uk.ets.registry.api.allocation.repository.AllocationEntryRepository;
import gov.uk.ets.registry.api.allocation.repository.AllocationStatusRepository;
import gov.uk.ets.registry.api.allocation.repository.AllocationYearRepository;
import gov.uk.ets.registry.api.allocation.service.AllocationCalculationService;
import gov.uk.ets.registry.api.allocation.type.AllocationClassification;
import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.common.error.UkEtsException;
import gov.uk.ets.registry.api.file.upload.allocationtable.AllocationTableSummary;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.NumberUtils;

/**
 * Service for handling allocation tables.
 */
@Service
@AllArgsConstructor
public class AllocationTableService {

    /**
     * Repository for allocation tables.
     */
    private UploadedFilesRepository uploadedFilesRepository;

    /**
     * Repository for allocation entries.
     */
    private AllocationEntryRepository allocationEntryRepository;

    /**
     * Repository for allocation years.
     */
    private AllocationYearRepository allocationYearRepository;

    /**
     * Repository for installations.
     */
    private InstallationRepository installationRepository;

    /**
     * Repository for aircraft operators.
     */
    private AircraftOperatorRepository aircraftOperatorRepository;

    /**
     * Allocation status repository.
     */
    private AllocationStatusRepository allocationStatusRepository;

    /**
     * Allocation configuration service.
     */
    private final AllocationConfigurationService allocationConfigurationService;

    /**
     * Allocation calculation service.
     */
    private final AllocationCalculationService allocationCalculationService;

    /**
     * Compliant entity repository.
     */
    private final CompliantEntityRepository compliantEntityRepository;

    /**
     * Task repository.
     */
    private final TaskRepository taskRepository;

    /**
     * Populates the allocation table entries for the provided task.
     *
     * @param requestIdentifier The request identifier.
     */
    @Transactional
    public void submitAllocationEntries(Long requestIdentifier) throws IOException {
        UploadedFile file = uploadedFilesRepository.findFirstByTaskRequestId(requestIdentifier);
        AllocationTableSummary table = loadAllocationTable(file.getFileName(), file.getFileData());

        Set<Integer> years = new HashSet<>();
        table.getAllocations().forEach(entry -> {
            final Long identifier = entry.getIdentifier();
            final AllocationType type = table.getType();
            final AllocationYear year = allocationYearRepository.findByYear(entry.getYear());
            years.add(entry.getYear());

            Long id = type.isRelatedWithInstallation() ?
                installationRepository.getCompliantEntityId(identifier) :
                aircraftOperatorRepository.getCompliantEntityId(identifier);

            AllocationEntry allocationEntry = allocationEntryRepository
                .findByCompliantEntityIdAndTypeAndAllocationYear_Year(id, type, entry.getYear());
            if (allocationEntry == null) {
                allocationEntry = new AllocationEntry();
                allocationEntry.setEntitlement(entry.getEntitlement());
                allocationEntry.setType(type);
                allocationEntry.setCompliantEntityId(id);
                allocationEntry.setAllocationYear(year);

            } else {
                allocationEntry.setEntitlement(entry.getEntitlement());
            }
            allocationEntryRepository.save(allocationEntry);

            AllocationStatus status =
                allocationStatusRepository.findByCompliantEntityIdAndAllocationYear_Year(id, entry.getYear());
            if (status == null) {
                status = new AllocationStatus();
                status.setCompliantEntityId(id);
                status.setStatus(AllocationStatusType.ALLOWED);
                status.setAllocationYear(year);
                allocationStatusRepository.save(status);
            }
            CompliantEntity compliantEntity =
                compliantEntityRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException(
                        "Error while fetching the compliant entity"));
            compliantEntity.setAllocationClassification(updateAllocationClassification(compliantEntity.getId()));
            compliantEntity.setAllocationWithholdStatus(AllocationStatusType.ALLOWED);
            compliantEntityRepository.save(compliantEntity);
        });

        years.forEach(allocationCalculationService::calculateAndUpdateRegistryStatus);
    }

    /**
     * Loads the allocation table.
     *
     * @param filename    The file name.
     * @param fileContent The file content.
     * @return an allocation table summary.
     */
    public AllocationTableSummary loadAllocationTable(String filename, byte[] fileContent) throws IOException {

        ReadableWorkbook book = new ReadableWorkbook(new ByteArrayInputStream(fileContent));
        Sheet sheet = book.getFirstSheet();
        List<Row> rows = sheet.read();

        String[] fileNameArray = FilenameUtils.getBaseName(filename).split("_");

        List<Cell> header = rows.get(0).getCells(0, rows.get(0).getCellCount());
        List<String> headerValues = header.stream().map(Cell::getText).collect(toList());

        int fromYearIndex = IntStream.range(0, header.size())
            .filter(cell -> fileNameArray[2].equals(headerValues.get(cell))).findFirst().orElse(0);

        int toYearIndex = IntStream.range(0, header.size())
            .filter(cell -> fileNameArray[3].equals(headerValues.get(cell))).findFirst().orElse(0);

        List<AllocationSummary> allocations = new ArrayList<>();

        rows.stream()
            .skip(1)
            .filter(r -> r.getFirstNonEmptyCell().isPresent())
            .forEach(row -> {
                Long identifier = NumberUtils.parseNumber(row.getCellText(0), Long.class);
                final List<Cell> cells = row.getCells(0, toYearIndex + 1);
                for (int index = fromYearIndex; index <= toYearIndex; index++) {
                    allocations.add(new AllocationSummary(
                        identifier,
                        NumberUtils.parseNumber(headerValues.get(index), Integer.class),
                        NumberUtils.parseNumber(cells.get(index).getText(), Long.class)
                    ));
                }
            });

        AllocationTableSummary result = new AllocationTableSummary();
        result.setType(AllocationType.parse(fileNameArray[1]));
        result.setAllocations(allocations);
        return result;
    }

    public AllocationClassification updateAllocationClassification(Long compliantEntityId) {

        AllocationClassificationSummary allocationClassificationSummary =
            allocationCalculationService.calculateAllocationClassification(
                allocationConfigurationService.getAllocationYear(), compliantEntityId);

        if (Objects.nonNull(allocationClassificationSummary.getAllocationClassification())) {
            return AllocationClassification
                .valueOf(allocationClassificationSummary.getAllocationClassification());            
        }

        return null;
    }

    /**
     * Returns a list with compliant entities' IDs included in a pending ALLOCATION_TABLE_UPLOAD_REQUEST task.
     **/
    public Set<Long> getEntitiesInPendingAllocationTableUploadTask() {
        List<Task> allocationTableUploadTasks =
            taskRepository.findPendingTasksByType(RequestType.ALLOCATION_TABLE_UPLOAD_REQUEST);

        if (!allocationTableUploadTasks.isEmpty()) {
            UploadedFile file =
                uploadedFilesRepository.findFirstByTaskRequestId(allocationTableUploadTasks.get(0).getRequestId());

            AllocationTableSummary allocationTableSummary;
            try {
                allocationTableSummary = loadAllocationTable(file.getFileName(), file.getFileData());
            } catch (IOException e) {
                throw new UkEtsException("Error while processing the file");
            }

            return allocationTableSummary.getAllocations().stream().map(AllocationSummary::getIdentifier)
                .collect(Collectors.toSet());
        }
        return new HashSet<>();
    }
}
