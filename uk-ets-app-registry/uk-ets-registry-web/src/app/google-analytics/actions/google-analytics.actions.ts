import { createAction, props } from '@ngrx/store';

export const loadGoogleAnalyticsSuccess = createAction(
  '[GoogleAnalytics] Load Google Analytics Success'
);

export const loadGoogleAnalyticsFailure = createAction(
  '[GoogleAnalytics] Load Google Analytics Failure',
  props<{ error: any }>()
);

export const updateGoogleAnalyticsMetrics = createAction(
  '[GoogleAnalytics] Update Google Analytics tracking time',
  props<{ name: string; time: number }>()
);

export const resetGoogleAnalyticsMetrics = createAction(
  '[GoogleAnalytics] Reset Google Analytics tracking time'
);

export const sendPageView = createAction('[GoogleAnalytics] Send a page view');

export const sendCustomPageView = createAction(
  '[GoogleAnalytics] Send a custom page view'
);

export const googleAnalyticsGoalSuccess = createAction(
  '[GoogleAnalytics] Goal successfully tracked'
);

export const googleAnalyticsGoalFailure = createAction(
  '[GoogleAnalytics] Goal failed'
);
