package gov.uk.ets.registry.api.account.web.mappers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.shared.AccountProjection;
import org.junit.jupiter.api.Test;

class AccountHolderNameMapperTest {

    private AccountHolderNameFromProjectionMapper mapper = new AccountHolderNameFromProjectionMapper();

    @Test
    void mapAccountHolderWithoutFirstLastNames() {
        // given
        String expectedName = "John Adam";
        AccountProjection accountProjection = mock(AccountProjection.class);
        given(accountProjection.getAccountHolderName()).willReturn(expectedName);

        // when
        String returnedName = mapper.map(accountProjection);

        // then
        assertEquals(expectedName, returnedName);

        // given
        given(accountProjection.getAccountHolderFirstName()).willReturn("firstName");

        // when
        returnedName = mapper.map(accountProjection);

        // then
        assertEquals(expectedName, returnedName);

        // given
        given(accountProjection.getAccountHolderFirstName()).willReturn(null);
        given(accountProjection.getAccountHolderLastName()).willReturn("lastName");

        // when
        returnedName = mapper.map(accountProjection);

        // then
        assertEquals(expectedName, returnedName);
    }

    @Test
    void mapAccountHolderWithFirstLastNames() {
        // given
        String expectedFirstName = "John";
        String expectedLastName = "Adam";
        AccountProjection accountProjection = mock(AccountProjection.class);
        given(accountProjection.getAccountHolderFirstName()).willReturn(expectedFirstName);
        given(accountProjection.getAccountHolderLastName()).willReturn(expectedLastName);

        // when
        String returnedName = mapper.map(accountProjection);

        // then
        assertEquals(expectedFirstName + " " + expectedLastName, returnedName);
    }
}