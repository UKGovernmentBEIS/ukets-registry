import { createAction, props } from '@ngrx/store';
import {
  AccountStatusActionOption,
  AccountStatusActionState
} from '@shared/model/account/account-status-action';
import { AccountStatus } from '@shared/model/account';

export enum ChangeAccountStatusActionTypes {
  FETCH_LOAD_AND_SHOW_ALLOWED_ACCOUNT_STATUS_ACTIONS = '[Account Status] Fetch, load and show allowed change account status actions',
  FETCH_ALLOWED_ACCOUNT_STATUS_ACTIONS_SUCCESS = '[Account Status] Successful fetch of allowed change account status actions',
  LOAD_ALLOWED_ACCOUNT_STATUS_ACTIONS = '[Account Status] Load allowed change account status actions',
  SET_SELECTED_ACCOUNT_STATUS_ACTION = '[Account Status] Set selected account status action',
  SET_COMMENT_AND_SUBMIT_ACCOUNT_STATUS_ACTION = '[Account Status] Set comment & Submit Change Account Status',
  SET_COMMENT = '[Account Status] Set comment',
  SUBMIT_ACCOUNT_STATUS_ACTION = '[Account Status] Submit Change Account Status',
  SUBMIT_ACCOUNT_STATUS_ACTION_SUCCESS = '[Account Status] Submit Change Account Status Success',
  NAVIGATE_TO = '[Account Status] Navigate to',
  CANCEL_ACCOUNT_STATUS = '[Account Status] Cancel change account status',
  CLEAR_ACCOUNT_STATUS = '[Account Status] Clear change account status'
}

export const fetchLoadAndShowAllowedAccountStatusActions = createAction(
  ChangeAccountStatusActionTypes.FETCH_LOAD_AND_SHOW_ALLOWED_ACCOUNT_STATUS_ACTIONS
);

export const fetchAllowedAccountStatusActionsSuccess = createAction(
  ChangeAccountStatusActionTypes.FETCH_ALLOWED_ACCOUNT_STATUS_ACTIONS_SUCCESS,
  props<{ changeAccountStatusActionTypes: AccountStatusActionOption[] }>()
);

export const loadAllowedAccountStatusActions = createAction(
  ChangeAccountStatusActionTypes.LOAD_ALLOWED_ACCOUNT_STATUS_ACTIONS,
  props<{ changeAccountStatusActionTypes: AccountStatusActionOption[] }>()
);

export const setSelectedAccountStatusAction = createAction(
  ChangeAccountStatusActionTypes.SET_SELECTED_ACCOUNT_STATUS_ACTION,
  props<{ accountStatusAction: AccountStatusActionState }>()
);

export const setCommentAndSubmitAccountStatusAction = createAction(
  ChangeAccountStatusActionTypes.SET_COMMENT_AND_SUBMIT_ACCOUNT_STATUS_ACTION,
  props<{ comment: string }>()
);

export const setComment = createAction(
  ChangeAccountStatusActionTypes.SET_COMMENT,
  props<{ comment: string }>()
);

export const submitAccountStatusAction = createAction(
  ChangeAccountStatusActionTypes.SUBMIT_ACCOUNT_STATUS_ACTION,
  props<{
    accountId: string;
    newStatus: AccountStatus;
    comment: string;
  }>()
);

export const submitAccountStatusActionSuccess = createAction(
  ChangeAccountStatusActionTypes.SUBMIT_ACCOUNT_STATUS_ACTION_SUCCESS,
  props<{ newStatus: AccountStatus }>()
);

export const cancelAccountStatus = createAction(
  ChangeAccountStatusActionTypes.CANCEL_ACCOUNT_STATUS
);

export const clearAccountStatus = createAction(
  ChangeAccountStatusActionTypes.CLEAR_ACCOUNT_STATUS
);

export const navigateTo = createAction(
  ChangeAccountStatusActionTypes.NAVIGATE_TO,
  props<{ route: string }>()
);
