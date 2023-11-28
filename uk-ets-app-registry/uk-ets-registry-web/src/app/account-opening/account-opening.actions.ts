import { Action, createAction, props } from '@ngrx/store';
import { RequestType } from '@registry-web/task-management/model';
import { ErrorSummary } from '@shared/error-summary';
import { AccountType } from '@shared/model/account';

export enum AccountOpeningActionTypes {
  SET_ACCOUNT_TYPE = '[Main Wizard] Set account type',
  CANCEL_REQUEST = '[Main Wizard] Cancel request',
  CREATE_ACCOUNT = '[Main Wizard] Create account',
  COMPLETE_REQUEST = '[Main Wizard] Complete request',
  SAME_ADDRESS = '[Main Wizard] Same Address',
  FETCH_SUMMARY_FILE = '[Main Wizard] - Fetch account opening summary file',
}

export const setAccountType = createAction(
  AccountOpeningActionTypes.SET_ACCOUNT_TYPE,
  props<{
    accountType: AccountType;
    minNumberOfARs: number;
    maxNumberOfARs: number;
  }>()
);

export const createAccount = createAction(
  AccountOpeningActionTypes.CREATE_ACCOUNT
);

export const cancelRequest = createAction(
  AccountOpeningActionTypes.CANCEL_REQUEST
);

export const completeRequest = createAction(
  AccountOpeningActionTypes.COMPLETE_REQUEST,
  props<{
    requestID: number;
    taskType: RequestType;
  }>()
);

export class CreateAccount implements Action {
  readonly type = AccountOpeningActionTypes.CREATE_ACCOUNT;
  constructor(readonly errorSummary: ErrorSummary) {}
}

export const accountDetailsSameBillingAddress = createAction(
  AccountOpeningActionTypes.SAME_ADDRESS,
  props<{ accountDetailsSameBillingAddress: boolean }>()
);

export const fetchAccountOpeningSummaryFile = createAction(
  AccountOpeningActionTypes.FETCH_SUMMARY_FILE
);
