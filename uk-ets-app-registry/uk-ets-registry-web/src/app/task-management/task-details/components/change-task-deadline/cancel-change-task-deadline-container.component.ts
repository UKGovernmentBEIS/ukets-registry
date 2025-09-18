import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';
import { ActivatedRoute } from '@angular/router';
import { selectTask } from '../../reducers/task-details.selector';
import { Observable, take } from 'rxjs';
import { TaskDetails } from '@registry-web/task-management/model';
import { cancelChangeTaskDeadline } from '../../actions/task-details.actions';

@Component({
  selector: 'app-cancel-change-task-deadline-container',
  template: `
    <app-cancel-update-request
      notification="Are you sure you want to cancel the addition of the note?"
      (cancelRequest)="onCancel()"
    ></app-cancel-update-request>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelChangeTaskDeadlineContainerComponent implements OnInit {
  goBackRoute: string;
  task$: Observable<TaskDetails>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.queryParams.subscribe((params) => {
      this.goBackRoute = params.goBackRoute;
    });
    this.store.dispatch(
      canGoBack({
        goBackRoute: this.goBackRoute,
        extras: { skipLocationChange: true },
      })
    );
  }

  onCancel() {
    this.store.dispatch(cancelChangeTaskDeadline());
  }
}
