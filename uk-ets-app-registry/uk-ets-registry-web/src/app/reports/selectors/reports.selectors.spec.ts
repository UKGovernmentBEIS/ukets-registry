import * as fromReports from '../reducers/reports.reducer';
import { selectReportsState } from './reports.selectors';

describe('Reports Selectors', () => {
  it('should select the feature state', () => {
    const result = selectReportsState({
      [fromReports.reportsFeatureKey]: {},
    });

    expect(result).toEqual({});
  });
});
