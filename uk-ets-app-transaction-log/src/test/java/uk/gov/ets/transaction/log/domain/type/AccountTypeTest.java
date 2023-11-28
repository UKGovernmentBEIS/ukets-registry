package uk.gov.ets.transaction.log.domain.type;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AccountTypeTest {

    @Test
    public void testNonKyotoOnlyTransactions() {
        Stream.of(AccountType.values()).forEach(t->Assertions.assertTrue(KyotoAccountType.PARTY_HOLDING_ACCOUNT.equals(t.getKyotoType())));
    }
    
}
