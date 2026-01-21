import { inject } from '@angular/core';
import {
  CanActivateFn,
  CanDeactivateFn,
  createUrlTreeFromSnapshot,
} from '@angular/router';
import { Store } from '@ngrx/store';
import {
  ClaimAccountActions,
  selectSubmittedRequestIdentifier,
} from '@claim-account/store';
import { map } from 'rxjs';
import { MENU_SCOPES } from '@registry-web/shared/model/navigation-menu';
import { AuthApiService } from '@registry-web/auth/auth-api.service';

export const clearClaimAccountGuard: CanDeactivateFn<unknown> = () => {
  inject(Store).dispatch(ClaimAccountActions.CLEAR_REQUEST());
  return true;
};

export const canActivateClaimAccount: CanActivateFn = (route) => {
  const canClaimAccount$ = inject(AuthApiService).hasScope(
    MENU_SCOPES.CLAIM_ACCOUNT
  );

  return canClaimAccount$.pipe(
    map(
      (canClaimAccount) =>
        canClaimAccount || createUrlTreeFromSnapshot(route, ['../account-list'])
    )
  );
};

export const canActivateClaimAccountRequestSubmitted: CanActivateFn = (
  route
) => {
  const requestIdentifier$ = inject(Store).select(
    selectSubmittedRequestIdentifier
  );
  return requestIdentifier$.pipe(
    map(
      (requestIdentifier) =>
        !!requestIdentifier || createUrlTreeFromSnapshot(route, ['../'])
    )
  );
};
