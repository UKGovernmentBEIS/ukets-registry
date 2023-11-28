import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  tokenChangeReducerFeatureKey,
  TokenChangeState
} from '@user-management/token-change/reducer/token-change.reducer';

const selectTokenChangeState = createFeatureSelector<TokenChangeState>(
  tokenChangeReducerFeatureKey
);

export const selectState = createSelector(
  selectTokenChangeState,
  state => state
);

export const selectSubmittedRequestIdentifier = createSelector(
  selectTokenChangeState,
  state => state.submittedRequestIdentifier
);
