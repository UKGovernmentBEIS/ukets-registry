import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable, map } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { canGoBack } from '@registry-web/shared/shared.action';
import {
  navigateToCancelChangeTaskDeadline,
  navigateToChangeTaskDeadline,
} from '@registry-web/task-management/task-details/actions/task-details-navigation.actions';
import {
  selectTask,
  selectTaskDeadlineAsDate,
} from '@registry-web/task-management/task-details/reducers/task-details.selector';
import { TaskDetails } from '@registry-web/task-management/model';
import { submitChangedTaskDeadline } from '@registry-web/task-management/task-details/actions/task-details.actions';

@Component({
  selector: 'app-change-deadline-check-and-submit-container',
  template: `
    <app-change-deadline-check-and-submit
      [initialDeadline]="initialDeadline$ | async"
      [changedDeadline]="changedDeadline$ | async"
      (handleSubmit)="onSubmit()"
      (handleChange)="onChange()"
    ></app-change-deadline-check-and-submit>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
})
export class ChangeDeadlineCheckAndSubmitContainerComponent {
  changedDeadline$: Observable<Date>;
  initialDeadline$: Observable<Date>;
  task$: Observable<TaskDetails>;

  constructor(private store: Store, private route: ActivatedRoute) {
    this.task$ = this.store.select(selectTask);
    this.changedDeadline$ = this.store.select(selectTaskDeadlineAsDate);
    this.initialDeadline$ = this.task$.pipe(map((t) => t.deadline));

    this.store.dispatch(
      canGoBack({
        goBackRoute: `/task-details/${this.route.snapshot.paramMap.get(
          'requestId'
        )}/change-task-deadline`,
        extras: { skipLocationChange: true },
      })
    );
  }

  onSubmit() {
    this.store.dispatch(submitChangedTaskDeadline());
  }

  onChange() {
    this.store.dispatch(navigateToChangeTaskDeadline());
  }

  onCancel() {
    this.store.dispatch(
      navigateToCancelChangeTaskDeadline({
        currentRoute: `/task-details/${this.route.snapshot.paramMap.get(
          'requestId'
        )}/check-change-task-deadline`,
      })
    );
  }
}
