import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { Observable } from 'rxjs';

import { UserAgentEffects } from './user-agent.effects';
import { ApiErrorHandlingService } from '@shared/services/api-error-handling.service';
import { UserAgentService } from '@user-agent/service';
import { provideMockStore } from '@ngrx/store/testing';

describe('UserAgentEffects', () => {
  let actions$: Observable<any>;
  let effects: UserAgentEffects;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        UserAgentEffects,
        ApiErrorHandlingService,
        {
          provide: UserAgentService,
          useValue: {
            fetchAllocation: jest.fn(),
          },
        },
        provideMockStore(),
        provideMockActions(() => actions$),
      ],
    });

    effects = TestBed.inject(UserAgentEffects);
  });

  test('should be created', () => {
    expect(effects).toBeTruthy();
  });
});
