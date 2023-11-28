import * as fromGoogleAnalytics from '../reducers/google-analytics.reducer';
import { selectGoogleAnalyticsState } from './google-analytics.selectors';

describe('GoogleAnalytics Selectors', () => {
  it('should select the feature state', () => {
    const result = selectGoogleAnalyticsState({
      [fromGoogleAnalytics.googleAnalyticsFeatureKey]: {},
    });

    expect(result).toEqual({});
  });
});
