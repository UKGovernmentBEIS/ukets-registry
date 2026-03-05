import { createSelector } from '@ngrx/store';
import { regulatorNoticeDetailsFeature } from '@regulator-notice-management/details/store/regulator-notice-details.reducer';

export const { selectNoticeDetails, selectHistory, selectLoading } =
  regulatorNoticeDetailsFeature;

export const selectRequestId = createSelector(
  selectNoticeDetails,
  (noticeDetails) => noticeDetails?.requestId
);
