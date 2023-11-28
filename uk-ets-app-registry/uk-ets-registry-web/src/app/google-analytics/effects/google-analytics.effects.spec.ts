import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { provideMockStore } from '@ngrx/store/testing';
import { Observable } from 'rxjs';

import { GoogleAnalyticsEffects } from './google-analytics.effects';

describe('GoogleAnalyticsEffects', () => {
  let actions$: Observable<any>;
  let effects: GoogleAnalyticsEffects;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        GoogleAnalyticsEffects,
        provideMockStore(),
        provideMockActions(() => actions$),
      ],
    });

    effects = TestBed.inject(GoogleAnalyticsEffects);
  });

  it('should be created', () => {
    expect(effects).toBeTruthy();
  });
});
