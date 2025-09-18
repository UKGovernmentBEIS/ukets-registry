import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';
import { Observable } from 'rxjs';
import {
  selectAmountRequested,
  selectTask,
} from '@task-details/reducers/task-details.selector';
import { RequestType, TaskDetails } from '@task-management/model';
import {
  navigateToBACSCancelPaymentMethod,
  navigateToBACSConfirmPaymentMethod,
} from '@task-details/actions/task-details-navigation.actions';
import { TaskDetailsActions } from '../../actions';
import { FileBase } from '@registry-web/shared/model/file';

@Component({
  selector: 'app-payment-bacs-details-container',
  template: `
    <app-payment-bacs-details
      (handleContinue)="onContinue($event)"
      [amount]="amount$ | async"
      [taskDetails]="taskDetails$ | async"
    >
    </app-payment-bacs-details>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  styles: ``,
})
export class PaymentBacsDetailsContainerComponent implements OnInit {
  amount$: Observable<number>;
  taskDetails$: Observable<TaskDetails>;

  constructor(
    private store: Store,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.amount$ = this.store.select(selectAmountRequested);
    this.taskDetails$ = this.store.select(selectTask);

    this.store.dispatch(
      canGoBack({
        goBackRoute: `/task-details/${this.route.snapshot.paramMap.get(
          'requestId'
        )}/payment-select-method`,
        extras: { skipLocationChange: true },
      })
    );
  }

  onContinue(invoice: FileBase) {
    this.store.dispatch(
      TaskDetailsActions.fetchTaskUserFile({
        taskFileDownloadInfo: {
          fileId: invoice.id,
          taskRequestId: this.route.snapshot.paramMap.get('requestId'),
          taskType: RequestType.PAYMENT_REQUEST,
        },
      })
    );
    this.store.dispatch(navigateToBACSConfirmPaymentMethod());
  }

  onCancel() {
    this.store.dispatch(navigateToBACSCancelPaymentMethod());
  }
}
