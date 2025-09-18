import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { PaymentCompleteResponse } from '@task-management/model';
import { canGoBackToList, clearGoBackRoute } from '@shared/shared.action';
import { navigateToTaskDetails } from '@task-details/actions/task-details-navigation.actions';
import { selectTaskCompleteResponse } from '@task-details/reducers/task-details.selector';
import { Observable } from 'rxjs';
import { downloadPaymentReceipt } from '@task-details/actions/task-details.actions';
import { SearchMode } from '@shared/resolvers/search.resolver';
import { isAuthenticated } from '@registry-web/auth/auth.selector';

@Component({
  selector: 'app-payment-confirmation-container',
  template: `
    <app-payment-confirmation
      [paymentCompletionDetails]="taskCompleteResponse$ | async"
      [isAuthenticated]="isAuthenticated$ | async"
      (navigateToTaskDetailsEmitter)="onBackToTask()"
      (downloadReceiptEmitter)="onDownloadReceiptEmitter()"
    ></app-payment-confirmation>
  `,
  styles: ``,
})
export class PaymentConfirmationContainerComponent implements OnInit {
  taskCompleteResponse$: Observable<PaymentCompleteResponse>;
  isAuthenticated$: Observable<boolean>;

  constructor(private store: Store) {}

  ngOnInit() {
    this.store.dispatch(clearGoBackRoute());
    this.taskCompleteResponse$ = <Observable<PaymentCompleteResponse>>(
      this.store.select(selectTaskCompleteResponse)
    );
    this.isAuthenticated$ = this.store.select(isAuthenticated);
  }

  onBackToTask() {
    this.store.dispatch(
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
    this.store.dispatch(navigateToTaskDetails({}));
  }

  onDownloadReceiptEmitter() {
    this.store.dispatch(downloadPaymentReceipt());
  }
}
