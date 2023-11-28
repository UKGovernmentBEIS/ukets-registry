import { createAction, props } from '@ngrx/store';
import { NavigationExtras, Params } from '@angular/router';
import {
  NotificationProjection,
  NotificationSearchCriteria,
  SearchActionPayload,
} from '@notifications/notifications-list/model';
import { Pagination } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';

export const navigateTo = createAction(
  '[Notifications-list] Navigate to',
  props<{ route: string; extras?: NavigationExtras; queryParams?: Params }>()
);

export const searchNotifications = createAction(
  '[Notifications-list] Search for results',
  props<SearchActionPayload>()
);

export const loadNotifications = createAction(
  '[Notifications-list] Load the notifications and pagination',
  props<SearchActionPayload>()
);

export const navigateToNextPageOfResults = createAction(
  '[Notifications-list] Next notification list page',
  props<SearchActionPayload>()
);

export const navigateToPreviousPageOfResults = createAction(
  '[Notifications-list] Previous notification list page',
  props<SearchActionPayload>()
);

export const navigateToLastPageOfResults = createAction(
  '[Notifications-list] Last notification list page',
  props<SearchActionPayload>()
);

export const navigateToFirstPageOfResults = createAction(
  '[Notifications-list] First notification list page',
  props<SearchActionPayload>()
);

export const changePageSize = createAction(
  '[Notifications-list] Notification list change page size',
  props<SearchActionPayload>()
);

export const sortResults = createAction(
  '[Notifications-list] Notification list sort results',
  props<SearchActionPayload>()
);

export const notificationsLoaded = createAction(
  '[Notifications-list] Notification results and pagination info loaded',
  props<{
    criteria: NotificationSearchCriteria;
    results: NotificationProjection[];
    pagination: Pagination;
    sortParameters: SortParameters;
  }>()
);

export const clearNotificationsListRequest = createAction(
  '[Notifications-list] Clear Request'
);

export const hideCriteria = createAction(
  '[Notification List show/hide button] Set the hideCriteria boolean to false'
);

export const showCriteria = createAction(
  '[Notification List show/hide button] Set the hideCriteria boolean to true'
);
