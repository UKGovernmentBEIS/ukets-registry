import { createFeatureSelector, createSelector } from '@ngrx/store';
import { State } from 'src/app/reducers';
import { accountOpeningFeatureKey } from './account-opening.reducer';
import { AccountOpeningState } from './account-opening.model';
import { AccountType } from '@shared/model/account';

export const selectAccountOpening = createFeatureSelector<
  State,
  AccountOpeningState
>(accountOpeningFeatureKey);

export const selectAccountType = createSelector(
  selectAccountOpening,
  (accountOpeningState) => accountOpeningState.accountType
);

export const selectMinNumberOfARs = createSelector(
  selectAccountOpening,
  (accountOpeningState) => accountOpeningState.minNumberOfARs
);

export const selectMaxNumberOfARs = createSelector(
  selectAccountOpening,
  (accountOpeningState) => accountOpeningState.maxNumberOfARs
);

export const selectRequestID = createSelector(
  selectAccountOpening,
  (accountOpeningState) => accountOpeningState.requestID
);

export const selectIsOHA = createSelector(
  selectAccountOpening,
  (state) => state.accountType === AccountType.OPERATOR_HOLDING_ACCOUNT
);

export const selectIsAOHA = createSelector(
  selectAccountOpening,
  (state) => state.accountType === AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT
);

export const selectIsOHAOrAOHA = createSelector(
  selectIsOHA,
  selectIsAOHA,
  (isOHA, isAOHA) => {
    return isOHA || isAOHA;
  }
);

export const selectAccountDetailsSameBillingAddress = createSelector(
  selectAccountOpening,
  (state) => state.accountDetailsSameBillingAddress
);

export const selectTaskType = createSelector(selectAccountOpening, (state) => {
  return state.taskType;
});
