import { inject } from '@angular/core';
import { CanDeactivateFn } from '@angular/router';
import { Store } from '@ngrx/store';
import { BulkClaimAccountActions } from '@bulk-claim-account/store/actions';

export const clearBulkClaimAccountGuard: CanDeactivateFn<unknown> = (
  component,
  currentRoute,
  currentState,
  nextState
) => {
  const store = inject(Store);
  store.dispatch(BulkClaimAccountActions.clearBulkClaimAccountRequest());
  return true;
};
