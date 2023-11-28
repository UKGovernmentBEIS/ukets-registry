import { createAction, props } from '@ngrx/store';
import { AccountDetails } from '../../shared/model/account/account-details';

export const completeWizard = createAction(
  '[Account Details Wizard] Complete',
  props<{
    complete: boolean;
  }>()
);
export const deleteAccountDetails = createAction(
  '[Account Details Wizard] Delete account details'
);
export const nextPage = createAction(
  '[Account Details Wizard] — Continue',
  props<{ accountDetails: AccountDetails }>()
);
export const sameAddress = createAction(
  '[Account Details Wizard] — Same address',
  props<{ sameAddress: boolean }>()
);
