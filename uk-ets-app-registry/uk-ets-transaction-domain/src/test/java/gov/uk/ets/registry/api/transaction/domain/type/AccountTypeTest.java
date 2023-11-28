package gov.uk.ets.registry.api.transaction.domain.type;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class AccountTypeTest {

  @Test
  public void getAllKyotoGovernmentTypes() {
    List<AccountType> allKyotoGovernmentAccountTypes = AccountType.getAllKyotoGovernmentTypes();
    assertTrue(allKyotoGovernmentAccountTypes.size() > 0);
    allKyotoGovernmentAccountTypes.forEach(kyotoGovernmentAccountType -> {
      assertTrue(kyotoGovernmentAccountType.getKyoto());
      assertNotNull(kyotoGovernmentAccountType.getKyotoType());
      assertTrue(kyotoGovernmentAccountType.getKyotoType().isGovernment());
    });
  }

  @Disabled("Enable this when registry government account types are added to the account types")
  @Test
  public void getAllRegistryGovernmentTypes() {
    List<AccountType> allRegistryGovernmentTypes = AccountType.getAllRegistryGovernmentTypes();
    assertTrue(allRegistryGovernmentTypes.size() > 0);
    allRegistryGovernmentTypes.forEach(registryGovernmentType -> {
      assertFalse(registryGovernmentType.getKyoto());
      assertNotNull(registryGovernmentType.getRegistryType());
      assertTrue(registryGovernmentType.getRegistryType().isGovernment());
    });
  }
}