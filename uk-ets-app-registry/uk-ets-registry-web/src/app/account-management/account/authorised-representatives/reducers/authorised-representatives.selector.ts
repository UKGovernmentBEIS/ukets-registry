import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  authorisedRepresentativesFeatureKey,
  AuthorisedRepresentativesState,
} from './authorised-representatives.reducer';
import { AuthorisedRepresentativesUpdateType } from '@authorised-representatives/model';
import { AuthorisedRepresentativesRoutePaths } from '@authorised-representatives/model/authorised-representatives-route-paths.model';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { ARAccessRights } from '@shared/model/account';

const selectAuthorisedRepresentativesState =
  createFeatureSelector<AuthorisedRepresentativesState>(
    authorisedRepresentativesFeatureKey
  );

export const selectUpdateType = createSelector(
  selectAuthorisedRepresentativesState,
  (state) => state.updateType
);

export const selectExistingAr = createSelector(
  selectAuthorisedRepresentativesState,
  (state) => state.existingAr
);

export const selectNewAr = createSelector(
  selectAuthorisedRepresentativesState,
  (state) => state.newAr
);

export const selectNewArFullName = createSelector(
  selectAuthorisedRepresentativesState,
  (state) =>
    state.newAr
      ? state.newAr.user.alsoKnownAs && state.newAr.user.alsoKnownAs.length > 0
        ? `${state.newAr.user.alsoKnownAs} (${state.newAr.user.status})`
        : `${state.newAr.user.firstName} ${state.newAr.user.lastName} (${state.newAr.user.status})`
      : null
);

export const selectNewAccessRights = createSelector(
  selectAuthorisedRepresentativesState,
  (state) => state.selectedAccessRights
);

export const calculateGoBackPathFromSelectAccessRights = createSelector(
  selectAuthorisedRepresentativesState,
  (state) => {
    switch (state.updateType) {
      case AuthorisedRepresentativesUpdateType.ADD:
        return AuthorisedRepresentativesRoutePaths.ADD_REPRESENTATIVE;
      case AuthorisedRepresentativesUpdateType.REPLACE:
        return AuthorisedRepresentativesRoutePaths.REPLACE_REPRESENTATIVE;
      case AuthorisedRepresentativesUpdateType.CHANGE_ACCESS_RIGHTS:
        return AuthorisedRepresentativesRoutePaths.SELECT_AR_TABLE;
    }
    return AuthorisedRepresentativesRoutePaths.SELECT_UPDATE_TYPE;
  }
);

export const selectEligibleArsForAction = createSelector(
  selectAuthorisedRepresentativesState,
  (state) => state.eligibleArsForAction
);

export const selectSelectedArFromTableUrid = createSelector(
  selectAuthorisedRepresentativesState,
  (state) => state.selectedArFromTable
);

export const selectSelectedArFromTable = createSelector(
  selectAuthorisedRepresentativesState,
  (state) =>
    state.eligibleArsForAction
      .filter((ar) => ar.urid === state.selectedArFromTable)
      .pop()
);

export const calculateGoBackPathFromCheckUpdateRequest = createSelector(
  selectAuthorisedRepresentativesState,
  (state) => {
    switch (state.updateType) {
      case AuthorisedRepresentativesUpdateType.ADD:
      case AuthorisedRepresentativesUpdateType.REPLACE:
      case AuthorisedRepresentativesUpdateType.CHANGE_ACCESS_RIGHTS:
        return AuthorisedRepresentativesRoutePaths.SELECT_ACCESS_RIGHTS;
      case AuthorisedRepresentativesUpdateType.REMOVE:
      case AuthorisedRepresentativesUpdateType.SUSPEND:
      case AuthorisedRepresentativesUpdateType.RESTORE:
        return AuthorisedRepresentativesRoutePaths.SELECT_AR_TABLE;
    }
  }
);

export const selectSubmittedRequestIdentifier = createSelector(
  selectAuthorisedRepresentativesState,
  (state) => state.submittedRequestIdentifier
);

export const selectAuthorisedRepresentativesOtherAccounts = createSelector(
  selectAuthorisedRepresentativesState,
  (state) => state.authorisedRepresentativesOtherAccounts
);

export const selectEligibleArsAsOptions = createSelector(
  selectEligibleArsForAction,
  (ars) =>
    ars.map(
      (ar) =>
        ({
          label:
            ar.user.alsoKnownAs && ar.user.alsoKnownAs.length > 0
              ? ar.user.alsoKnownAs
              : `${ar.user.firstName} ${ar.user.lastName}`,
          value: ar.urid,
        } as Option)
    )
);

export const selectUpdateRequestPayload = createSelector(
  selectUpdateType,
  selectNewAr,
  selectExistingAr,
  selectSelectedArFromTableUrid,
  selectNewAccessRights,
  (
    updateType,
    newAr,
    existingAr,
    selectedArFromTable,
    selectedAccessRights
  ) => {
    let candidateUrid: string;
    let replaceeUrid: string;
    let accessRight: ARAccessRights;
    let comment: string;
    switch (updateType) {
      case AuthorisedRepresentativesUpdateType.ADD:
        candidateUrid = newAr ? newAr.urid : '';
        accessRight = selectedAccessRights;
        break;
      case AuthorisedRepresentativesUpdateType.RESTORE:
      case AuthorisedRepresentativesUpdateType.REMOVE:
      case AuthorisedRepresentativesUpdateType.SUSPEND:
        candidateUrid = selectedArFromTable;
        break;
      case AuthorisedRepresentativesUpdateType.CHANGE_ACCESS_RIGHTS:
        candidateUrid = selectedArFromTable;
        accessRight = selectedAccessRights;
        break;
      case AuthorisedRepresentativesUpdateType.REPLACE:
        candidateUrid = newAr ? newAr.urid : '';
        replaceeUrid = existingAr ? existingAr.urid : '';
        accessRight = selectedAccessRights;
    }
    return { candidateUrid, replaceeUrid, accessRight, comment };
  }
);
