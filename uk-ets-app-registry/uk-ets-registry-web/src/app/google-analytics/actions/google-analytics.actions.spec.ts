import { GoogleAnalyticsActions } from '@google-analytics/actions';

describe('loadGoogleAnalyticssSuccess', () => {
  it('should return an action', () => {
    expect(GoogleAnalyticsActions.loadGoogleAnalyticsSuccess().type).toBe(
      '[GoogleAnalytics] Load Google Analytics Success'
    );
  });
});
