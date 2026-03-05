import { createFeature, createReducer } from '@ngrx/store';
import { mutableOn } from '@registry-web/shared/mutable-on';
import { RegulatorNoticeDetailsActions } from '@regulator-notice-management/details/store/regulator-notice-details.actions';
import { RegulatorNoticeTaskDetails } from '@shared/task-and-regulator-notice-management/model';
import { DomainEvent } from '@registry-web/shared/model/event';

const featureKey = 'regulatorNoticeDetails';

export interface RegulatorNoticeDetailsState {
  noticeDetails: RegulatorNoticeTaskDetails;
  history: DomainEvent[];
  loading: boolean;
  loadingError: string;
}

export const initialState: RegulatorNoticeDetailsState = {
  noticeDetails: null,
  history: [],
  loading: false,
  loadingError: null,
};

export const regulatorNoticeDetailsFeature = createFeature({
  name: featureKey,
  reducer: createReducer(
    initialState,

    mutableOn(
      RegulatorNoticeDetailsActions.LOAD_DETAILS,
      (state, { noticeDetails }) => {
        state.noticeDetails = noticeDetails;
      }
    ),

    mutableOn(
      RegulatorNoticeDetailsActions.COMPLETE_SUCCESS,
      (state, { completedNoticeDetails }) => {
        state.noticeDetails = completedNoticeDetails;
      }
    ),

    mutableOn(RegulatorNoticeDetailsActions.FETCH_HISTORY, (state) => {
      state.loading = true;
    }),

    mutableOn(
      RegulatorNoticeDetailsActions.FETCH_HISTORY_ERROR,
      (state, { error }) => {
        state.loadingError = error;
        state.loading = false;
      }
    ),

    mutableOn(
      RegulatorNoticeDetailsActions.FETCH_HISTORY_SUCCESS,
      (state, { results }) => {
        state.history = results;
        state.loading = false;
      }
    ),

    mutableOn(RegulatorNoticeDetailsActions.RESET, (state) => resetState(state))
  ),
});

function resetState(state: RegulatorNoticeDetailsState) {
  state.noticeDetails = initialState.noticeDetails;
  state.history = initialState.history;
  state.loading = initialState.loading;
  state.loadingError = initialState.loadingError;
}
