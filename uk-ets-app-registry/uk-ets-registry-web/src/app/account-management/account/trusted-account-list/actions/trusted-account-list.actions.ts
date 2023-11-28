import { createAction, props } from '@ngrx/store';
import { NavigationExtras, Params } from '@angular/router';
import { TrustedAccountListUpdateType } from '@trusted-account-list/model';
import { TrustedAccount, UserDefinedAccountParts } from '@shared/model/account';
import { DescriptionUpdateActionState } from '@account-management/account/trusted-account-list/reducers/trusted-account-list.reducer';

export enum TrustedAccountListActionTypes {
  NAVIGATE_TO = '[Trusted Account List] Navigate to',
  SET_REQUEST_UPDATE_TYPE = '[Trusted Account List] Set request update type',
  FETCH_TRUSTED_ACCOUNTS_TO_REMOVE = '[Trusted Account List] Fetch trusted accounts eligible for removal',
  FETCH_TRUSTED_ACCOUNTS_TO_REMOVE_SUCCESS = '[Trusted Account List] Fetch trusted accounts eligible for removal successful',
  SELECT_TRUSTED_ACCOUNTS_FOR_REMOVAL = '[Trusted Account List] Select trusted accounts for removal',
  SELECT_TRUSTED_ACCOUNTS_FOR_REMOVAL_SUCCESS = '[Trusted Account List] Select trusted accounts for removal successful',
  CANCEL_CLICKED = '[Trusted Account List] Cancel clicked',
  CANCEL_TRUSTED_ACCOUNT_LIST_UPDATE_REQUEST = '[Trusted Account List] Cancel update request',
  CLEAR_TRUSTED_ACCOUNT_LIST_UPDATE_REQUEST = '[Trusted Account List] Clear update request',
  SET_USER_DEFINED_TRUSTED_ACCOUNT = '[Trusted Account List] Set User Defined Trusted Account',
  SET_USER_DEFINED_TRUSTED_ACCOUNT_SUCCESS = '[Trusted Account List] Set User Defined Trusted Account Success',
  SET_USER_DEFINED_TRUSTED_ACCOUNT_ERROR = '[Trusted Account List] Set User Defined Trusted Account error',
  SUBMIT_UPDATE_REQUEST = '[Trusted Account List] Submit update request',
  SUBMIT_UPDATE_REQUEST_ADD = '[Trusted Account List] Submit update request to add',
  SUBMIT_UPDATE_REQUEST_REMOVE = '[Trusted Account List] Submit update request to remove',
  SUBMIT_UPDATE_REQUEST_SUCCESS = '[Trusted Account List] Submit update request success',
  SET_DESCRIPTION_AND_NAVIGATE = '[Trusted Account List] Set description action & navigate to confirm',
  SET_DESCRIPTION_ACTION = '[Trusted Account List] Set description action',
  SET_DESCRIPTION = '[Trusted Account List] Set description',
  SUBMIT_CHANGE_DESCRIPTION_ACTION = '[Trusted Account List] Submit change description action',
  SUBMIT_CHANGE_DESCRIPTION_ACTION_SUCCESS = '[Trusted Account List] Submit change description action Success',
  CANCEL_CHANGE_ACCOUNT_DESCRIPTION = '[Trusted Account List] Cancel change Account description',
  CLEAR_CHANGE_ACCOUNT_DESCRIPTION = '[Trusted Account List] Clear change Account description',
  TRUSTED_ACCOUNT_FULL_IDENTIFIER_DESCRIPTION_UPDATE_SUCCESS = '[Trusted Account List] Load account details of trusted account being changed with success',
  CANCEL_PENDING_ACTIVATION = '[Trusted Account List] Cancel Pending Activation Requested',
  CANCEL_PENDING_ACTIVATION_SUCCESS = '[Trusted Account List] Cancel Pending Activation Requested SUCCESS',
  CANCEL_PENDING_ACTIVATION_FAILURE = '[Trusted Account List] Cancel Pending Activation Requested FAILURE',
  HIDE_CANCEL_PENDING_ACTIVATION_SUCCESS_BANNER = '[Trusted Account List] Hide cancel pending activation success banner',
}

export const navigateTo = createAction(
  TrustedAccountListActionTypes.NAVIGATE_TO,
  props<{ route: string; extras?: NavigationExtras; queryParams?: Params }>()
);

export const setRequestUpdateType = createAction(
  TrustedAccountListActionTypes.SET_REQUEST_UPDATE_TYPE,
  props<{ updateType: TrustedAccountListUpdateType }>()
);

export const fetchTrustedAccountsToRemove = createAction(
  TrustedAccountListActionTypes.FETCH_TRUSTED_ACCOUNTS_TO_REMOVE,
  props<{ accountId: string }>()
);

export const fetchTrustedAccountsToRemoveSuccess = createAction(
  TrustedAccountListActionTypes.FETCH_TRUSTED_ACCOUNTS_TO_REMOVE_SUCCESS,
  props<{ trustedAccounts: TrustedAccount[] }>()
);

export const selectTrustedAccountsForRemoval = createAction(
  TrustedAccountListActionTypes.SELECT_TRUSTED_ACCOUNTS_FOR_REMOVAL,
  props<{ trustedAccountForRemoval: TrustedAccount[] }>()
);

export const selectTrustedAccountsForRemovalSuccess = createAction(
  TrustedAccountListActionTypes.SELECT_TRUSTED_ACCOUNTS_FOR_REMOVAL_SUCCESS,
  props<{ trustedAccountForRemoval: TrustedAccount[] }>()
);

export const cancelClicked = createAction(
  TrustedAccountListActionTypes.CANCEL_CLICKED,
  props<{ route: string }>()
);

export const cancelTrustedAccountListUpdateRequest = createAction(
  TrustedAccountListActionTypes.CANCEL_TRUSTED_ACCOUNT_LIST_UPDATE_REQUEST
);

export const clearTrustedAccountListUpdateRequest = createAction(
  TrustedAccountListActionTypes.CLEAR_TRUSTED_ACCOUNT_LIST_UPDATE_REQUEST
);

export const setUserDefinedTrustedAccount = createAction(
  TrustedAccountListActionTypes.SET_USER_DEFINED_TRUSTED_ACCOUNT,
  props<{
    userDefinedTrustedAccountParts: UserDefinedAccountParts;
    userDefinedTrustedAccountDescription: string;
  }>()
);

export const setUserDefinedTrustedAccountSuccess = createAction(
  TrustedAccountListActionTypes.SET_USER_DEFINED_TRUSTED_ACCOUNT_SUCCESS,
  props<{ kyotoAccountType: boolean }>()
);

export const submitUpdateRequest = createAction(
  TrustedAccountListActionTypes.SUBMIT_UPDATE_REQUEST
);

export const submitUpdateRequestToAdd = createAction(
  TrustedAccountListActionTypes.SUBMIT_UPDATE_REQUEST_ADD,
  props<{ trustedAccount: TrustedAccount; accountId: string }>()
);

export const submitUpdateRequestToRemove = createAction(
  TrustedAccountListActionTypes.SUBMIT_UPDATE_REQUEST_REMOVE,
  props<{ trustedAccounts: TrustedAccount[]; accountId: string }>()
);

export const submitUpdateRequestSuccess = createAction(
  TrustedAccountListActionTypes.SUBMIT_UPDATE_REQUEST_SUCCESS,
  props<{ requestId: string }>()
);

export const setDescriptionActionAndNavigateToConfirmAction = createAction(
  TrustedAccountListActionTypes.SET_DESCRIPTION_AND_NAVIGATE,
  props<{ descriptionUpdateActionState: DescriptionUpdateActionState }>()
);

export const setDescriptionAction = createAction(
  TrustedAccountListActionTypes.SET_DESCRIPTION_ACTION,
  props<{ descriptionUpdateActionState: DescriptionUpdateActionState }>()
);

export const setDescription = createAction(
  TrustedAccountListActionTypes.SET_DESCRIPTION,
  props<{ description: string }>()
);

export const submitChangeDescriptionAction = createAction(
  TrustedAccountListActionTypes.SUBMIT_CHANGE_DESCRIPTION_ACTION,
  props<{ descriptionUpdateActionState: DescriptionUpdateActionState }>()
);

export const submitChangeDescriptionActionSuccess = createAction(
  TrustedAccountListActionTypes.SUBMIT_CHANGE_DESCRIPTION_ACTION_SUCCESS,
  props<{ trustedAccountUpdated: TrustedAccount }>()
);

export const cancelDescriptionChange = createAction(
  TrustedAccountListActionTypes.CANCEL_CHANGE_ACCOUNT_DESCRIPTION
);

export const clearDescription = createAction(
  TrustedAccountListActionTypes.CLEAR_CHANGE_ACCOUNT_DESCRIPTION
);

export const prepareForTrustedAccountChangeDescriptionSuccess = createAction(
  TrustedAccountListActionTypes.TRUSTED_ACCOUNT_FULL_IDENTIFIER_DESCRIPTION_UPDATE_SUCCESS,
  props<{ accountFullIdentifier: string }>()
);

export const cancelPendingActivationRequested = createAction(
  TrustedAccountListActionTypes.CANCEL_PENDING_ACTIVATION,
  props<{ accountIdentifier: string; trustedAccountFullIdentifier: string }>()
);

export const cancelPendingActivationRequestedSuccess = createAction(
  TrustedAccountListActionTypes.CANCEL_PENDING_ACTIVATION_SUCCESS
);

export const cancelPendingActivationRequestedFailure = createAction(
  TrustedAccountListActionTypes.CANCEL_PENDING_ACTIVATION_FAILURE
);

export const hideCancelPendingActivationSuccessBanner = createAction(
  TrustedAccountListActionTypes.HIDE_CANCEL_PENDING_ACTIVATION_SUCCESS_BANNER
);
