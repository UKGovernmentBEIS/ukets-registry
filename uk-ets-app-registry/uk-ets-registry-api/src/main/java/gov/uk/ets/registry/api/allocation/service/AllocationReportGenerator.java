package gov.uk.ets.registry.api.allocation.service;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.dhatim.fastexcel.Color.GRAY9;
import static org.dhatim.fastexcel.Color.WHITE;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.allocation.data.AllocationOverview;
import gov.uk.ets.registry.api.allocation.domain.AllocationJob;
import gov.uk.ets.registry.api.allocation.domain.AllocationJobError;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.error.UkEtsException;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.TaskTransaction;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.transaction.domain.BaseTransactionEntity;
import gov.uk.ets.registry.api.transaction.domain.SearchableTransaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionResponse;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.repository.TransactionResponseRepository;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.dhatim.fastexcel.BorderStyle;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class AllocationReportGenerator {

    private final AllocationJobService allocationJobService;
    private final TaskRepository taskRepository;
    private final TransactionResponseRepository transactionResponseRepository;
    private final Mapper mapper;

    public byte[] generate(Long allocationJobId) {

        AllocationJob allocationJob = allocationJobService.getJob(allocationJobId);

        Task task = taskRepository.findByRequestId(allocationJob.getRequestIdentifier());
        Long requestId = task.getRequestId();

        List<TaskTransaction> taskTransactions = task.getTransactionIdentifiers();
        AllocationOverview allocationOverview = mapper.convertToPojo(task.getDifference(), AllocationOverview.class);

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            Workbook wb = new Workbook(stream, "uk-ets", "1.0");
            Worksheet ws = wb.newWorksheet("Data");

            setCommonFields(requestId, allocationJob, ws);
            setOverallFields(allocationJob, taskTransactions, allocationOverview.getBeneficiaryRecipients().size(), ws);

            if (!taskTransactions.isEmpty()) {
                writeValue(ws, "In details", 7, 0);
                ws.style(7, 0).bold().set();

                setTransactionTable(taskTransactions, ws);
            }

            wb.finish();
            return stream.toByteArray();
        } catch (IOException e) {
            throw new UkEtsException(e);
        }
    }

    private void setCommonFields(Long requestId, AllocationJob allocationJob, Worksheet ws) {
        writeValue(ws, "JOB ID: " + allocationJob.getId(), 0, 0);
        ws.style(0, 0).bold().set();
        writeValue(ws, "TASK ID: " + requestId, 1, 0);
        ws.style(1, 0).bold().set();
        writeValue(ws, "Overall", 3, 0);
        ws.style(3, 0).bold().set();
    }

    private void setOverallFields(AllocationJob allocationJob, List<TaskTransaction> taskTransactions, int totalTransactions, Worksheet ws) {

        List<String> overallHeaders = List.of("Executed Timestamp", "Job Status", "Job Failure reason", "Total Transactions",
            "Completed Transactions", "Failed Transactions");
        setRow(ws, overallHeaders, 4);
        for (int i=0; i<overallHeaders.size(); i++) {
            ws.style(4, i).bold().set();
        }

        long failedTransactions =
            taskTransactions.stream().map(TaskTransaction::getTransaction).map(BaseTransactionEntity::getStatus)
                .filter(status -> status == TransactionStatus.FAILED).count();
        long completedTransactions = taskTransactions.size() - failedTransactions;
        String failureReason = Optional.ofNullable(allocationJob.getErrors())
            .map(errors -> errors.stream().map(AllocationJobError::getDetails).collect(Collectors.joining(";")))
            .orElse("N/A");

        List<Object> list = List.of(allocationJob.getUpdated(), allocationJob.getStatus(), failureReason, totalTransactions,
            completedTransactions, failedTransactions);
        setRow(ws, list, 5);
    }

    private void setTransactionTable(List<TaskTransaction> taskTransactions, Worksheet ws) {
        List<String> transactionHeaders = List.of("Transaction ID", "Transaction type", "Unit Quantity", "Acquiring account ID",
            "Acquiring account type", "Acquiring account name", "Acquiring account holder", "Transaction start (UTC)",
            "Last updated on (UTC)", "Transaction status", "Failure reasons");
        setRow(ws, transactionHeaders, 8);
        for (int i=0; i<transactionHeaders.size(); i++) {
            ws.style(8, i).fillColor(GRAY9).fontColor(WHITE).bold().set();
        }

        for (int i=0; i<taskTransactions.size(); i++) {
            SearchableTransaction transaction = taskTransactions.get(i).getTransaction();
            Account acquiringAccount = transaction.getAcquiringUkRegistryAccount();
            String failureReason = Optional.of(transaction)
                .filter(trans -> trans.getStatus() != TransactionStatus.COMPLETED)
                .map(trans -> transactionResponseRepository.findByTransactionId(trans.getId()))
                .map(TransactionResponse::getDetails)
                .orElse("N/A");

            List<Object> transactionDetails =
                List.of(transaction.getIdentifier(), transaction.getType(), transaction.getQuantity(),
                    acquiringAccount.getFullIdentifier(), acquiringAccount.getAccountType(),
                    acquiringAccount.getAccountName(), acquiringAccount.getAccountHolder().getName(),
                    transaction.getStarted(), transaction.getLastUpdated(), transaction.getStatus(), failureReason);
            setRow(ws, transactionDetails, 9+i);
        }
    }

    private void setRow(Worksheet ws, List<?> objects, int row) {
        for (int i=0; i< objects.size(); i++) {
            writeValue(ws, objects.get(i), row, i);
            ws.style(row, i).borderStyle(BorderStyle.THIN).set();
        }
    }

    private void writeValue(Worksheet ws, Object value, int row, int column) {
        if (value == null) {
            ws.value(row, column, "");
        } else if (value instanceof String) {
            ws.value(row, column, defaultIfBlank((String) value, ""));
        } else if (value instanceof Enum) {
            ws.value(row, column, ((Enum) value).name());
        } else if (value instanceof Number) {
            ws.value(row, column, (Number) value);
        } else if (value instanceof Date) {
            ws.value(row, column, (Date) value);
            ws.style(row, column).format("yyyy-MM-dd H:mm:ss").set();
        } else {
            throw new RuntimeException(String.format("data type not supported :%s", value.getClass()));
        }
    }
}
