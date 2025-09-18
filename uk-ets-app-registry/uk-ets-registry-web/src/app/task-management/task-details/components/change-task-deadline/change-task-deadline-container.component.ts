import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable, map, take } from 'rxjs';
import { Store } from '@ngrx/store';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { canGoBack, errors } from '@shared/shared.action';
import { RequestType, TaskDetails } from '@registry-web/task-management/model';
import {
  selectTask,
  selectTaskDeadline,
} from '../../reducers/task-details.selector';
import { navigateToCancelChangeTaskDeadline } from '../../actions/task-details-navigation.actions';
import { updateTaskDeadline } from '../../actions/task-details.actions';
import { convertDateForDatepicker } from '@registry-web/shared/shared.util';

@Component({
  selector: 'app-change-task-deadline-container',
  template: `
    <app-set-deadline
      [title]="title"
      [subtitle]="subtitle"
      [sectionTitle]="sectionTitle"
      [accountName]="accountName$ | async"
      [accountNumber]="accountNumber$ | async"
      [accountHolderName]="accountHolderName$ | async"
      [recipientName]="recipientName$ | async"
      [deadline]="deadline$ | async"
      [initialDeadline]="initialDeadline$ | async"
      (setDeadline)="onContinue($event)"
      (errorDetails)="onError($event)"
    ></app-set-deadline>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChangeTaskDeadlineContainerComponent implements OnInit {
  title = 'Set new deadline for this request';
  subtitle = 'Change deadline';
  sectionTitle: string;

  accountName$: Observable<string>;
  accountNumber$: Observable<string>;
  accountHolderName$: Observable<string>;
  recipientName$: Observable<string>;
  initialDeadline$: Observable<string>;
  deadline$: Observable<any>;
  task$: Observable<TaskDetails>;

  requestId: string;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.task$ = this.store.select(selectTask);
    this.accountName$ = this.task$.pipe(
      map((task) => (task as any).accountName)
    );
    this.accountNumber$ = this.task$.pipe(
      map((task) => (task as any).accountFullIdentifier)
    );
    this.accountHolderName$ = this.task$.pipe(
      map((task) => (task as any).accountHolderName)
    );
    this.recipientName$ = this.task$.pipe(
      map((task) => (task as any).recipient)
    );
    this.initialDeadline$ = this.task$.pipe(
      map((task) => convertDateForDatepicker((task as any).deadline))
    );
    this.deadline$ = this.store.select(selectTaskDeadline);

    this.task$.pipe(take(1)).subscribe((task) => {
      this.requestId = task.requestId;
      if (task.taskType === RequestType.AH_REQUESTED_DOCUMENT_UPLOAD) {
        this.sectionTitle = 'Account details';
      } else if (task.taskType === RequestType.AR_REQUESTED_DOCUMENT_UPLOAD) {
        this.sectionTitle = 'Recipient details';
      }

      this.store.dispatch(
        canGoBack({
          goBackRoute: `/task-details/${task.requestId}`,
        })
      );
    });
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onCancel() {
    this.store.dispatch(
      navigateToCancelChangeTaskDeadline({
        currentRoute: `/task-details/${this.requestId}/change-task-deadline`,
      })
    );
  }

  onContinue(deadline: string) {
    this.store.dispatch(
      updateTaskDeadline({
        deadline: new Date(deadline),
      })
    );
  }
}
