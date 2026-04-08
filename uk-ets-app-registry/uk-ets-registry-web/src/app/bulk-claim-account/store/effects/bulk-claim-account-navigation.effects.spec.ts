import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { Observable } from 'rxjs';

import { BulkClaimAccountNavigationEffects } from './bulk-claim-account-navigation.effects';
import { provideRouter } from '@angular/router';
import { ConfirmBulkClaimAccountContainerComponent } from '@bulk-claim-account/components/confirm-bulk-claim-account/confirm-bulk-claim-account-container.component';
import { provideMockStore } from '@ngrx/store/testing';

describe('BulkClaimAccountNavigationEffects', () => {
  let actions$: Observable<any>;
  let effects: BulkClaimAccountNavigationEffects;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        BulkClaimAccountNavigationEffects,
        provideMockStore(),
        provideMockActions(() => actions$),
        provideRouter([
          {
            path: 'check-request-and-submit',
            component: ConfirmBulkClaimAccountContainerComponent,
          },
        ]),
      ],
    });

    effects = TestBed.inject(BulkClaimAccountNavigationEffects);
  });

  it('should be created', () => {
    expect(effects).toBeTruthy();
  });
});
