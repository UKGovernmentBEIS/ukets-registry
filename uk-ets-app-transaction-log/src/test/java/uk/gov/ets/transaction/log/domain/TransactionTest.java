package uk.gov.ets.transaction.log.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import uk.gov.ets.transaction.log.domain.type.TransactionType;
import uk.gov.ets.transaction.log.domain.type.UnitType;

public class TransactionTest {

    @Test
    public void testEquals() {
        Transaction proposal_1 = new Transaction();
        proposal_1.setIdentifier("UK100053");
        proposal_1.setType(TransactionType.TransferAllowances);
        proposal_1.setUnitType(UnitType.ALLOWANCE);
        
        Transaction proposal_2 = new Transaction();
        proposal_2.setIdentifier("UK100053");
        proposal_2.setType(TransactionType.TransferAllowances);
        proposal_2.setUnitType(UnitType.ALLOWANCE);
        
        Transaction proposal_3 = new Transaction();
        proposal_3.setIdentifier("UK100073");
        proposal_3.setType(TransactionType.TransferAllowances);
        proposal_3.setUnitType(UnitType.ALLOWANCE);
        
        assertEquals(proposal_1,proposal_2);
        
        assertNotEquals(proposal_1,proposal_3);
        assertNotEquals(proposal_2,proposal_3);
    }
}
