import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import { AuthorisedRepresentativesUpdateType } from '@authorised-representatives/model';
import { AuthorisedRepresentativesActions } from '@authorised-representatives/actions';
import {
  ARAccessRights,
  AuthorisedRepresentative,
} from '@shared/model/account';
import { Draft } from 'immer';

export const authorisedRepresentativesFeatureKey = 'authorised-representatives';

export interface AuthorisedRepresentativesState {
  updateType: AuthorisedRepresentativesUpdateType;
  existingAr: AuthorisedRepresentative;
  newAr: AuthorisedRepresentative;
  selectedAccessRights: ARAccessRights;
  eligibleArsForAction: AuthorisedRepresentative[];
  selectedArFromTable: string;
  submittedRequestIdentifier: string;
  authorisedRepresentativesOtherAccounts: AuthorisedRepresentative[];
  goals?: { name: string; time: number };
}

export const initialState: AuthorisedRepresentativesState = {
  updateType: null,
  existingAr: null,
  newAr: null,
  selectedAccessRights: null,
  eligibleArsForAction: [],
  selectedArFromTable: null,
  submittedRequestIdentifier: null,
  authorisedRepresentativesOtherAccounts: [],
};

const authorisedRepresentativesReducer = createReducer(
  initialState,
  mutableOn(
    AuthorisedRepresentativesActions.setRequestUpdateType,
    (state: Draft<AuthorisedRepresentativesState>, { updateType }) => {
      if (state.updateType !== updateType) {
        resetState(state);
      }
      state.updateType = updateType;
    }
  ),
  mutableOn(
    AuthorisedRepresentativesActions.setNewAccessRights,
    (state: Draft<AuthorisedRepresentativesState>, { accessRights }) => {
      state.selectedAccessRights = accessRights;
    }
  ),
  mutableOn(
    AuthorisedRepresentativesActions.fetchEligibleArsSuccess,
    (state: Draft<AuthorisedRepresentativesState>, { eligibleArs }) => {
      state.eligibleArsForAction = eligibleArs;
    }
  ),
  mutableOn(
    AuthorisedRepresentativesActions.setSelectedArFromTable,
    (state: Draft<AuthorisedRepresentativesState>, { selectedAr }) => {
      state.selectedArFromTable = selectedAr;
    }
  ),
  mutableOn(
    AuthorisedRepresentativesActions.clearAuthorisedRepresentativesUpdateRequest,
    (state) => {
      resetState(state);
    }
  ),
  mutableOn(
    AuthorisedRepresentativesActions.fetchAuthorisedRepresentativesOtherAccountsSuccess,
    (
      state: Draft<AuthorisedRepresentativesState>,
      { authorisedRepresentatives }
    ) => {
      state.authorisedRepresentativesOtherAccounts = authorisedRepresentatives;
    }
  ),
  mutableOn(
    AuthorisedRepresentativesActions.selectAuthorisedRepresentativeSuccess,
    (
      state: Draft<AuthorisedRepresentativesState>,
      { authorisedRepresentative }
    ) => {
      state.newAr = authorisedRepresentative;
    }
  ),
  mutableOn(
    AuthorisedRepresentativesActions.replaceAuthorisedRepresentativeSuccess,
    (
      state: Draft<AuthorisedRepresentativesState>,
      { candidateAr, existingArUrid }
    ) => {
      state.newAr = candidateAr;
      state.existingAr = state.eligibleArsForAction.find(
        (ar) => ar.urid === existingArUrid
      );
    }
  ),
  mutableOn(
    AuthorisedRepresentativesActions.submitUpdateRequestSuccess,
    (state, { requestId }) => {
      state.submittedRequestIdentifier = requestId;
    }
  )
);

export function reducer(
  state: AuthorisedRepresentativesState | undefined,
  action: Action
) {
  return authorisedRepresentativesReducer(state, action);
}

function resetState(state: AuthorisedRepresentativesState) {
  state.updateType = initialState.updateType;
  state.existingAr = initialState.existingAr;
  state.newAr = initialState.newAr;
  state.selectedAccessRights = initialState.selectedAccessRights;
  state.eligibleArsForAction = initialState.eligibleArsForAction;
  state.selectedArFromTable = initialState.selectedArFromTable;
  state.submittedRequestIdentifier = initialState.submittedRequestIdentifier;
  state.authorisedRepresentativesOtherAccounts =
    initialState.authorisedRepresentativesOtherAccounts;
}
