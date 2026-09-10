import { CanDeactivateFn } from '@angular/router';
import { UserCrcActions } from '../store';
import { inject } from '@angular/core';
import { Store } from '@ngrx/store';

export const clearUserCrcDetailsGuard: CanDeactivateFn<boolean> = (
  component,
  currentRoute,
  currentState,
  nextState
) => {
  const store = inject(Store);
  store.dispatch(UserCrcActions.clearUserCrcDetails());
  return true;
};
