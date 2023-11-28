package gov.uk.ets.registry.api.transaction.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountProjectionTest {
    @Test
    void isGovernmentAccount() {
        assertFalse(retrieveAccountProjection(AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT));
        assertFalse(retrieveAccountProjection(AccountType.PERSON_HOLDING_ACCOUNT));
        assertFalse(retrieveAccountProjection(AccountType.OPERATOR_HOLDING_ACCOUNT));
        assertFalse(retrieveAccountProjection(AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT));
        assertFalse(retrieveAccountProjection(AccountType.TRADING_ACCOUNT));

        Stream.of(AccountType.values()).forEach(accountType -> {
            if (accountType.isGovernmentAccount()) {
                assertTrue(retrieveAccountProjection(accountType));
            }
        });
    }

    private boolean retrieveAccountProjection(AccountType accountType) {
        return new AccountProjection("", "", accountType.getKyotoType(),
            accountType.getRegistryType(), "", 101L, "", AccountStatus.OPEN).isGovernmentAccount();
    }
}
