import { inject, Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import * as fromPaymentList from '@payment-management/payment-list/store/reducer';
import {
  navigateAndLoadPaymentList,
  setReferenceNumber,
} from '@payment-management/payment-list/store/actions';
import { navigateTo } from '@shared/shared.action';
import { mergeMap } from 'rxjs';
import { SearchMode } from '@registry-web/shared/resolvers/search.resolver';

@Injectable()
export class PaymentListNavigationEffects {
  private actions$ = inject(Actions);
  private store = inject(Store<fromPaymentList.PaymentListState>);

  navigateAndLoadPaymentList$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(navigateAndLoadPaymentList),
      mergeMap((action) => [
        setReferenceNumber({ referenceNumber: action.referenceNumber }),
        navigateTo({
          route: '/payment-list',
          extras: {
            skipLocationChange: false,
            queryParams: {
              mode: SearchMode.LOAD,
            },
          },
        }),
      ])
    );
  });
}
