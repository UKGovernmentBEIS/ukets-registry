import { Action, createReducer, on } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import {
  PaymentSearchCriteria,
  PaymentSearchResult,
} from '@payment-management/model';
import { PageParameters, Pagination } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';
import * as PaymentListActions from '@payment-management/payment-list/store/actions';

export const paymentListFeatureKey = 'paymentList';

const initialState = {
  externalCriteria: undefined,
  pageParameters: {
    page: 0,
    pageSize: 10,
  },
  pagination: undefined,
  sortParameters: {
    sortField: 'updated',
    sortDirection: 'DESC',
  },
  results: undefined,
  criteria: {
    referenceNumber: null,
  },
  hideCriteria: false,
  showAdvancedSearch: false,
  hideTable: true,
  resultsLoaded: false,
};

export interface PaymentListState {
  externalCriteria: boolean;
  pageParameters: PageParameters;
  pagination: Pagination;
  sortParameters: SortParameters;
  results: PaymentSearchResult[];
  criteria: PaymentSearchCriteria;
  hideCriteria: boolean;
  showAdvancedSearch: boolean;
  hideTable: boolean;
  resultsLoaded: boolean;
}

const paymentListReducer = createReducer(
  initialState,
  mutableOn(PaymentListActions.resetResultsLoaded, (state) => {
    state.resultsLoaded = false;
  }),
  mutableOn(
    PaymentListActions.paymentsLoaded,
    (state, { results, pagination, sortParameters, criteria }) => {
      state.criteria = criteria;
      state.pagination = pagination;
      state.results = results;
      state.hideTable = false;
      state.resultsLoaded = true;
      state.pageParameters = {
        page: pagination.currentPage - 1,
        pageSize: pagination.pageSize, // The page size that user selected
      };
      state.sortParameters = sortParameters;
    }
  ),
  mutableOn(
    PaymentListActions.setReferenceNumber,
    (state, { referenceNumber }) => {
      state.criteria.referenceNumber = referenceNumber;
    }
  ),
  mutableOn(PaymentListActions.hideCriteria, (state) => {
    state.hideCriteria = true;
  }),
  mutableOn(PaymentListActions.showCriteria, (state) => {
    state.hideCriteria = false;
  }),
  mutableOn(PaymentListActions.showAdvancedSearch, (state, { isVisible }) => {
    state.showAdvancedSearch = isVisible;
  }),
  on(PaymentListActions.clearState, (state): PaymentListState => initialState)
);

export function reducer(state: PaymentListState | undefined, action: Action) {
  return paymentListReducer(state, action);
}
