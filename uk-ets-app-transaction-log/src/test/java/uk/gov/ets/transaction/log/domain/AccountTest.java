package uk.gov.ets.transaction.log.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class AccountTest {

    @Test
    public void testEquals() {
        Account account_1 = new Account();
        account_1.setIdentifier(100053L);
        
        Account account_2 = new Account();
        account_2.setIdentifier(100053L);
        
        Account account_3 = new Account();
        account_3.setIdentifier(100946L);
        
        assertEquals(account_1,account_2);
        
        assertNotEquals(account_1,account_3);
        assertNotEquals(account_2,account_3);
    }
}
