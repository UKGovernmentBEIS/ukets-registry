import { createAction, props } from '@ngrx/store';
import { Pagination } from '../../shared/search/paginator/paginator.model';
import { SortParameters } from 'src/app/shared/search/sort/SortParameters';
import {
  AccountSearchResult,
  AccountSearchCriteria,
  SearchActionPayload,
} from './account-list.model';

/**
 * TODO: remove action type enum, pass the action type string into action creators.
 */
export enum AccountListActionTypes {
  SEARCH_ACCOUNTS = '[Account List: Search form: Search button] Search for results',
  SELECT_RESULTS_PAGE = '[Account List Paginator] Select the page of results',
  SELECT_NEXT_RESULTS_PAGE = '[Account List Paginator] Select the next page of results',
  SELECT_PREVIOUS_RESULTS_PAGE = '[Account List Paginator] Select the previous page of results',
  SELECT_LAST_RESULTS_PAGE = '[Account List Paginator] Select the last page of results',
  SELECT_FIRST_RESULTS_PAGE = '[Account List Paginator] Select the first page of results',
  CHANGE_PAGE_SIZE = '[Account List Paginator] Change the page size of results',
  HIDE_CRITERIA = '[Account List show/hide button] Set the hideCriteria boolean to false',
  SHOW_CRITERIA = '[Account List show/hide button] Set the hideCriteria boolean to true',
  SHOW_ADVANCED_SEARCH = '[Task List advanced search button] Set the advanced search visible or hidden',
  SEARCH_RESULTS_PAGE_LOADED = '[Account list Effect] Account results and pagination info loaded',
  LOAD_ACCOUNTS = '[Account list Effect] Load the Accounts and pagination',
  CLEAR_STATE = '[Account list resolver] Clear the state of Account List (default mode)',
  RESET_RESULTS_LOADED = '[Account list resolver] Set the resultsLoaded flag to false (LOAD mode)',
  REPLAY_SEARCH = '[Account List resolver] Replay the search by using the stored criteria, page number, page size (LOAD mode)',
  SORT_RESULTS = '[Account List results sort column] Sort results by column',
  RESET_SUCCESS = '[Account list resolver] Send command to clear the success message',
  CLEAR_SUCCESS = '[Account list Effect] Clear the success message',
}

// TODO: Actions that needed for searching accounts are exactly the same with task list actions. Consider minimizing duplications.

export const loadAccounts = createAction(
  AccountListActionTypes.LOAD_ACCOUNTS,
  props<SearchActionPayload>()
);

export const searchAccounts = createAction(
  AccountListActionTypes.SEARCH_ACCOUNTS,
  props<SearchActionPayload>()
);

export const navigateToPageOfResults = createAction(
  AccountListActionTypes.SELECT_RESULTS_PAGE,
  props<SearchActionPayload>()
);

export const replaySearch = createAction(
  AccountListActionTypes.REPLAY_SEARCH,
  props<SearchActionPayload>()
);

export const navigateToNextPageOfResults = createAction(
  AccountListActionTypes.SELECT_NEXT_RESULTS_PAGE,
  props<SearchActionPayload>()
);

export const navigateToPreviousPageOfResults = createAction(
  AccountListActionTypes.SELECT_PREVIOUS_RESULTS_PAGE,
  props<SearchActionPayload>()
);

export const navigateToLastPageOfResults = createAction(
  AccountListActionTypes.SELECT_LAST_RESULTS_PAGE,
  props<SearchActionPayload>()
);

export const navigateToFirstPageOfResults = createAction(
  AccountListActionTypes.SELECT_FIRST_RESULTS_PAGE,
  props<SearchActionPayload>()
);

export const changePageSize = createAction(
  AccountListActionTypes.CHANGE_PAGE_SIZE,
  props<SearchActionPayload>()
);

export const sortResults = createAction(
  AccountListActionTypes.SORT_RESULTS,
  props<SearchActionPayload>()
);

export const accountsLoaded = createAction(
  AccountListActionTypes.SEARCH_RESULTS_PAGE_LOADED,
  props<{
    results: AccountSearchResult[];
    pagination: Pagination;
    sortParameters: SortParameters;
    criteria: AccountSearchCriteria;
  }>()
);

export const hideCriteria = createAction(AccountListActionTypes.HIDE_CRITERIA);

export const showCriteria = createAction(AccountListActionTypes.SHOW_CRITERIA);

export const showAdvancedSearch = createAction(
  AccountListActionTypes.SHOW_ADVANCED_SEARCH,
  props<{ isVisible: boolean }>()
);

export const clearState = createAction(AccountListActionTypes.CLEAR_STATE);

export const resetResultsLoaded = createAction(
  AccountListActionTypes.RESET_RESULTS_LOADED
);

export const resetSuccess = createAction(AccountListActionTypes.RESET_SUCCESS);

export const clearSuccess = createAction(AccountListActionTypes.CLEAR_SUCCESS);
