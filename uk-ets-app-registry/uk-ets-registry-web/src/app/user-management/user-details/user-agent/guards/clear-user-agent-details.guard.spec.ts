import { TestBed } from '@angular/core/testing';
import { CanDeactivateFn } from '@angular/router';

import { clearUserAgentDetailsGuard } from './clear-user-agent-details.guard';

describe('clearUserAgentDetailsGuard', () => {
  const executeGuard: CanDeactivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() =>
      clearUserAgentDetailsGuard(...guardParameters)
    );

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
