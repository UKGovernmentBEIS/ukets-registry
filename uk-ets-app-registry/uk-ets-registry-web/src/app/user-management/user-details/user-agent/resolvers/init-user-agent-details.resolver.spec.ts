import { TestBed } from '@angular/core/testing';
import { ResolveFn } from '@angular/router';

import { initUserAgentDetailsResolver } from './init-user-agent-details.resolver';

describe('initUserAgentDetailsResolver', () => {
  const executeResolver: ResolveFn<boolean> = (...resolverParameters) =>
    TestBed.runInInjectionContext(() =>
      initUserAgentDetailsResolver(...resolverParameters)
    );

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeResolver).toBeTruthy();
  });
});
