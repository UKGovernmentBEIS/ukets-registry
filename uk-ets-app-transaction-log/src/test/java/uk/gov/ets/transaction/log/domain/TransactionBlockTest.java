package uk.gov.ets.transaction.log.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class TransactionBlockTest {

    @Test
    public void testEquals() {
        TransactionBlock transactionBlock_1 = new TransactionBlock();
        transactionBlock_1.setStartBlock(1200L);
        transactionBlock_1.setEndBlock(1250L);
        
        TransactionBlock transactionBlock_2 = new TransactionBlock();
        transactionBlock_2.setStartBlock(1200L);
        transactionBlock_2.setEndBlock(1250L);
        
        TransactionBlock transactionBlock_3 = new TransactionBlock();
        transactionBlock_3.setStartBlock(400L);
        transactionBlock_3.setEndBlock(500L);
        
        assertEquals(transactionBlock_1,transactionBlock_2);
        
        assertNotEquals(transactionBlock_1,transactionBlock_3);
        assertNotEquals(transactionBlock_2,transactionBlock_3);
    }
}
