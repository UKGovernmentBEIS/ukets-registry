import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { TaskCompleteResponse } from '@task-management/model';
import { Store } from '@ngrx/store';
import { selectTaskCompleteResponse } from '@task-details/reducers/task-details.selector';

@Component({
  selector: 'app-task-approval-confirmation-container',
  template: `
    <app-task-approval-confirmation
      [taskCompleteResponse]="taskCompleteResponse$ | async"
      [taskType]="(taskCompleteResponse$ | async).taskDetailsDTO.taskType"
    ></app-task-approval-confirmation>
  `
})
export class TaskApprovalConfirmationContainerComponent implements OnInit {
  taskCompleteResponse$: Observable<TaskCompleteResponse>;

  constructor(private store: Store) {}

  ngOnInit() {
    this.taskCompleteResponse$ = this.store.select(selectTaskCompleteResponse);
  }
}
