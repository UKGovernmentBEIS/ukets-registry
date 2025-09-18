import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import {
  selectParentRequestId,
  selectSubmittedRequestIdentifier,
} from '@request-payment/store/reducers';
import { canGoBack } from '@shared/shared.action';
import { Observable } from 'rxjs';
import { TaskDetailsActions } from '@task-management/task-details/actions';

@Component({
  selector: 'app-payment-request-submitted-container',
  template: `
    <app-payment-request-submitted
      [submittedIdentifier]="submittedIdentifier$ | async"
      [parentRequestId]="parentRequestId$ | async"
      (navigateToEmitter)="navigateTo($event)"
    ></app-payment-request-submitted>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PaymentRequestSubmittedContainerComponent implements OnInit {
  submittedIdentifier$: Observable<string>;
  parentRequestId$: Observable<string>;

  constructor(private store: Store) {}

  ngOnInit() {
    this.store.dispatch(canGoBack({ goBackRoute: null }));

    this.submittedIdentifier$ = this.store.select(
      selectSubmittedRequestIdentifier
    );

    this.parentRequestId$ = this.store.select(selectParentRequestId);
  }

  navigateTo(event: { parentRequestId: string }) {
    /* We perform this dispatch because we want to reload the parent task data with the updated sub-task list*/
    this.store.dispatch(
      TaskDetailsActions.loadTaskFromList({ taskId: event.parentRequestId })
    );
  }
}
