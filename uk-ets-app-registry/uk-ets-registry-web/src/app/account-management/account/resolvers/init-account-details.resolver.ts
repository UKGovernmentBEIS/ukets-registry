import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { Store } from '@ngrx/store';
import { catchError, filter, of, switchMap, take } from 'rxjs';
import { prepareNavigationToAccount } from '@account-management/account/account-details/account.actions';
import {
  areAccountDetailsLoaded,
  selectAccount,
} from '@account-management/account/account-details/account.selector';
import { Account } from '@shared/model/account';
import { ErrorSummary } from '@shared/error-summary';
import { errors } from '@shared/shared.action';

/**
 * Data resolver for account details.
 */
export const initAccountDetailsResolver: ResolveFn<boolean> = (
  route,
  state
) => {
  const store = inject(Store);
  const { accountId } = route.params;

  store.dispatch(prepareNavigationToAccount({ accountId }));

  return store.select(selectAccount).pipe(
    filter(
      (account): account is Account =>
        !!account && account.identifier?.toString() === accountId
    ),
    take(1),
    switchMap(() =>
      store.select(areAccountDetailsLoaded).pipe(
        filter((loaded) => loaded),
        take(1)
      )
    ),
    catchError((e) => {
      const summary: ErrorSummary = {
        errors: e?.error?.errors || [],
      };
      store.dispatch(errors({ errorSummary: summary }));
      return of(false);
    })
  );
};
