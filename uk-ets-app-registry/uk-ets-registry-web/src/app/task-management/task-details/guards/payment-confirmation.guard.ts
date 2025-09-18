import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';
import { Store } from '@ngrx/store';
import { fetchPaymentCompleteResponseWithExternalService } from '@task-details/actions/task-details-api.actions';
import { selectTaskCompleteResponse } from '@task-details/reducers/task-details.selector';
import { catchError, filter, first, switchMap } from 'rxjs/operators';
import { navigateToTaskDetails } from '@task-details/actions/task-details-navigation.actions';
import {
  PaymentCompleteResponse,
  RequestPaymentTaskDetails,
} from '@task-management/model';
import { Observable, of } from 'rxjs';
import { canGoBackToList } from '@shared/shared.action';
import { SearchMode } from '@shared/resolvers/search.resolver';
import { TaskDetailsActions } from '@task-details/actions';

export const paymentConfirmationGuard: CanActivateFn = (route, state) => {
  const store = inject(Store);
  store.dispatch(
    fetchPaymentCompleteResponseWithExternalService({
      requestId: route.url[0].path,
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
        store.dispatch(
          canGoBackToList({
            goBackToListRoute: `/task-list`,
            extras: {
              skipLocationChange: false,
              queryParams: {
                mode: SearchMode.LOAD,
              },
            },
          })
        );
        store.dispatch(
          TaskDetailsActions.prepareNavigationToTask({
            taskId: response.requestIdentifier,
          })
        );
        store.dispatch(navigateToTaskDetails({}));
        return of(false);
      }
    }),
    catchError(() => of(false))
  );
};
