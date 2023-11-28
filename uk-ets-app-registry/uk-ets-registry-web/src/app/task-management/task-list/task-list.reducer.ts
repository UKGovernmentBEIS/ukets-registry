import { Action, createReducer, on } from '@ngrx/store';
import {
  AccountType,
  BulkActionSuccess,
  Task,
  TASK_TYPE_OPTIONS,
  TaskSearchCriteria,
  TaskType,
} from '@task-management/model';
import { PageParameters, Pagination } from '@shared/search/paginator';
import { mutableOn } from '@shared/mutable-on';
import * as TaskListActions from './task-list.actions';
import { indexOfTask } from './util/task.comparator';
import { SortParameters } from 'src/app/shared/search/sort/SortParameters';
import { ACCOUNT_TYPE_OPTIONS } from '@task-management/task-list/task-search/search-tasks-form/search-tasks-form.model';

export const taskListFeatureKey = 'taskList';

export interface TaskListState {
  externalCriteria: boolean;
  pageParameters: PageParameters;
  pagination: Pagination;
  sortParameters: SortParameters;
  results: Task[];
  criteria: TaskSearchCriteria;
  hideCriteria: boolean;
  showAdvancedSearch: boolean;
  hideTable: boolean;
  resultsLoaded: boolean;
  selectedTasks: Task[];
  bulkActionSuccess: BulkActionSuccess;
  taskTypeOptions: TaskType[];
  accountTypeOptions: AccountType[];
}

export const initialState: TaskListState = {
  externalCriteria: undefined,
  pageParameters: {
    page: 0,
    pageSize: 10,
  },
  pagination: undefined,
  sortParameters: {
    sortField: 'createdOn',
    sortDirection: 'DESC',
  },
  results: undefined,
  criteria: undefined,
  hideCriteria: false,
  showAdvancedSearch: false,
  hideTable: true,
  resultsLoaded: false,
  selectedTasks: [],
  bulkActionSuccess: undefined,
  taskTypeOptions: TASK_TYPE_OPTIONS,
  accountTypeOptions: ACCOUNT_TYPE_OPTIONS,
};

const taskListReducer = createReducer(
  initialState,
  mutableOn(TaskListActions.resetResultsLoaded, (state) => {
    state.resultsLoaded = false;
  }),
  mutableOn(
    TaskListActions.tasksLoaded,
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
  mutableOn(TaskListActions.hideCriteria, (state) => {
    state.hideCriteria = true;
  }),
  mutableOn(TaskListActions.showAdvancedSearch, (state, { isVisible }) => {
    state.showAdvancedSearch = isVisible;
  }),
  mutableOn(TaskListActions.showCriteria, (state) => {
    state.hideCriteria = false;
  }),
  on(TaskListActions.clearState, (state): TaskListState => initialState),
  mutableOn(
    TaskListActions.updateSelectedTasks,
    (state, { added, removed }) => {
      state.selectedTasks = state.selectedTasks
        .slice()
        .filter((task) => indexOfTask(removed, task) < 0)
        .filter((task) => indexOfTask(added, task) < 0)
        .concat(added);
    }
  ),
  mutableOn(TaskListActions.clearTasksSelection, (state) => {
    state.selectedTasks = [];
  }),
  mutableOn(TaskListActions.notifySuccess, (state, { message }) => {
    state.bulkActionSuccess = { message };
  }),
  mutableOn(TaskListActions.clearSuccess, (state) => {
    state.bulkActionSuccess = undefined;
  })
);

export function reducer(state: TaskListState | undefined, action: Action) {
  return taskListReducer(state, action);
}
