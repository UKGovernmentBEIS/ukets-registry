import { createAction, props } from '@ngrx/store';
import { TrustedAccountList } from './trusted-account-list';
import { NavigationExtras, Params } from '@angular/router';

export const completeWizard = createAction(
  '[Trusted Account List Wizard] Complete',
  props<{
    complete: boolean;
  }>()
);
export const deleteTrustedAccountList = createAction(
  '[Trusted Account List Wizard] Delete trusted account list'
);
export const nextPage = createAction(
  '[Trusted Account List Wizard] — Continue',
  props<{ trustedAccountList: TrustedAccountList }>()
);

export const navigateTo = createAction(
  '[Trusted Account List Wizard] — Navigate to',
  props<{ route: string; extras?: NavigationExtras; queryParams?: Params }>()
);

export const navigateToNextPage = createAction(
  '[Trusted Account List Wizard] — Navigate to next page'
);
