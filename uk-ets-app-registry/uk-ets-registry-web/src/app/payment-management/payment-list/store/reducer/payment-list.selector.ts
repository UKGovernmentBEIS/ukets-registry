import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  paymentListFeatureKey,
  PaymentListState,
} from './payment-list.reducer';

const selectPaymentListState = createFeatureSelector<PaymentListState>(
  paymentListFeatureKey
);

export const selectSearchState = createSelector(
  selectPaymentListState,
  (state) => state
);

export const selectCriteriaExternal = createSelector(
  selectPaymentListState,
  (state) => state.externalCriteria
);

export const selectPagination = createSelector(
  selectPaymentListState,
  (state) => state.pagination
);

export const selectPageParameters = createSelector(
  selectPaymentListState,
  (state) => state.pageParameters
);

export const selectSortParameters = createSelector(
  selectPaymentListState,
  (state) => state.sortParameters
);

export const selectResults = createSelector(
  selectPaymentListState,
  (state) => state.results
);

export const selectHideCriteria = createSelector(
  selectPaymentListState,
  (state) => state.hideCriteria
);

export const selectShowAdvancedSearch = createSelector(
  selectPaymentListState,
  (state) => state.showAdvancedSearch
);

export const selectResultsLoaded = createSelector(
  selectPaymentListState,
  (state) => state.resultsLoaded
);

export const selectCriteria = createSelector(
  selectPaymentListState,
  (state) => state.criteria
);
