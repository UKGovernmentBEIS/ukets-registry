import { TestBed } from '@angular/core/testing';
import { CanDeactivateFn } from '@angular/router';

import { clearUserCrcDetailsGuard } from './clear-user-crc-details.guard';

describe('clearUserCrcDetailsGuard', () => {
  const executeGuard: CanDeactivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() =>
      clearUserCrcDetailsGuard(...guardParameters)
    );

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
