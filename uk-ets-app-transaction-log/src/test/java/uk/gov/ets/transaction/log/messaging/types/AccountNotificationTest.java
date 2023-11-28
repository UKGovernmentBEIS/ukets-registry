package uk.gov.ets.transaction.log.messaging.types;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;

import org.junit.jupiter.api.Test;

public class AccountNotificationTest {

    @Test
    public void testEquals() {
        AccountNotification accountNotification_1 = new AccountNotification();
        accountNotification_1.setIdentifier(100058L);
        
        AccountNotification accountNotification_2 = new AccountNotification();
        accountNotification_2.setIdentifier(100058L);
        
        AccountNotification accountNotification_3 = new AccountNotification();
        accountNotification_3.setIdentifier(109633L);
        
        AccountNotification accountNotification_4 = new AccountNotification();
        accountNotification_4.setIdentifier(null);
        
        assertEquals(accountNotification_1,accountNotification_2);
        
        assertNotEquals(accountNotification_1,accountNotification_3);
        assertNotEquals(accountNotification_1,accountNotification_4);
        assertNotEquals(null,accountNotification_1);
    }
    
    @Test
    public void test_toString() {
        AccountNotification AccountNotification_1 = AccountNotification.
                builder().
                accountName("accountName").
                checkDigits(768).
                commitmentPeriodCode(2).
                fullIdentifier("GB-111-111").
                identifier(889L).
                openingDate(new Date()).
                build();
        assertDoesNotThrow(()->AccountNotification_1.toString());
        assertNotNull(AccountNotification_1.toString());
        
        AccountNotification AccountNotification_2 = new AccountNotification();
        assertDoesNotThrow(()->AccountNotification_2.toString());
        assertNotNull(AccountNotification_2.toString());
        
    }
}
