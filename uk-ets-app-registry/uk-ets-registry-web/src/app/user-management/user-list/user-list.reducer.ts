import { Action, createReducer, on } from '@ngrx/store';
import {
  Pagination,
  PageParameters,
} from '../../shared/search/paginator/paginator.model';
import { mutableOn } from '@shared/mutable-on';
import { SortParameters } from 'src/app/shared/search/sort/SortParameters';
import { UserProjection, UserSearchCriteria } from './user-list.model';
import * as UserList from './user-list.actions';
import * as TaskListActions from '../../task-management/task-list/task-list.actions';

export const userListFeatureKey = 'userList';

const initialState = {
  externalCriteria: undefined,
  pageParameters: {
    page: 0,
    pageSize: 10,
  },
  pagination: undefined,
  sortParameters: {
    sortField: 'registeredOn',
    sortDirection: 'DESC',
  },
  results: undefined,
  criteria: undefined,
  hideCriteria: false,
  hideTable: true,
  resultsLoaded: false,
};

export interface UserListState {
  externalCriteria: boolean;
  pageParameters: PageParameters;
  pagination: Pagination;
  sortParameters: SortParameters;
  results: UserProjection[];
  criteria: UserSearchCriteria;
  hideCriteria: boolean;
  hideTable: boolean;
  resultsLoaded: boolean;
}

const userListReducer = createReducer(
  initialState,
  mutableOn(TaskListActions.resetResultsLoaded, (state) => {
    state.resultsLoaded = false;
  }),
  mutableOn(
    UserList.usersLoaded,
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
  mutableOn(UserList.hideCriteria, (state) => {
    state.hideCriteria = true;
  }),
  mutableOn(UserList.showCriteria, (state) => {
    state.hideCriteria = false;
  }),
  on(UserList.clearState, (state): UserListState => initialState)
);

export function reducer(state: UserListState | undefined, action: Action) {
  return userListReducer(state, action);
}
