import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  TalTransactionRulesState,
  talTransactionRulesFeatureKey
} from '@tal-transaction-rules/reducers/tal-transaction-rules.reducer';

const selectTalTransactionRulesState = createFeatureSelector<
  TalTransactionRulesState
>(talTransactionRulesFeatureKey);

export const selectCurrentRules = createSelector(
  selectTalTransactionRulesState,
  state => state.currentRules
);

export const selectUpdatedRules = createSelector(
  selectTalTransactionRulesState,
  state => state.newRules
);

export const selectSubmittedRequestIdentifier = createSelector(
  selectTalTransactionRulesState,
  state => state.submittedRequestIdentifier
);
