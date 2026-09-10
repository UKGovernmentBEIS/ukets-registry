import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { Observable } from 'rxjs';

import { UserAgentNavigationEffects } from './user-agent-navigation.effects';
import { provideMockStore } from '@ngrx/store/testing';

describe('UserAgentNavigationEffects', () => {
  let actions$: Observable<any>;
  let effects: UserAgentNavigationEffects;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        UserAgentNavigationEffects,
        provideMockStore(),
        provideMockActions(() => actions$),
      ],
    });

    effects = TestBed.inject(UserAgentNavigationEffects);
  });

  it('should be created', () => {
    expect(effects).toBeTruthy();
  });
});
