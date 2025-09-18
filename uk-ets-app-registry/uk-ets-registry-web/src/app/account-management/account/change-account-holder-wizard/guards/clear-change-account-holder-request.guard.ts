import { inject } from '@angular/core';
import { CanDeactivateFn } from '@angular/router';
import { ChangeAccountHolderWizardActions } from '@change-account-holder-wizard/store/actions';
import { Store } from '@ngrx/store';

export const clearChangeAccountHolderRequestGuard: CanDeactivateFn<unknown> = (
  component,
  currentRoute,
  currentState,
  nextState
) => {
  const store = inject(Store);
  store.dispatch(
    ChangeAccountHolderWizardActions.clearAccountHolderChangeRequest()
  );
  return true;
};
