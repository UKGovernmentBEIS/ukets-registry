import { createFeatureSelector, createSelector } from '@ngrx/store';
import { State } from 'src/app/reducers';
import { accountOpeningFeatureKey } from '../account-opening.reducer';
import { AccountOpeningState } from '../account-opening.model';

const selectAccountOpening = createFeatureSelector<State, AccountOpeningState>(
  accountOpeningFeatureKey
);

export const selectTrustedAccountList = createSelector(
  selectAccountOpening,
  accountOpeningState => accountOpeningState.trustedAccountList
);

export const selectTrustedAccountListCompleted = createSelector(
  selectAccountOpening,
  accountOpeningState => accountOpeningState.trustedAccountListCompleted
);
