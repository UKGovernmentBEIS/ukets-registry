import { createFeatureSelector } from '@ngrx/store';
import { GoogleAnalyticsReducers } from '@google-analytics/reducers';
import { RouterReducerState } from '@ngrx/router-store';

export const selectGoogleAnalyticsState = createFeatureSelector<GoogleAnalyticsReducers.GoogleAnalyticsMetrics>(
  GoogleAnalyticsReducers.googleAnalyticsFeatureKey
);
export const selectRouter = createFeatureSelector<RouterReducerState>('router');
