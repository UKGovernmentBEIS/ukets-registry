import { createAction, props } from '@ngrx/store';
import {
  SearchActionPayload,
  TALSearchCriteria,
  TrustedAccount,
} from '@registry-web/shared/model/account/trusted-account';
import { Pagination } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';

export enum TALActionTypes {
  LOAD_TAL = '[Account Trusted Account list] Load the Trusted account list and pagination',
  FETCH_TAL = '[Account Trusted Account list] Fetch the Trusted account list and pagination',
  SHOW_HIDE_CRITERIA = '[Account Trusted Account list] Show/hide filters',
  SEARCH_ACCOUNTS = '[Account Trusted Account list] Search for accounts in TAL',
  SELECT_RESULTS_PAGE = '[Account Trusted Account list Paginator] Select the page of results',
  SELECT_NEXT_RESULTS_PAGE = '[Account Trusted Account list Paginator] Select the next page of results',
  SELECT_PREVIOUS_RESULTS_PAGE = '[Account Trusted Account list Paginator] Select the previous page of results',
  SELECT_LAST_RESULTS_PAGE = '[Account Trusted Account list Paginator] Select the last page of results',
  SELECT_FIRST_RESULTS_PAGE = '[Account Trusted Account list Paginator] Select the first page of results',
  CHANGE_PAGE_SIZE = '[Account Trusted Account list Paginator] Change the page size of results',
  SORT_RESULTS = '[Account Trusted Account list sorting] Sort results by column',
  SEARCH_RESULTS_PAGE_LOADED = '[Account Trusted Account list] Trusted Account results and pagination info loaded',
  CLEAR_STATE = '[[Account Trusted Account list]] Clear state',
}

export const loadTAL = createAction(
  TALActionTypes.LOAD_TAL,
  props<SearchActionPayload>()
);

export const fetchTAL = createAction(TALActionTypes.FETCH_TAL);

export const searchTAL = createAction(
  TALActionTypes.SEARCH_ACCOUNTS,
  props<SearchActionPayload>()
);

export const navigateToPageOfResults = createAction(
  TALActionTypes.SELECT_RESULTS_PAGE,
  props<SearchActionPayload>()
);

export const navigateToNextPageOfResults = createAction(
  TALActionTypes.SELECT_NEXT_RESULTS_PAGE,
  props<SearchActionPayload>()
);

export const navigateToPreviousPageOfResults = createAction(
  TALActionTypes.SELECT_PREVIOUS_RESULTS_PAGE,
  props<SearchActionPayload>()
);

export const navigateToLastPageOfResults = createAction(
  TALActionTypes.SELECT_LAST_RESULTS_PAGE,
  props<SearchActionPayload>()
);

export const navigateToFirstPageOfResults = createAction(
  TALActionTypes.SELECT_FIRST_RESULTS_PAGE,
  props<SearchActionPayload>()
);

export const changePageSize = createAction(
  TALActionTypes.CHANGE_PAGE_SIZE,
  props<SearchActionPayload>()
);

export const sortResults = createAction(
  TALActionTypes.SORT_RESULTS,
  props<SearchActionPayload>()
);

export const trustedAccountListLoaded = createAction(
  TALActionTypes.SEARCH_RESULTS_PAGE_LOADED,
  props<{
    results: TrustedAccount[];
    pagination: Pagination;
    sortParameters: SortParameters;
    criteria: TALSearchCriteria;
  }>()
);

export const clearState = createAction(TALActionTypes.CLEAR_STATE);

export const showHideCriteria = createAction(
  TALActionTypes.SHOW_HIDE_CRITERIA,
  props<{ showHide: boolean }>()
);
