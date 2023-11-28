package gov.uk.ets.registry.api.transaction.domain.data;

import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class AccountSummaryTest {

    @Test
    public void testParsing() {
        Assert.assertNull(AccountSummary.parse(null, null, null));
        Assert.assertNull(AccountSummary.parse(null, RegistryAccountType.NONE, null));
        Assert.assertNull(AccountSummary.parse(null, null, AccountStatus.OPEN));
        Assert.assertNull(AccountSummary.parse(null, RegistryAccountType.NONE, AccountStatus.OPEN));
        AccountSummary accountSummary = AccountSummary.parse("GB-100-12345-0-22", RegistryAccountType.NONE, AccountStatus.OPEN);
        Assert.assertTrue(accountSummary.belongsToRegistry());
        Assert.assertEquals(AccountStatus.OPEN, accountSummary.getAccountStatus());
        Assert.assertEquals(RegistryAccountType.NONE, accountSummary.getRegistryAccountType());
        Assert.assertEquals(AccountType.PARTY_HOLDING_ACCOUNT, accountSummary.getType());
        Assert.assertEquals("GB", accountSummary.getRegistryCode());
        Assert.assertEquals(Integer.valueOf(0), accountSummary.getCommitmentPeriod());

        accountSummary = AccountSummary.parse("JP-121-12345-0-22", RegistryAccountType.NONE, AccountStatus.OPEN);
        Assert.assertEquals(AccountType.PERSON_HOLDING_ACCOUNT, accountSummary.getType());
        Assert.assertFalse(accountSummary.belongsToRegistry());

        AccountSummary accountSummary2 = AccountSummary.parse("JP-121-11111-0-22", RegistryAccountType.NONE, AccountStatus.OPEN);
        Assert.assertNotEquals(accountSummary, accountSummary2);

        accountSummary2.setIdentifier(12345L);
        Assert.assertEquals(accountSummary, accountSummary2);


    }

    @Test
    public void test() {
        Assert.assertEquals(Long.valueOf(12345), AccountSummary.parseIdentifier("JP-121-12345-0-22"));
        Assert.assertEquals(Long.valueOf(12345), AccountSummary.parseIdentifier("JP-121-12345-0"));
        Assert.assertEquals(Long.valueOf(12345), AccountSummary.parseIdentifier("JP-121-12345"));
        Assert.assertEquals(null, AccountSummary.parseIdentifier("JP-121"));
        Assert.assertEquals(null, AccountSummary.parseIdentifier("JP-121-XXX"));
        Assert.assertEquals(null, AccountSummary.parseIdentifier("JP121"));
    }

}