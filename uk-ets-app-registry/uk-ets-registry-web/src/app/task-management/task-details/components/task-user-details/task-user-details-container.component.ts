import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { TaskDetails, TaskType } from '@task-management/model';
import { selectTask } from '@task-details/reducers/task-details.selector';
import { Store } from '@ngrx/store';
import { taskTypeOptions } from '@task-management/task-list/task-list.selector';

@Component({
  selector: 'app-task-user-details-container',
  template: `
    <app-feature-header-wrapper>
      <app-task-header
        [taskDetails]="task$ | async"
        [showBackToList]="false"
        [taskHeaderActionsVisibility]="false"
        [taskTypeOptions]="taskTypeOptions$ | async"
      ></app-task-header>
    </app-feature-header-wrapper>
    <app-user-details-container> </app-user-details-container>
  `,
})
export class TaskUserDetailsContainerComponent implements OnInit {
  taskTypeOptions$: Observable<TaskType[]>;
  task$: Observable<TaskDetails> = this.store.select(selectTask);

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.task$ = this.store.select(selectTask);
    this.taskTypeOptions$ = this.store.select(taskTypeOptions);
  }
}
