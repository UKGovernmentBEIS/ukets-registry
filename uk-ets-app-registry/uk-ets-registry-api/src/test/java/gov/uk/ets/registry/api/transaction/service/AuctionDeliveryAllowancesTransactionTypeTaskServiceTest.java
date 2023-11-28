package gov.uk.ets.registry.api.transaction.service;

import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TransactionTaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class AuctionDeliveryAllowancesTransactionTypeTaskServiceTest {

    @Mock
    private TransactionWithTaskService transactionWithTaskService;

    private AuctionDeliveryAllowancesTransactionTypeTaskService auctionDeliveryAllowancesTransactionTypeTaskService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        auctionDeliveryAllowancesTransactionTypeTaskService =
            new AuctionDeliveryAllowancesTransactionTypeTaskService(transactionWithTaskService);
    }

    @Test
    public void test_getDetails() {
        TaskDetailsDTO taskDetailsDTO = new TaskDetailsDTO();
        taskDetailsDTO.setTaskType(RequestType.TRANSACTION_REQUEST);
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.AuctionDeliveryAllowances);
        TransactionTaskDetailsDTO dto = new TransactionTaskDetailsDTO(taskDetailsDTO, transaction.getType(), transaction.getReference());

        Mockito.when(transactionWithTaskService.getTransactionTaskDetails(dto, transaction))
            .thenReturn(dto);

        TransactionTaskDetailsDTO details = auctionDeliveryAllowancesTransactionTypeTaskService.getDetails(dto, transaction);
        Assert.assertNotNull(details);
        Assert.assertEquals(RequestType.TRANSACTION_REQUEST, details.getTaskType());
    }
}
