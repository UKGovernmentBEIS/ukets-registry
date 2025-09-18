package gov.uk.ets.registry.api.allocation.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.allocation.data.AllocationOverview;
import gov.uk.ets.registry.api.allocation.data.AllocationSummary;
import gov.uk.ets.registry.api.allocation.domain.AllocationJob;
import gov.uk.ets.registry.api.allocation.type.AllocationJobStatus;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.TaskTransaction;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.transaction.domain.SearchableTransaction;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.repository.TransactionResponseRepository;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class AllocationReportGeneratorTest {

    @MockBean
    private AllocationJobService allocationJobService;
    @MockBean
    private TaskRepository taskRepository;
    @MockBean
    private TransactionResponseRepository transactionResponseRepository;
    @MockBean
    private Mapper mapper;

    private AllocationReportGenerator generator;

    @BeforeEach
    public void setup() {
        generator = new AllocationReportGenerator(allocationJobService, taskRepository, transactionResponseRepository, mapper);
    }

    @Test
    void test() throws IOException {
        // given
        AllocationJob allocationJob = new AllocationJob();
        allocationJob.setId(1L);
        allocationJob.setRequestIdentifier(111L);
        allocationJob.setStatus(AllocationJobStatus.COMPLETED);
        allocationJob.setUpdated(new Date());

        SearchableTransaction transaction = new SearchableTransaction();
        transaction.setIdentifier("transaction identifier");
        transaction.setType(TransactionType.AllocateAllowances);
        transaction.setQuantity(2L);
        transaction.setStarted(new Date());
        transaction.setLastUpdated(new Date());
        transaction.setStatus(TransactionStatus.PROPOSED);

        AccountHolder accountHolder = new AccountHolder();
        accountHolder.setName("AH name");
        Account acquiringAccount = new Account();
        acquiringAccount.setFullIdentifier("full Identifier");
        acquiringAccount.setAccountType("Account Type");
        acquiringAccount.setAccountName("Account Name");
        acquiringAccount.setAccountHolder(accountHolder);

        transaction.setAcquiringUkRegistryAccount(acquiringAccount);
        TaskTransaction taskTransaction = new TaskTransaction();
        taskTransaction.setTransaction(transaction);

        Task task = new Task();
        task.setRequestId(111L);
        task.setTransactionIdentifiers(List.of(taskTransaction));
        task.setDifference("difference");

        AllocationOverview overview = new AllocationOverview();
        overview.setBeneficiaryRecipients(List.of(new AllocationSummary()));

        Mockito.when(allocationJobService.getJob(1L)).thenReturn(allocationJob);
        Mockito.when(taskRepository.findByRequestId(111L)).thenReturn(task);
        Mockito.when(mapper.convertToPojo("difference", AllocationOverview.class)).thenReturn(overview);

        // when
        byte[] result = generator.generate(1L);

        // then
        Assertions.assertNotNull(result);

        InputStream is = new ByteArrayInputStream(result);
        Workbook book = WorkbookFactory.create(is);

        Assertions.assertEquals(1, book.getNumberOfSheets());

        Sheet data = book.getSheet("Data");

        Assertions.assertEquals("JOB ID: 1", data.getRow(0).getCell(0).getStringCellValue());
        Assertions.assertEquals("COMPLETED", data.getRow(5).getCell(1).getStringCellValue());
        Assertions.assertEquals(1, data.getRow(5).getCell(3).getNumericCellValue());
        Assertions.assertEquals(1, data.getRow(5).getCell(4).getNumericCellValue());
        Assertions.assertEquals(0, data.getRow(5).getCell(5).getNumericCellValue());
        Assertions.assertEquals("transaction identifier", data.getRow(9).getCell(0).getStringCellValue());
        Assertions.assertEquals(2, data.getRow(9).getCell(2).getNumericCellValue());
        Assertions.assertEquals("PROPOSED", data.getRow(9).getCell(9).getStringCellValue());
        Assertions.assertEquals("N/A", data.getRow(9).getCell(10).getStringCellValue());
    }
}
