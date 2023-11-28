package uk.gov.ets.transaction.log.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import uk.gov.ets.transaction.log.domain.type.TransactionStatus;

public class TransactionHistoryTest {

    @Test
    public void testEquals() {
        Transaction proposal_1 = new Transaction();
        proposal_1.setIdentifier("UK100053");
        
        TransactionHistory history_1 = new TransactionHistory();
        history_1.setId(1200L);
        history_1.setStatus(TransactionStatus.STL_CHECKED_NO_DISCREPANCY);
        history_1.setTransaction(proposal_1);
        
        TransactionHistory history_2 = new TransactionHistory();
        history_2.setId(1200L);
        history_2.setStatus(TransactionStatus.STL_CHECKED_NO_DISCREPANCY);
        history_2.setTransaction(proposal_1);
        
        TransactionHistory history_3 = new TransactionHistory();
        history_3.setId(1200L);
        history_3.setStatus(TransactionStatus.STL_CHECKED_NO_DISCREPANCY);
        history_3.setTransaction(new Transaction());
        
        TransactionHistory history_4 = new TransactionHistory();
        history_4.setId(1200L);
        history_4.setStatus(TransactionStatus.STL_CHECKED_DISCREPANCY);
        history_4.setTransaction(proposal_1);
        
        TransactionHistory history_5 = new TransactionHistory();
        history_4.setStatus(TransactionStatus.STL_CHECKED_DISCREPANCY);
        
        TransactionHistory history_6 = new TransactionHistory();
        history_6.setTransaction(proposal_1);
        
        assertEquals(history_1,history_2);
        
        assertNotEquals(history_1,history_3);
        assertNotEquals(history_2,history_3);
        assertNotEquals(history_1,history_4);
        assertNotEquals(history_1,history_5);
        assertNotEquals(history_1,history_6);
        assertNotEquals(history_1,new TransactionHistory());
        assertNotEquals(null,history_1);
    }
}
