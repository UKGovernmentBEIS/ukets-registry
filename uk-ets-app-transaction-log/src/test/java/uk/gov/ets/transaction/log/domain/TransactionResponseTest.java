package uk.gov.ets.transaction.log.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Date;

import org.junit.jupiter.api.Test;

public class TransactionResponseTest {

    @Test
    public void testEquals() {
        Date dateOccurred = new Date();
        Transaction proposal_1 = new Transaction();
        proposal_1.setIdentifier("UK100053");
        
        TransactionResponse response_1 = new TransactionResponse();
        response_1.setErrorCode(1200L);
        response_1.setTransactionBlockId(1250L);
        response_1.setTransaction(proposal_1);
        response_1.setDateOccurred(dateOccurred);
        
        TransactionResponse response_2 = new TransactionResponse();
        response_2.setErrorCode(1200L);
        response_2.setTransactionBlockId(1250L);
        response_2.setTransaction(proposal_1);
        response_2.setDateOccurred(dateOccurred);
        
        TransactionResponse response_3 = new TransactionResponse();
        response_3.setErrorCode(1299L);
        response_3.setTransactionBlockId(1250L);
        response_3.setTransaction(proposal_1);
        response_3.setDateOccurred(dateOccurred);
        
        TransactionResponse response_4 = new TransactionResponse();
        response_4.setErrorCode(1200L);
        response_4.setTransactionBlockId(420L);
        response_4.setTransaction(proposal_1);
        response_4.setDateOccurred(dateOccurred);
        
        Transaction proposal_2 = new Transaction();
        proposal_2.setIdentifier("UK100821");
        TransactionResponse response_5 = new TransactionResponse();
        response_5.setErrorCode(1200L);
        response_5.setTransactionBlockId(1250L);
        response_5.setTransaction(proposal_2);
        response_5.setDateOccurred(dateOccurred);
        
        TransactionResponse response_6 = new TransactionResponse();
        response_6.setErrorCode(1200L);
        response_6.setTransactionBlockId(1250L);
        response_6.setTransaction(proposal_1);
        response_6.setDateOccurred(new Date(99999999999L));
        
        assertEquals(response_1,response_2);
        
        assertNotEquals(response_1,response_3);
        assertNotEquals(response_1,response_4);
        assertNotEquals(response_1,response_5);
        assertNotEquals(response_1,response_6);
    }
}
