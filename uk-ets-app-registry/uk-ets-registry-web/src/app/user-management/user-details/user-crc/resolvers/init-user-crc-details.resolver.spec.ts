import { TestBed } from '@angular/core/testing';
import { ResolveFn } from '@angular/router';

import { initUserCrcDetailsResolver } from './init-user-crc-details.resolver';

describe('initUserCrcDetailsResolver', () => {
  const executeResolver: ResolveFn<boolean> = (...resolverParameters) =>
    TestBed.runInInjectionContext(() =>
      initUserCrcDetailsResolver(...resolverParameters)
    );

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeResolver).toBeTruthy();
  });
});
