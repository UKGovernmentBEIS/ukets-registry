package uk.gov.ets.transaction.log.messaging.types;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.JacksonUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.ets.transaction.log.domain.type.TransactionStatus;

class TransactionAnswerTest {

    @Test
    void testSerialization() {
      ObjectMapper mapper = JacksonUtils.enhancedObjectMapper();
      TransactionAnswer answer = TransactionAnswer.builder().
              transactionIdentifier("UK100058").
              transactionStatusCode(TransactionStatus.STL_CHECKED_NO_DISCREPANCY.getCode()).
              build();
      assertDoesNotThrow(()->mapper.writeValueAsString(answer));
    }
    
    @Test
    void testEquals() {
        TransactionAnswer transactionAnswer_1 = new TransactionAnswer();
        transactionAnswer_1.setTransactionIdentifier("UK100058");
        transactionAnswer_1.setTransactionStatusCode(TransactionStatus.STL_CHECKED_NO_DISCREPANCY.getCode());
        
        TransactionAnswer transactionAnswer_2 = new TransactionAnswer();
        transactionAnswer_2.setTransactionIdentifier("UK100058");
        transactionAnswer_2.setTransactionStatusCode(TransactionStatus.STL_CHECKED_NO_DISCREPANCY.getCode());
        
        TransactionAnswer transactionAnswer_3 = new TransactionAnswer();
        transactionAnswer_3.setTransactionIdentifier("UK100058");
        transactionAnswer_3.setTransactionStatusCode(TransactionStatus.STL_CHECKED_DISCREPANCY.getCode());
        
        TransactionAnswer transactionAnswer_4 = new TransactionAnswer();
        transactionAnswer_4.setTransactionIdentifier("UK2400099");
        transactionAnswer_4.setTransactionStatusCode(TransactionStatus.STL_CHECKED_NO_DISCREPANCY.getCode());
        
        TransactionAnswer transactionAnswer_5 = new TransactionAnswer();
        transactionAnswer_5.setTransactionIdentifier(null);
        transactionAnswer_5.setTransactionStatusCode(TransactionStatus.STL_CHECKED_NO_DISCREPANCY.getCode());
        
        TransactionAnswer transactionAnswer_6 = new TransactionAnswer();
        transactionAnswer_6.setTransactionIdentifier("UK2400099");
//        transactionAnswer_6.setTransactionStatusCode();
        
        TransactionAnswer transactionAnswer_7 = new TransactionAnswer();
        
        assertEquals(transactionAnswer_1,transactionAnswer_2);
        
        assertNotEquals(transactionAnswer_1,transactionAnswer_3);
        assertNotEquals(transactionAnswer_1,transactionAnswer_4);
        assertNotEquals(transactionAnswer_1,transactionAnswer_5);
        assertNotEquals(transactionAnswer_1,transactionAnswer_6);
        assertNotEquals(transactionAnswer_1,transactionAnswer_7);
        assertNotEquals(null,transactionAnswer_1);
        assertEquals(transactionAnswer_5,transactionAnswer_5);
        assertEquals(transactionAnswer_6,transactionAnswer_6);
        assertEquals(transactionAnswer_7,transactionAnswer_7);
    }
    
    @Test
    void test_toString() {
        TransactionAnswer transactionAnswer_4 = new TransactionAnswer("UK2400099",TransactionStatus.STL_CHECKED_NO_DISCREPANCY.getCode(), null);
        assertDoesNotThrow(()->transactionAnswer_4.toString());
        assertNotNull(transactionAnswer_4.toString());
        
        TransactionAnswer transactionAnswer_5 = new TransactionAnswer(null,TransactionStatus.STL_CHECKED_NO_DISCREPANCY.getCode(), null);
        assertDoesNotThrow(()->transactionAnswer_5.toString());
        assertNotNull(transactionAnswer_5.toString());
        
        TransactionAnswer transactionAnswer_6 = new TransactionAnswer("UK2400099",0, null);
        assertDoesNotThrow(()->transactionAnswer_6.toString());
        assertNotNull(transactionAnswer_6.toString());
        
        TransactionAnswer transactionAnswer_7 = new TransactionAnswer();
        assertDoesNotThrow(()->transactionAnswer_7.toString());
        assertNotNull(transactionAnswer_7.toString());
    }
}
