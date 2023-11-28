import { Action, createReducer, on } from '@ngrx/store';
import {
  Pagination,
  PageParameters,
} from '@shared/search/paginator/paginator.model';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { AccountSearchCriteria } from './account-list.model';
import * as AccountListActions from './account-list.actions';
import { mutableOn } from '@shared/mutable-on';
import { Account } from '@shared/model/account';

export const accountListFeatureKey = 'accountList';

export interface AccountListState {
  externalCriteria: boolean;
  pageParameters: PageParameters;
  pagination: Pagination;
  sortParameters: SortParameters;
  results: Account[];
  criteria: AccountSearchCriteria;
  hideCriteria: boolean;
  showAdvancedSearch: boolean;
  hideTable: boolean;
  resultsLoaded: boolean;
}

const initialState = {
  externalCriteria: undefined,
  pageParameters: {
    page: 0,
    pageSize: 10,
  },
  pagination: undefined,
  sortParameters: {
    sortField: 'accountHolderName',
    sortDirection: 'ASC',
  },
  results: undefined,
  criteria: {
    accountIdOrName: undefined,
    accountStatus: undefined,
    accountType: undefined,
    accountHolderName: undefined,
    complianceStatus: undefined,
    permitOrMonitoringPlanIdentifier: undefined,
    authorizedRepresentativeUrid: undefined,
    regulatorType: undefined,
    allocationStatus: undefined,
    allocationWithholdStatus: undefined,
    excludedForYear: undefined,
    installationOrAircraftOperatorId: undefined,
  },
  hideCriteria: false,
  showAdvancedSearch: false,
  hideTable: true,
  resultsLoaded: false,
};

const accountListReducer = createReducer(
  initialState,
  mutableOn(AccountListActions.resetResultsLoaded, (state) => {
    state.resultsLoaded = false;
  }),
  mutableOn(
    AccountListActions.accountsLoaded,
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
  mutableOn(AccountListActions.hideCriteria, (state) => {
    state.hideCriteria = true;
  }),
  mutableOn(AccountListActions.showAdvancedSearch, (state, { isVisible }) => {
    state.showAdvancedSearch = isVisible;
  }),
  mutableOn(AccountListActions.showCriteria, (state) => {
    state.hideCriteria = false;
  }),
  on(AccountListActions.clearState, (state): AccountListState => initialState)
);

export function reducer(state: AccountListState | undefined, action: Action) {
  return accountListReducer(state, action);
}
