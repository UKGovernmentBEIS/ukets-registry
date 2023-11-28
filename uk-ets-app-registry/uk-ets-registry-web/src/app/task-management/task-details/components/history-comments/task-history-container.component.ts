import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import {
  selectTask,
  selectTaskHistory,
} from '@task-details/reducers/task-details.selector';
import { DomainEvent } from '@shared/model/event';
import { TaskDetails, TaskType } from '@task-management/model';
import { taskTypeOptions } from '@task-management/task-list/task-list.selector';
import { isAdmin } from '@registry-web/auth/auth.selector';
import { navigateToUserProfile } from '@shared/shared.action';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-task-history-container',
  template: `
    <app-feature-header-wrapper>
      <app-task-header
        [taskDetails]="task$ | async"
        [showBackToList]="false"
        [taskHeaderActionsVisibility]="false"
        [taskTypeOptions]="taskTypeOptions$ | async"
      ></app-task-header>
    </app-feature-header-wrapper>
    <app-history-comments [task]="task$ | async"></app-history-comments>
    <app-domain-events
      [domainEvents]="taskHistory$ | async"
      [isAdmin]="isAdmin$ | async"
      (navigate)="navigateToUserPage($event)"
    ></app-domain-events>
  `,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HistoryContainerComponent implements OnInit {
  taskHistory$: Observable<DomainEvent[]>;
  taskTypeOptions$: Observable<TaskType[]>;
  task$: Observable<TaskDetails> = this.store.select(selectTask);
  isAdmin$: Observable<boolean>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.taskHistory$ = this.store.select(selectTaskHistory);
    this.task$ = this.store.select(selectTask);
    this.taskTypeOptions$ = this.store.select(taskTypeOptions);
    this.isAdmin$ = this.store.select(isAdmin);
  }

  navigateToUserPage(urid: string) {
    const goBackRoute = this.route.snapshot['_routerState'].url;
    const userProfileRoute = '/user-details/' + urid;
    this.store.dispatch(
      navigateToUserProfile({ goBackRoute, userProfileRoute })
    );
  }
}
