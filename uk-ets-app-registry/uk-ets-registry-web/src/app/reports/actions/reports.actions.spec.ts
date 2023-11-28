import * as fromReports from './reports.actions';

describe('loadReports', () => {
  it('should return an action', () => {
    expect(fromReports.loadReports().type).toBe('[Reports] Load Reports');
  });
});
