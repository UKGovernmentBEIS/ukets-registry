import { createAction, props } from '@ngrx/store';
import { Pagination } from '@shared/search/paginator/paginator.model';
import { SortParameters } from '@shared/search/sort/SortParameters';
import {
  SearchActionPayload,
  UserProjection,
  UserSearchCriteria
} from './user-list.model';

export const loadUsers = createAction(
  '[User list Effect] Load the Users and pagination',
  props<SearchActionPayload>()
);

export const searchUsers = createAction(
  '[User List: Search form: Search button] Search for results',
  props<SearchActionPayload>()
);

export const navigateToPageOfResults = createAction(
  '[User List Paginator] Select the page of results',
  props<SearchActionPayload>()
);

export const replaySearch = createAction(
  '[User List resolver] Replay the search by using the stored criteria, page number, page size (LOAD mode)',
  props<SearchActionPayload>()
);

export const navigateToNextPageOfResults = createAction(
  '[User List Paginator] Select the next page of results',
  props<SearchActionPayload>()
);

export const navigateToPreviousPageOfResults = createAction(
  '[User List Paginator] Select the previous page of results',
  props<SearchActionPayload>()
);

export const navigateToLastPageOfResults = createAction(
  '[User List Paginator] Select the last page of results',
  props<SearchActionPayload>()
);

export const navigateToFirstPageOfResults = createAction(
  '[User List Paginator] Select the first page of results',
  props<SearchActionPayload>()
);

export const changePageSize = createAction(
  '[User List Paginator] Change the page size of results',
  props<SearchActionPayload>()
);

export const sortResults = createAction(
  '[User List results sort column] Sort results by column',
  props<SearchActionPayload>()
);

export const usersLoaded = createAction(
  '[User list Effect] User results and pagination info loaded',
  props<{
    results: UserProjection[];
    pagination: Pagination;
    sortParameters: SortParameters;
    criteria: UserSearchCriteria;
  }>()
);

export const hideCriteria = createAction(
  '[User List show/hide button] Set the hideCriteria boolean to false'
);

export const showCriteria = createAction(
  '[User List show/hide button] Set the hideCriteria boolean to true'
);

export const clearState = createAction(
  '[User list resolver] Clear the state of User List (default mode)'
);

export const resetResultsLoaded = createAction(
  '[User list resolver] Set the resultsLoaded flag to false (LOAD mode)'
);

export const resetSuccess = createAction(
  '[User list resolver] Send command to clear the success message'
);

export const clearSuccess = createAction(
  '[User list Effect] Clear the success message'
);
