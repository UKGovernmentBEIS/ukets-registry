import { CanActivateFn } from '@angular/router';
import { fetchPaymentViaWebLinkCompleteResponseWithExternalService } from '@task-details/actions/task-details-api.actions';
import { Store } from '@ngrx/store';
import { inject } from '@angular/core';
import {
  PaymentCompleteResponse,
  RequestPaymentTaskDetails,
} from '@task-management/model';
import { catchError, filter, first, Observable, of, switchMap } from 'rxjs';
import { selectTaskCompleteResponse } from '@task-details/reducers/task-details.selector';
import { navigateToTaskDetails } from '@task-details/actions/task-details-navigation.actions';

export const paymentWeblinkConfirmationGuard: CanActivateFn = (
  route,
  state
) => {
  const store = inject(Store);
  store.dispatch(
    fetchPaymentViaWebLinkCompleteResponseWithExternalService({
      uuid: route.url[0].path,
    })
  );

  return (<Observable<PaymentCompleteResponse>>(
    store.select(selectTaskCompleteResponse)
  )).pipe(
    filter((response) => response != undefined),
    first(),
    switchMap((response) => {
      if (
        (response.taskDetailsDTO as RequestPaymentTaskDetails).paymentStatus !==
        'FAILED'
      ) {
        return of(true);
      } else {
        store.dispatch(navigateToTaskDetails({}));
        return of(false);
      }
    }),
    catchError(() => of(false))
  );
};
