import { createAction, props } from '@ngrx/store';
import {
  TaskSearchCriteria,
  Task,
  SearchActionPayload,
  BulkActionPayload,
  BulkAssignPayload,
  BulkActionSuccess,
} from '../model/task-list.model';
import { Pagination } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';

export enum TaskListActionTypes {
  SEARCH_TASKS = '[Task List: Search form: Search button] Search for results',
  SELECT_RESULTS_PAGE = '[Task List Paginator] Select the page of results',
  SELECT_NEXT_RESULTS_PAGE = '[Task List Paginator] Select the next page of results',
  SELECT_PREVIOUS_RESULTS_PAGE = '[Task List Paginator] Select the previous page of results',
  SELECT_LAST_RESULTS_PAGE = '[Task List Paginator] Select the last page of results',
  SELECT_FIRST_RESULTS_PAGE = '[Task List Paginator] Select the first page of results',
  CHANGE_PAGE_SIZE = '[Task List Paginator] Change the page size of results',
  HIDE_CRITERIA = '[Task List show/hide button] Set the hideCriteria boolean to false',
  SHOW_ADVANCED_SEARCH = '[Task List advanced search button] Set the advanced search visible or hidden',
  SHOW_CRITERIA = '[Task List show/hide button] Set the hideCriteria boolean to true',
  SEARCH_RESULTS_PAGE_LOADED = '[Task list Effect] Task results and pagination info loaded',
  LOAD_TASKS = '[Task list Effect] Load the Tasks and pagination',
  CLEAR_STATE = '[Task list resolver] Clear the state of Task List (default mode)',
  RESET_RESULTS_LOADED = '[Task list resolver] Set the resultsLoaded flag to false (LOAD mode)',
  REPLAY_SEARCH = '[Task List resolver] Replay the search by using the stored criteria, page number, page size (LOAD mode)',
  CLEAR_SELECTION_OF_TASKS = '[Task List resolver] Clear the selected tasks form store (LOAD mode)',
  UPDATE_SELECTED_TASKS = '[Search Tasks results] Update the selected/checked tasks',
  BULK_CLAIM = '[Bulk Claim form submit] Claim tasks',
  BULK_ASSIGN = '[Bulk Assign form submit] Assign tasks',
  NOTIFY_WITH_SUCCESS = '[Bulk Actions Effect] Notify with success',
  SORT_RESULTS = '[Task List results sort column] Sort results by column',
  RESET_SUCCESS = '[Task list resolver] Send command to clear the success message',
  CLEAR_SUCCESS = '[Task list Effect] Clear the success message',
}

export const loadTasks = createAction(
  TaskListActionTypes.LOAD_TASKS,
  props<SearchActionPayload>()
);

export const searchTasks = createAction(
  TaskListActionTypes.SEARCH_TASKS,
  props<SearchActionPayload>()
);

export const navigateToPageOfResults = createAction(
  TaskListActionTypes.SELECT_RESULTS_PAGE,
  props<SearchActionPayload>()
);

export const replaySearch = createAction(
  TaskListActionTypes.REPLAY_SEARCH,
  props<SearchActionPayload>()
);

export const navigateToNextPageOfResults = createAction(
  TaskListActionTypes.SELECT_NEXT_RESULTS_PAGE,
  props<SearchActionPayload>()
);

export const navigateToPreviousPageOfResults = createAction(
  TaskListActionTypes.SELECT_PREVIOUS_RESULTS_PAGE,
  props<SearchActionPayload>()
);

export const navigateToLastPageOfResults = createAction(
  TaskListActionTypes.SELECT_LAST_RESULTS_PAGE,
  props<SearchActionPayload>()
);

export const navigateToFirstPageOfResults = createAction(
  TaskListActionTypes.SELECT_FIRST_RESULTS_PAGE,
  props<SearchActionPayload>()
);

export const changePageSize = createAction(
  TaskListActionTypes.CHANGE_PAGE_SIZE,
  props<SearchActionPayload>()
);

export const sortResults = createAction(
  TaskListActionTypes.SORT_RESULTS,
  props<SearchActionPayload>()
);

export const tasksLoaded = createAction(
  TaskListActionTypes.SEARCH_RESULTS_PAGE_LOADED,
  props<{
    results: Task[];
    pagination: Pagination;
    sortParameters: SortParameters;
    criteria: TaskSearchCriteria;
  }>()
);

export const hideCriteria = createAction(TaskListActionTypes.HIDE_CRITERIA);

export const showCriteria = createAction(TaskListActionTypes.SHOW_CRITERIA);

export const showAdvancedSearch = createAction(
  TaskListActionTypes.SHOW_ADVANCED_SEARCH,
  props<{ isVisible: boolean }>()
);

export const clearState = createAction(TaskListActionTypes.CLEAR_STATE);

export const clearTasksSelection = createAction(
  TaskListActionTypes.CLEAR_SELECTION_OF_TASKS
);

export const resetResultsLoaded = createAction(
  TaskListActionTypes.RESET_RESULTS_LOADED
);

export const updateSelectedTasks = createAction(
  TaskListActionTypes.UPDATE_SELECTED_TASKS,
  props<{
    added: Task[];
    removed: Task[];
  }>()
);

export const claimTasks = createAction(
  TaskListActionTypes.BULK_CLAIM,
  props<BulkActionPayload>()
);

export const assignTasks = createAction(
  TaskListActionTypes.BULK_ASSIGN,
  props<BulkAssignPayload>()
);

export const notifySuccess = createAction(
  TaskListActionTypes.NOTIFY_WITH_SUCCESS,
  props<BulkActionSuccess>()
);

export const resetSuccess = createAction(TaskListActionTypes.RESET_SUCCESS);

export const clearSuccess = createAction(TaskListActionTypes.CLEAR_SUCCESS);
