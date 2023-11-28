package gov.uk.ets.registry.api.file.upload.bulkar.services;

import static gov.uk.ets.registry.api.file.upload.bulkar.error.BulkArError.*;
import static java.util.function.Predicate.not;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.file.upload.bulkar.error.BulkArBusinessRulesException;
import gov.uk.ets.registry.api.file.upload.bulkar.error.BulkArError;
import gov.uk.ets.registry.api.file.upload.wrappers.BulkArAccountAccessDTO;
import gov.uk.ets.registry.api.file.upload.wrappers.BulkArAccountDTO;
import gov.uk.ets.registry.api.file.upload.wrappers.BulkArContentValidationWrapper;
import gov.uk.ets.registry.api.file.upload.wrappers.BulkArTaskDTO;
import gov.uk.ets.registry.api.file.upload.wrappers.BulkArUserDTO;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BulkArExcelFileValidationService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountAccessRepository accountAccessRepository;
    private final TaskRepository taskRepository;

    /**
     * Method for the validation of the file content.
     *
     * @param multiPartInputStream the input stream of the uploaded file
     * @param wrapper              the wrapper that contains data for the validation of the file content
     * @throws IOException if I/O interruption occurs
     */
    public void validateFileContent(InputStream multiPartInputStream,
                                    BulkArContentValidationWrapper wrapper) throws IOException {

        ReadableWorkbook wb = new ReadableWorkbook(multiPartInputStream);
        Sheet sheet = wb.getFirstSheet();
        List<Row> sheetList = sheet.read();
        validateColumnHeaders(sheetList.get(0).getCells(0, sheetList.get(0).getCellCount()),
                              wrapper);

        if (sheetList.stream().skip(1).findAny().isEmpty()) {
            throw BulkArBusinessRulesException.create(INVALID_TABLE_EMPTY.getMessage());
        }

        List<BulkArAccountDTO> accountDTOS = accountRepository.retrieveAccounts();
        List<BulkArUserDTO> userDTOS = userRepository.retrieveUsersByUrid();
        List<BulkArAccountAccessDTO> accountAccessDTOS = accountAccessRepository.retrieveAccountAccesses();
        List<BulkArTaskDTO> tasks =
            taskRepository.retrieveTasksByRequestType(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST);

        sheetList.stream().skip(1).filter(r -> r.getFirstNonEmptyCell().isPresent()).forEach(row -> {
            validateDuplicates(row.getRowNum(), wrapper,
                               row.getCellText(wrapper.getAccountNumberPosition())
                                  .concat(row.getCellText(wrapper.getUserIdPosition())));
            validateAccountNumbers(accountDTOS, row.getRowNum(), wrapper,
                                   row.getCellText(wrapper.getAccountNumberPosition()));
            validateUserId(userDTOS, row.getRowNum(), wrapper, row.getCellText(wrapper.getUserIdPosition()));
            validateAccountPermission(row.getRowNum(), wrapper, row.getCellText(wrapper.getPermissionPosition()));
            validateUserIsNotAlreadyAr(accountAccessDTOS, row.getRowNum(), wrapper,
                                       row.getCellText(wrapper.getUserIdPosition()),
                                       row.getCellText(wrapper.getAccountNumberPosition()));
            validatePendingAddArTasks(wrapper, row.getRowNum(), row.getCellText(wrapper.getAccountNumberPosition()),
                                      row.getCellText(wrapper.getUserIdPosition()), tasks);
        });

        if (!wrapper.getFileContentExceptions().isEmpty()) {
            BulkArBusinessRulesException exception = new BulkArBusinessRulesException();
            exception.addErrors(wrapper.getFileContentExceptions());
            throw exception;
        }
    }

    /**
     * Validates columns and assigns their positions in BulkArContentValidationWrapper object.
     * @param cells the list of cells.
     * @param wrapper the BulkArContentValidationWrapper wrapper.
     */
    public void validateColumnHeaders(List<Cell> cells, BulkArContentValidationWrapper wrapper) {

        List<String> mandatoryFields = Arrays.asList("Account number", "User ID", "Permissions");

        if (cells.stream().anyMatch(Objects::isNull)) {
            wrapper.getFileContentExceptions().add(INVALID_COLUMNS.getMessage());
        }

        List<String> cellsAsStringValues = cells.stream()
                                                .filter(Objects::nonNull)
                                                .map(Cell::getText)
                                                .map(String::toLowerCase)
                                                .collect(Collectors.toList());

        List<Integer> mandatoryFieldIndices = mandatoryFields.stream()
                                                     .map(String::toLowerCase)
                                                     .map(cellsAsStringValues::indexOf)
                                                     .collect(Collectors.toList());

        List<Integer> notFoundList = IntStream.range(0,mandatoryFieldIndices.size())
                                              .boxed()
                                              .filter(i -> Integer.valueOf(-1).equals(mandatoryFieldIndices.get(i)))
                                              .collect(Collectors.toList());

        notFoundList.forEach(i -> wrapper.getFileContentExceptions()
                                         .add(mandatoryFields.get(i) + INVALID_COLUMN_MISSING.getMessage()));

        if (!wrapper.getFileContentExceptions().isEmpty()) {
           BulkArBusinessRulesException exception = new BulkArBusinessRulesException();
           exception.addErrors(wrapper.getFileContentExceptions());
           throw exception;
        }
        wrapper.setAccountNumberPosition(mandatoryFieldIndices.get(0));
        wrapper.setUserIdPosition(mandatoryFieldIndices.get(1));
        wrapper.setPermissionPosition(mandatoryFieldIndices.get(2));
    }

    private void validateAccountNumbers(List<BulkArAccountDTO> dtos, int rowNum, BulkArContentValidationWrapper wrapper, String accountIdCell) {
        if (accountIdCell.isBlank()) {
            wrapper.getFileContentExceptions().add(wrapError(rowNum, EMPTY_ACCOUNT_ID));
        } else if (dtos.stream().noneMatch(d -> d.getFullIdentifier().equals(accountIdCell))) {
            wrapper.getFileContentExceptions().add(wrapError(rowNum, ACCOUNT_ID_NOT_IN_REGISTRY));
        } else if (dtos.stream()
                       .filter(BulkArAccountDTO::isAccountStatusClosed)
                       .anyMatch(d -> d.getFullIdentifier().equals(accountIdCell))) {
            wrapper.getFileContentExceptions().add(wrapError(rowNum, ACCOUNT_STATUS_CLOSED));
        } else if (dtos.stream()
                       .filter(BulkArAccountDTO::isAccountStatusClosurePending)
                       .anyMatch(d -> d.getFullIdentifier().equals(accountIdCell))) {
            wrapper.getFileContentExceptions().add(wrapError(rowNum, ACCOUNT_STATUS_CLOSURE_PENDING));
        } else if (dtos.stream()
                       .filter(BulkArAccountDTO::isAccountStatusProposed)
                       .anyMatch(d -> d.getFullIdentifier().equals(accountIdCell))) {
            wrapper.getFileContentExceptions().add(wrapError(rowNum, ACCOUNT_STATUS_PROPOSED));
        } else if (dtos.stream()
                       .filter(BulkArAccountDTO::isAccountStatusRejected)
                       .anyMatch(d -> d.getFullIdentifier().equals(accountIdCell))) {
            wrapper.getFileContentExceptions().add(wrapError(rowNum, ACCOUNT_STATUS_REJECTED));
        } else if (dtos.stream()
                       .filter(BulkArAccountDTO::isAccountStatusTransferPending)
                       .anyMatch(d -> d.getFullIdentifier().equals(accountIdCell))) {
            wrapper.getFileContentExceptions().add(wrapError(rowNum, ACCOUNT_STATUS_TRANSFER_PENDING));
        } else if (dtos.stream()
                       .filter(d -> AccountType.get(d.getRegistryAccountType(), d.getKyotoAccountType())
                                               .isGovernmentAccount())
                       .anyMatch(d -> d.getFullIdentifier().equals(accountIdCell))) {
            wrapper.getFileContentExceptions().add(wrapError(rowNum, ACCOUNT_ID_BELONGS_TO_GOV_ACC));
        }
    }

    private void validateDuplicates(int rowNum, BulkArContentValidationWrapper wrapper, String idCell) {
        if (!areIdsUnique(wrapper, idCell)) {
            wrapper.getFileContentExceptions().add(wrapError(rowNum, DUPLICATE_ID_ENTRIES));
        }
    }

    private void validateUserId(List<BulkArUserDTO> dtos, int rowNum, BulkArContentValidationWrapper wrapper, String userIdCell) {
        if (userIdCell.isBlank()) {
            wrapper.getFileContentExceptions().add(wrapError(rowNum, EMPTY_USER_ID));
        } else if (dtos.stream()
                    .noneMatch(d -> d.getUrid().equals(userIdCell))) {
            wrapper.getFileContentExceptions().add(wrapError(rowNum, USER_ID_NOT_IN_REGISTRY));
        } else if (dtos.stream()
                       .anyMatch(d -> d.getUrid().equals(userIdCell) && UserStatus.SUSPENDED.equals(d.getUserStatus()))) {
            wrapper.getFileContentExceptions().add(wrapError(rowNum, USER_ID_SUSPENDED));
        } else if (dtos.stream()
                      .anyMatch(d -> d.getUrid().equals(userIdCell) && UserStatus.DEACTIVATION_PENDING.equals(d.getUserStatus()))) {
            wrapper.getFileContentExceptions().add(wrapError(rowNum, USER_ID_DEACTIVATION_PENDING));
        } else if (dtos.stream()
                      .anyMatch(d -> d.getUrid().equals(userIdCell) && UserStatus.DEACTIVATED.equals(d.getUserStatus()))) {
            wrapper.getFileContentExceptions().add(wrapError(rowNum, USER_ID_DEACTIVATED));
        }
    }

    private void validateUserIsNotAlreadyAr(List<BulkArAccountAccessDTO> dtos, int rowNum,
                                            BulkArContentValidationWrapper wrapper, String userIdCell, String accountIdCell) {
        if (dtos.stream()
                .filter(d -> d.getAccessState().equals(AccountAccessState.ACTIVE))
                .anyMatch(d -> d.getUserUrid().equals(userIdCell) && d.getAccountIdentifier().equals(accountIdCell))) {
            wrapper.getFileContentExceptions().add(wrapError(rowNum, AR_ALREADY_ADDED));
        } else if (
            dtos.stream()
                .filter(d -> d.getAccessState().equals(AccountAccessState.REQUESTED))
                .anyMatch(d -> d.getUserUrid().equals(userIdCell) && d.getAccountIdentifier().equals(accountIdCell))) {
            wrapper.getFileContentExceptions().add(wrapError(rowNum, PENDING_ADD_AR_APPROVAL));
        }
    }

    private void validateAccountPermission(int rowNum, BulkArContentValidationWrapper wrapper, String permissionCell) {
        if (permissionCell.isBlank()) {
            wrapper.getFileContentExceptions().add(wrapError(rowNum, EMPTY_PERMISSIONS));
        } else if (Stream.of(AccountAccessRight.values())
                         .filter(not(AccountAccessRight.ROLE_BASED::equals))
                         .noneMatch(e -> e.equals(AccountAccessRight.parse(permissionCell)))) {
            wrapper.getFileContentExceptions().add(wrapError(rowNum, INVALID_PERMISSIONS));
        }
    }

    private void validatePendingAddArTasks(BulkArContentValidationWrapper wrapper, int rowNum, String accountIdCell,
                                           String userIdCell, List<BulkArTaskDTO> tasks) {
        if (tasks.stream().anyMatch(t -> RequestStateEnum.SUBMITTED_NOT_YET_APPROVED.equals(t.getTaskStatus()) &&
                                         userIdCell.equals(t.getUserUrid()) &&
                                         accountIdCell.equals(t.getAccountFullIdentifier()))) {
            wrapper.getFileContentExceptions().add(wrapError(rowNum, PENDING_ADD_AR_APPROVAL));
        }
    }

    private static boolean areIdsUnique(BulkArContentValidationWrapper wrapper, String cell) {
        return wrapper.getUniqueIdentifiers().add(cell);
    }

    private static String wrapError(int rowNum, BulkArError error) {
        return "Row " + rowNum + ": " + error.getMessage();
    }
}
