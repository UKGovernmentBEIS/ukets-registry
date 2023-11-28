import { Action, createReducer, on } from '@ngrx/store';
import {
  Pagination,
  PageParameters,
} from '../../shared/search/paginator/paginator.model';
import { mutableOn } from '@shared/mutable-on';
import { SortParameters } from 'src/app/shared/search/sort/SortParameters';
import * as TransactionList from './transaction-list.actions';
import * as TaskListActions from '../../task-management/task-list/task-list.actions';
import {
  Transaction,
  TransactionSearchCriteria,
} from '@shared/model/transaction';

export const transactionListFeatureKey = 'transactionList';

const initialState = {
  externalCriteria: undefined,
  pageParameters: {
    page: 0,
    pageSize: 10,
  },
  pagination: undefined,
  sortParameters: {
    sortField: 'lastUpdated',
    sortDirection: 'DESC',
  },
  results: undefined,
  criteria: {
    transactionId: undefined,
    transactionType: undefined,
    transactionStatus: undefined,
    transactionLastUpdateDateFrom: undefined,
    transactionLastUpdateDateTo: undefined,
    transferringAccountNumber: undefined,
    acquiringAccountNumber: undefined,
    acquiringAccountType: undefined,
    transferringAccountType: undefined,
    unitType: undefined,
    initiatorUserId: undefined,
    approverUserId: undefined,
    transactionalProposalDateFrom: undefined,
    transactionalProposalDateTo: undefined,
    reversed: undefined,
  },
  hideCriteria: false,
  showAdvancedSearch: false,
  hideTable: true,
  resultsLoaded: false,
};

export interface TransactionListState {
  externalCriteria: boolean;
  pageParameters: PageParameters;
  pagination: Pagination;
  sortParameters: SortParameters;
  results: Transaction[];
  criteria: TransactionSearchCriteria;
  hideCriteria: boolean;
  showAdvancedSearch: boolean;
  hideTable: boolean;
  resultsLoaded: boolean;
}

const transactionListReducer = createReducer(
  initialState,
  mutableOn(TaskListActions.resetResultsLoaded, (state) => {
    state.resultsLoaded = false;
  }),
  mutableOn(
    TransactionList.transactionsLoaded,
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
  mutableOn(TransactionList.hideCriteria, (state) => {
    state.hideCriteria = true;
  }),
  mutableOn(TransactionList.showCriteria, (state) => {
    state.hideCriteria = false;
  }),
  mutableOn(TransactionList.showAdvancedSearch, (state, { isVisible }) => {
    state.showAdvancedSearch = isVisible;
  }),
  on(TransactionList.clearState, (state): TransactionListState => initialState)
);

export function reducer(
  state: TransactionListState | undefined,
  action: Action
) {
  return transactionListReducer(state, action);
}
