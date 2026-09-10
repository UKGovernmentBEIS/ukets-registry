import { inject } from '@angular/core';
import { CanDeactivateFn } from '@angular/router';
import { Store } from '@ngrx/store';
import { UserAgentActions } from '@user-agent/store';

export const clearUserAgentDetailsGuard: CanDeactivateFn<boolean> = () => {
  const store = inject(Store);
  store.dispatch(UserAgentActions.clearUserAgentDetails());
  return true;
};
