import { Action, createAction, props } from '@ngrx/store';
import {
  ARAccessRights,
  AuthorisedRepresentative,
  LoadARsActionPayload,
} from '../../shared/model/account/authorised-representative';
import { ErrorSummary } from '../../shared/error-summary/error-summary';
import { ViewOrCheck } from '../account-opening.model';

export enum AuthorisedRepresentativeActionTypes {
  FETCH_ONE = '[Authorised Representative Wizard] Fetch One',
  FETCH_LIST = '[Authorised Representative Wizard] Fetch List',
  LOAD_ARS = '[Authorised Representative Wizard] Load ARs',
  SET_CURRENT_AR = '[Authorised Representative Wizard] Set current AR',
  SET_CURRENT_AR_BY_INDEX = '[Authorised Representative Wizard] Set current AR by index',
  SET_ACCESS_RIGHTS = '[Authorised Representative Wizard] Set access rights',
  ADD_CURRENT_AR_TO_LIST = '[Authorised Representative Wizard] Add current AR to list',
  REMOVE_AR_FROM_LIST = '[Authorised Representative Wizard] Remove AR from list',
  SET_VIEW_OR_CHECK = '[Authorised Representative Wizard] Set view or check',
  SET_AR_INDEX = '[Authorised Representative Wizard] Set AR index',
  CLEAR_CURRENT_AR = '[Authorised Representative Wizard] Clear current AR',
  COMPLETE = '[Authorised Representative Wizard] Complete',
}

export const fetchAuthorisedRepresentative = createAction(
  AuthorisedRepresentativeActionTypes.FETCH_ONE,
  props<{
    urid: string;
    errorSummaries: unknown;
  }>()
);
export const fetchAuthorisedRepresentatives = createAction(
  AuthorisedRepresentativeActionTypes.FETCH_LIST,
  props<{
    accountHolderId: string;
    errorSummary: ErrorSummary;
  }>()
);
export const loadAuthorisedRepresentatives = createAction(
  AuthorisedRepresentativeActionTypes.LOAD_ARS,
  props<LoadARsActionPayload>()
);
export const setCurrentAuthorisedRepresentative = createAction(
  AuthorisedRepresentativeActionTypes.SET_CURRENT_AR,
  props<{ AR: AuthorisedRepresentative }>()
);
export const setCurrentAuthorisedRepresentativeByIndex = createAction(
  AuthorisedRepresentativeActionTypes.SET_CURRENT_AR_BY_INDEX,
  props<{ index: number }>()
);
export const setAccessRightsToCurrentAR = createAction(
  AuthorisedRepresentativeActionTypes.SET_ACCESS_RIGHTS,
  props<{ accessRights: ARAccessRights }>()
);
export const addCurrentARToList = createAction(
  AuthorisedRepresentativeActionTypes.ADD_CURRENT_AR_TO_LIST
);
export const removeARFromList = createAction(
  AuthorisedRepresentativeActionTypes.REMOVE_AR_FROM_LIST
);
export const setAuthorisedRepresentativeViewOrCheck = createAction(
  AuthorisedRepresentativeActionTypes.SET_VIEW_OR_CHECK,
  props<{ viewOrCheck: ViewOrCheck }>()
);
export const setAuthorisedRepresentativeIndex = createAction(
  AuthorisedRepresentativeActionTypes.SET_AR_INDEX,
  props<{ index: number }>()
);
export const clearCurrentAuthorisedRepresentative = createAction(
  AuthorisedRepresentativeActionTypes.CLEAR_CURRENT_AR
);
export const completeWizard = createAction(
  AuthorisedRepresentativeActionTypes.COMPLETE,
  props<{
    complete: boolean;
  }>()
);

export class LoadARs implements Action {
  readonly type = AuthorisedRepresentativeActionTypes.LOAD_ARS;
  constructor(readonly ARs: AuthorisedRepresentative[]) {}
}

export class FetchOneAR implements Action {
  readonly type = AuthorisedRepresentativeActionTypes.FETCH_ONE;
  constructor(readonly urid: string, readonly errorSummaries: unknown) {}
}

export class FetchARList implements Action {
  readonly type = AuthorisedRepresentativeActionTypes.FETCH_LIST;
  constructor(
    readonly errorSummary: ErrorSummary,
    readonly accountHolderId: string
  ) {}
}
