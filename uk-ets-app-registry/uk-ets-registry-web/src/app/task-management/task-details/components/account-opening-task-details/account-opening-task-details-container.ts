import { Component, Input } from '@angular/core';
import {
  AccountOpeningTaskDetails,
  TaskUpdateDetails,
} from '@task-management/model';
import { Store } from '@ngrx/store';
import { clearErrors } from '@shared/shared.action';
import { updateTask } from '@task-details/actions/task-details.actions';
import { Observable } from 'rxjs';
import { isSeniorOrJuniorAdmin } from '@registry-web/auth/auth.selector';

@Component({
  selector: 'app-account-opening-task-details-container',
  template: `
    <app-account-opening-task-details
      [taskDetails]="taskDetails"
      [isSeniorOrJuniorAdmin]="isSeniorOrJuniorAdmin$ | async"
      (triggerTaskUpdate)="updateTask($event)"
    ></app-account-opening-task-details>
  `,
})
export class AccountOpeningTaskDetailsContainerComponent {
  @Input()
  taskDetails: AccountOpeningTaskDetails;
  
  isSeniorOrJuniorAdmin$:Observable<boolean> = this.store.select(isSeniorOrJuniorAdmin);

  constructor(private store: Store) {}

  updateTask(model: TaskUpdateDetails) {
    this.store.dispatch(clearErrors());
    this.store.dispatch(
      updateTask({
        updateInfo: model.updateInfo,
        taskUpdateAction: model.taskUpdateAction,
        taskDetails: model.taskDetails,
      })
    );
  }
}
