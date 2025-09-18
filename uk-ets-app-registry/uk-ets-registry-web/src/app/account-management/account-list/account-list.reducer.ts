import { Action, createReducer, on } from '@ngrx/store';
import {
  Pagination,
  PageParameters,
} from '@shared/search/paginator/paginator.model';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { AccountSearchCriteria } from './account-list.model';
import * as AccountListActions from './account-list.actions';
import { mutableOn } from '@shared/mutable-on';
import { Account, AccountStatus } from '@shared/model/account';

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

const defaultCriteria = {
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
  operatorId: undefined,
  imo: undefined,
};

const defaultCriteriaAdmin = {
  accountIdOrName: undefined,
  accountStatus: 'ALL_EXCEPT_CLOSED',
  accountType: undefined,
  accountHolderName: undefined,
  complianceStatus: undefined,
  permitOrMonitoringPlanIdentifier: undefined,
  authorizedRepresentativeUrid: undefined,
  regulatorType: undefined,
  allocationStatus: undefined,
  allocationWithholdStatus: undefined,
  excludedForYear: undefined,
  operatorId: undefined,
  imo: undefined,
};

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
  criteria: defaultCriteria,
  hideCriteria: false,
  showAdvancedSearch: false,
  hideTable: true,
  resultsLoaded: false,
};

const initialStateAdmin = { ...initialState, criteria: defaultCriteriaAdmin };

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
  on(
    AccountListActions.clearStatePerRole,
    (state, { isAdmin }): AccountListState =>
      isAdmin ? initialStateAdmin : initialState
  )
);

export function reducer(state: AccountListState | undefined, action: Action) {
  return accountListReducer(state, action);
}
