import { createAction, props } from '@ngrx/store';
import { NavigationExtras, Params } from '@angular/router';
import { TrustedAccountListRules } from '@shared/model/account';

export enum TalTransactionRulesActionTypes {
  NAVIGATE_TO = '[TAL Transaction Rules] Navigate to',
  FETCH_CURRENT_RULES = '[TAL Transaction Rules] Fetch current transaction rules',
  SET_CURRENT_RULES_SUCCESS = '[TAL Transaction Rules] Set current transaction rules success',
  SET_NEW_RULES = '[TAL Transaction Rules] Set new transaction rules',
  CANCEL_CLICKED = '[TAL Transaction Rules] Cancel clicked',
  CANCEL_TAL_TRANSACTION_RULES_UPDATE_REQUEST = '[TAL Transaction Rules] Cancel TAL transaction rules update request',
  CLEAR_TAL_TRANSACTION_RULES_UPDATE_REQUEST = '[TAL Transaction Rules] Clear update request',
  SUBMIT_UPDATE_REQUEST = '[TAL Transaction Rules] Submit update request',
  SUBMIT_UPDATE_REQUEST_SUCCESS = '[TAL Transaction Rules] Submit update request success'
}

export const navigateTo = createAction(
  TalTransactionRulesActionTypes.NAVIGATE_TO,
  props<{ route: string; extras?: NavigationExtras; queryParams?: Params }>()
);

export const fetchCurrentRules = createAction(
  TalTransactionRulesActionTypes.FETCH_CURRENT_RULES,
  props<{ accountId: string }>()
);

export const setCurrentRulesSuccess = createAction(
  TalTransactionRulesActionTypes.SET_CURRENT_RULES_SUCCESS,
  props<{ currentRules: TrustedAccountListRules }>()
);

export const setNewRules = createAction(
  TalTransactionRulesActionTypes.SET_NEW_RULES,
  props<{ newRules: TrustedAccountListRules }>()
);

export const submitUpdateRequest = createAction(
  TalTransactionRulesActionTypes.SUBMIT_UPDATE_REQUEST
);

export const submitUpdateRequestSuccess = createAction(
  TalTransactionRulesActionTypes.SUBMIT_UPDATE_REQUEST_SUCCESS,
  props<{ requestId: string }>()
);
export const clearTalTransactionRulesUpdateRequest = createAction(
  TalTransactionRulesActionTypes.CLEAR_TAL_TRANSACTION_RULES_UPDATE_REQUEST
);

export const cancelTalTransactionRulesUpdateRequest = createAction(
  TalTransactionRulesActionTypes.CANCEL_TAL_TRANSACTION_RULES_UPDATE_REQUEST
);

export const cancelClicked = createAction(
  TalTransactionRulesActionTypes.CANCEL_CLICKED,
  props<{ route: string }>()
);
