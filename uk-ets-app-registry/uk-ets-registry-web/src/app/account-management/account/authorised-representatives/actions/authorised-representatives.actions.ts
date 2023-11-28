import { createAction, props } from '@ngrx/store';
import { NavigationExtras, Params } from '@angular/router';
import { AuthorisedRepresentativesUpdateType } from '@authorised-representatives/model';
import {
  ARAccessRights,
  AuthorisedRepresentative,
} from '@shared/model/account';
import { ArUpdateRequest } from '@authorised-representatives/model/ar-update-request';

export const navigateTo = createAction(
  '[Authorised Representatives] Navigate to',
  props<{ route: string; extras?: NavigationExtras; queryParams?: Params }>()
);

export const setRequestUpdateType = createAction(
  '[Authorised Representatives] Set request update type',
  props<{ updateType: AuthorisedRepresentativesUpdateType }>()
);

export const setNewAccessRights = createAction(
  '[Authorised Representatives] Set new access rights',
  props<{ accessRights: ARAccessRights }>()
);

export const fetchEligibleArs = createAction(
  '[Authorised Representatives] Fetch eligible authorised representatives'
);

export const fetchEligibleArsSuccess = createAction(
  '[Authorised Representatives] Fetch eligible authorised representatives success',
  props<{
    eligibleArs: AuthorisedRepresentative[];
  }>()
);

export const setSelectedArFromTable = createAction(
  '[Authorised Representatives] Set selected AR from table',
  props<{ selectedAr: string }>()
);

export const submitUpdateRequest = createAction(
  '[Authorised Representatives] Submit update request',
  props<{ comment: string }>()
);

export const submitUpdateRequestSuccess = createAction(
  '[Authorised Representatives] Submit update request success',
  props<{ requestId: string }>()
);

export const cancelClicked = createAction(
  '[Authorised Representatives] Cancel clicked',
  props<{ route: string }>()
);

export const cancelAuthorisedRepresentativesUpdateRequest = createAction(
  '[Authorised Representatives] Cancel update request'
);

export const clearAuthorisedRepresentativesUpdateRequest = createAction(
  '[Authorised Representatives] Clear update request'
);

export const fetchAuthorisedRepresentativesOtherAccounts = createAction(
  '[Authorised Representatives] Fetch Other Accounts'
);

export const fetchAuthorisedRepresentativesOtherAccountsSuccess = createAction(
  '[Authorised Representatives] Fetch Other Accounts Success',
  props<{ authorisedRepresentatives: AuthorisedRepresentative[] }>()
);

export const selectAuthorisedRepresentative = createAction(
  '[Authorised Representatives] Select Authorised Representative',
  props<{ urid: string }>()
);

export const selectAuthorisedRepresentativeSuccess = createAction(
  '[Authorised Representatives] Select Authorised Representative Success',
  props<{ authorisedRepresentative: AuthorisedRepresentative }>()
);

export const replaceAuthorisedRepresentative = createAction(
  '[Authorised Representatives] Replace Authorised Representative',
  props<{ updateRequest: ArUpdateRequest }>()
);

export const replaceAuthorisedRepresentativeSuccess = createAction(
  '[Authorised Representatives] Replace Authorised Representative Success',
  props<{
    candidateAr: AuthorisedRepresentative;
    existingArUrid: string;
  }>()
);
