import { createReducer } from '@ngrx/store';
import { mutableOn } from '@registry-web/shared/mutable-on';
import { GoogleAnalyticsActions } from '@google-analytics/actions';

export const googleAnalyticsFeatureKey = 'googleAnalytics';

export interface GoogleAnalyticsMetrics {
  name: string;
  time: number;
  error: any;
}

export const initialState: GoogleAnalyticsMetrics = {
  name: '',
  time: null,
  error: [],
};

export const goals = {
  userRegistration: {
    start: '/registration/personal-details',
    end: '',
  },
  signIn: {
    start: '',
    end: '',
  },
  accountOpening: {
    start: '',
    end: '',
  },
  submitDocuments: {
    start: '/task-details',
    end: '/task-details/submit-docs/submitted',
  },
};

export const reducer = createReducer(
  initialState,

  mutableOn(
    GoogleAnalyticsActions.loadGoogleAnalyticsFailure,
    (state, { error }) => {
      state.name = '';
      state.time = null;
      state.error = error;
    }
  ),

  mutableOn(
    GoogleAnalyticsActions.updateGoogleAnalyticsMetrics,
    (state, { name, time }) => {
      state.name = name;
      state.time = time;
    }
  ),

  mutableOn(GoogleAnalyticsActions.resetGoogleAnalyticsMetrics, (state) => {
    state.name = '';
    state.time = null;
    state.error = [];
  })
);
