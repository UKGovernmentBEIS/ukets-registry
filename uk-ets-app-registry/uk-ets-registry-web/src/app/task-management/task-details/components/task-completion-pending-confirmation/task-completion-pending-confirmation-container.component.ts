import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { TaskDetails } from '@task-management/model';
import { Store } from '@ngrx/store';
import {
  selectNavigationAwayTargetUrl,
  selectTask,
} from '@task-details/reducers/task-details.selector';
import { canGoBackToList } from '@registry-web/shared/shared.action';
import { SearchMode } from '@registry-web/shared/resolvers/search.resolver';

@Component({
  selector: 'app-task-completion-pending-confirmation-container',
  template: `
    <app-task-completion-pending-confirmation
      [taskDetails]="taskDetails$ | async"
      [leaveUrl]="targetUrl$ | async"
    ></app-task-completion-pending-confirmation>
  `,
})
export class TaskCompletionPendingConfirmationContainerComponent
  implements OnInit
{
  taskDetails$: Observable<TaskDetails>;
  targetUrl$: Observable<string>;

  constructor(private store: Store) {}

  ngOnInit() {
    this.taskDetails$ = this.store.select(selectTask);
    this.targetUrl$ = this.store.select(selectNavigationAwayTargetUrl);

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
  }
}
