package uk.gov.ets.transaction.log.domain.type;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TransactionTypeTest {

    @Test
    void testHasDescription() {
        Stream.of(TransactionType.values()).forEach(t->Assertions.assertNotNull(t.getDescription()));
    }
    
    @Test
    void testHasSupplementaryCode() {
        Stream.of(TransactionType.values()).forEach(t->{
            System.out.println(t);
            Assertions.assertTrue(t.getSupplementaryCode() > 0);
            Assertions.assertTrue(t.getPrimaryCode() > 0);
        });
    }
    
}
