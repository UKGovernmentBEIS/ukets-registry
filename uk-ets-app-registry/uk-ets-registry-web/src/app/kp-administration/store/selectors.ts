import * as fromMessageList from '@kp-administration/store/reducers/itl-messages.reducer';
import * as fromSendMessage from '@kp-administration/store/reducers/itl-message-send.reducer';
import * as fromMessageDetails from '@kp-administration/store/reducers/itl-message-details.reducer';
import * as fromNotices from '@kp-administration/store/reducers/itl-notices.reducer';
import * as fromReconciliation from '@kp-administration/store/reducers/itl-reconciliation.reducer';

import { createFeatureSelector, createSelector } from '@ngrx/store';

export const itlFeatureKey = 'itl-feature';

export interface ItlState {
  messagesList: fromMessageList.MessageListState;
  sendMessage: fromSendMessage.SendMessageState;
  messageDetails: fromMessageDetails.MessageDetailsState;
  notices: fromNotices.NoticesState;
  reconciliation: fromReconciliation.ReconciliationState;
}

export const selectItlState = createFeatureSelector<ItlState>(itlFeatureKey);

// Send message selectors
const selectSendMessageState = createSelector(
  selectItlState,
  (state) => state.sendMessage
);
export const selectMessageId = createSelector(
  selectSendMessageState,
  (state) => state.messageId
);

// Message list selectors
const selectMessageListState = createSelector(
  selectItlState,
  (state) => state.messagesList
);
export const areCriteriaExternal = createSelector(
  selectMessageListState,
  (state) => state.externalCriteria
);
export const selectPagination = createSelector(
  selectMessageListState,
  (state) => state.pagination
);

export const selectPageParameters = createSelector(
  selectMessageListState,
  (state) => state.pageParameters
);

export const selectSortParameters = createSelector(
  selectMessageListState,
  (state) => state.sortParameters
);
export const selectResults = createSelector(
  selectMessageListState,
  (state) => state.results
);
export const selectHideCriteria = createSelector(
  selectMessageListState,
  (state) => state.hideCriteria
);
export const selectResultsLoaded = createSelector(
  selectMessageListState,
  (state) => state.resultsLoaded
);
export const selectCriteria = createSelector(
  selectMessageListState,
  (state) => state.criteria
);

// Message details selectors
const selectMessageDetailsState = createSelector(
  selectItlState,
  (state) => state.messageDetails
);

export const selectMessage = createSelector(
  selectMessageDetailsState,
  (state) => state.messageDetails
);

// Notices selectors
export const selectNoticesState = createSelector(
  selectItlState,
  (state) => state.notices
);

export const selectNotices = createSelector(
  selectNoticesState,
  (state) => state.notices
);

export const selectNoticesPagination = createSelector(
  selectNoticesState,
  (state) => state.pagination
);

export const selectNoticesPageParameters = createSelector(
  selectNoticesState,
  (state) => state.pageParameters
);

export const selectNoticesSortParams = createSelector(
  selectNoticesState,
  (state) => state.sortParameters
);

export const selectNotice = createSelector(
  selectNoticesState,
  (state) => state.notice
);

export const selectNoticeByIndex = createSelector(
  selectNoticesState,
  (state, props) => state.notice[props.index]
);

export const selectLatestKpReconciliation = createSelector(
  selectItlState,
  (state) => state.reconciliation.latestReconciliation
);
