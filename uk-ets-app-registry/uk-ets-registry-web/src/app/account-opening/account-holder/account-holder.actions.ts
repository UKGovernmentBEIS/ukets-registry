import { Action, createAction, props } from '@ngrx/store';
import { ErrorSummary } from '@shared/error-summary';
import {
  AccountHolder,
  AccountHolderSelectionType,
  AccountHolderType,
} from '../../shared/model/account/account-holder';
import { AccountHolderContactInfo } from '@shared/model/account';

export enum AccountHolderActionTypes {
  COMPLETE_ACCOUNT_HOLDER = '[Account Holder Wizard] Complete',
  DELETE_ACCOUNT_HOLDER = '[Account Holder Wizard] Delete account holder',
  SET_ACCOUNT_HOLDER_TYPE = '[Account Holder Wizard] - Set account holder type',
  SET_ACCOUNT_HOLDER_SELECTION_TYPE = '[Account Holder Wizard] - Set account holder selection type',
  ACCOUNT_HOLDER_NEXT_PAGE = '[Account Holder Wizard] - Continue',
  FETCH_ACCOUNT_HOLDER_LIST = '[Account Holder Wizard] - Fetch list',
  POPULATE_ACCOUNT_HOLDER_CONTACT_INFO = '[Account Holder Wizard] - Populate primary and alternative contacts',
  POPULATE_ACCOUNT_HOLDER_LIST = '[Account Holder Wizard] - Populate list',
  POPULATE_ACCOUNT_HOLDER = '[Account Holder Wizard] - Populate account holder',
  CLEAN_ACCOUNT_HOLDER_LIST = '[Account Holder Wizard] - Clean list',
  FETCH_ACCOUNT_HOLDER_BY_ID = '[Account Holder Wizard] - Fetch by ID',
}

export const completeWizard = createAction(
  AccountHolderActionTypes.COMPLETE_ACCOUNT_HOLDER,
  props<{
    complete: boolean;
  }>()
);
export const deleteAccountHolder = createAction(
  AccountHolderActionTypes.DELETE_ACCOUNT_HOLDER
);
export const setAccountHolderType = createAction(
  AccountHolderActionTypes.SET_ACCOUNT_HOLDER_TYPE,
  props<{ holderType: AccountHolderType }>()
);
export const setAccountHolderSelectionType = createAction(
  AccountHolderActionTypes.SET_ACCOUNT_HOLDER_SELECTION_TYPE,
  props<{ selectionType: AccountHolderSelectionType }>()
);
export const nextPage = createAction(
  AccountHolderActionTypes.ACCOUNT_HOLDER_NEXT_PAGE,
  props<{ accountHolder: AccountHolder }>()
);
export const fetchAccountHolderList = createAction(
  AccountHolderActionTypes.FETCH_ACCOUNT_HOLDER_LIST,
  props<{ holderType: string }>()
);
export const populateAccountHolderList = createAction(
  AccountHolderActionTypes.POPULATE_ACCOUNT_HOLDER_LIST,
  props<{ list: AccountHolder[] }>()
);
export const populateAccountHolderContactInfo = createAction(
  AccountHolderActionTypes.POPULATE_ACCOUNT_HOLDER_CONTACT_INFO,
  props<{ accountHolderContactInfo: AccountHolderContactInfo }>()
);
export const populateAccountHolder = createAction(
  AccountHolderActionTypes.POPULATE_ACCOUNT_HOLDER,
  props<{ accountHolder: AccountHolder }>()
);
export const cleanAccountHolderList = createAction(
  AccountHolderActionTypes.CLEAN_ACCOUNT_HOLDER_LIST
);
export const fetchAccountHolderById = createAction(
  AccountHolderActionTypes.FETCH_ACCOUNT_HOLDER_BY_ID,
  props<{ id: number }>()
);

export class FetchList implements Action {
  readonly type = AccountHolderActionTypes.FETCH_ACCOUNT_HOLDER_LIST;

  constructor(
    readonly errorSummary: ErrorSummary,
    readonly urid: string,
    readonly holderType: string
  ) {}
}

export class FetchByID implements Action {
  readonly type = AccountHolderActionTypes.FETCH_ACCOUNT_HOLDER_BY_ID;

  constructor(readonly errorSummary: ErrorSummary, readonly id: number) {}
}
