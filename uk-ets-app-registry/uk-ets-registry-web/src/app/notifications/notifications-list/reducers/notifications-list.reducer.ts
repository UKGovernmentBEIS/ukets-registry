import { PageParameters, Pagination } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import {
  clearNotificationsListRequest,
  hideCriteria,
  loadNotifications,
  notificationsLoaded,
  showCriteria,
} from '@notifications/notifications-list/actions/notifications-list.actions';
import {
  NotificationProjection,
  NotificationSearchCriteria,
} from '@notifications/notifications-list/model';
import {
  generateHoursOptions,
  generateHoursOptionsWithNow,
} from '@shared/shared.util';
import { Option } from '@registry-web/shared/form-controls/uk-select-input/uk-select.model';

export const notificationsListFeatureKey = 'notificationsList';

export interface NotificationsListState {
  externalCriteria: boolean;
  pageParameters: PageParameters;
  pagination: Pagination;
  sortParameters: SortParameters;
  results: NotificationProjection[];
  criteria: NotificationSearchCriteria;
  hideCriteria: boolean;
  hideTable: boolean;
  resultsLoaded: boolean;
  timeOptions: Option[];
  timeOptionsWithNow: Option[];
}

const initialState: NotificationsListState = {
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
  criteria: {
    type: undefined,
  },
  hideCriteria: true,
  hideTable: true,
  resultsLoaded: false,
  timeOptions: null,
  timeOptionsWithNow: null,
};

const notificationsListReducer = createReducer(
  initialState,
  mutableOn(loadNotifications, (state) => {
    state.timeOptions = generateHoursOptions();
    state.timeOptionsWithNow = generateHoursOptionsWithNow();
  }),
  mutableOn(
    notificationsLoaded,
    (state, { criteria, results, pagination, sortParameters }) => {
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
  mutableOn(clearNotificationsListRequest, (state) => {
    resetState(state);
  }),
  mutableOn(hideCriteria, (state) => {
    state.hideCriteria = true;
  }),
  mutableOn(showCriteria, (state) => {
    state.hideCriteria = false;
  })
);

export function reducer(
  state: NotificationsListState | undefined,
  action: Action
) {
  return notificationsListReducer(state, action);
}

function resetState(state) {
  state.externalCriteria = initialState.externalCriteria;
  state.pageParameters = initialState.pageParameters;
  state.pagination = initialState.pagination;
  state.sortParameters = initialState.sortParameters;
  state.results = initialState.results;
  state.hideTable = initialState.hideTable;
  state.resultsLoaded = initialState.resultsLoaded;
  state.timeOptions = initialState.timeOptions;
  state.hideCriteria = true;
}
