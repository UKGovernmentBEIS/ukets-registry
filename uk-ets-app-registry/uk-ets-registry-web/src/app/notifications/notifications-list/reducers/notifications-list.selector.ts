import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  notificationsListFeatureKey,
  NotificationsListState,
} from '@notifications/notifications-list/reducers/notifications-list.reducer';

const selectNotificationsListState =
  createFeatureSelector<NotificationsListState>(notificationsListFeatureKey);

export const selectSortParameters = createSelector(
  selectNotificationsListState,
  (state) => state.sortParameters
);

export const selectResults = createSelector(
  selectNotificationsListState,
  (state) => state.results
);

export const selectPagination = createSelector(
  selectNotificationsListState,
  (state) => state.pagination
);

export const selectPageParameters = createSelector(
  selectNotificationsListState,
  (state) => state.pageParameters
);

export const selectTimeOptions = createSelector(
  selectNotificationsListState,
  (state) => state.timeOptions
);

export const selectTimeOptionsWithNow = createSelector(
  selectNotificationsListState,
  (state) => state.timeOptionsWithNow
);

export const selectHideCriteria = createSelector(
  selectNotificationsListState,
  (state) => state.hideCriteria
);

export const selectCriteria = createSelector(
  selectNotificationsListState,
  (state) => state.criteria
);
