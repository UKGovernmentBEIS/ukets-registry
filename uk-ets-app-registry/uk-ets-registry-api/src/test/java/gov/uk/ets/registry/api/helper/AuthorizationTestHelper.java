package gov.uk.ets.registry.api.helper;

import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.Scope;
import org.mockito.Mockito;

public class AuthorizationTestHelper {

  AuthorizationService authorizationService;

  public AuthorizationTestHelper(AuthorizationService authorizationService) {
    this.authorizationService = authorizationService;
  }

  public void mockAuthAsAdmin() {
    Mockito.when(authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN)).thenReturn(true);
    Mockito.when(authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ENROLLED_NON_ADMIN)).thenReturn(false);
  }

  public void mockAuthAsNonAdmin() {
    Mockito.when(authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN)).thenReturn(false);
    Mockito.when(authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ENROLLED_NON_ADMIN)).thenReturn(true);
  }
}
