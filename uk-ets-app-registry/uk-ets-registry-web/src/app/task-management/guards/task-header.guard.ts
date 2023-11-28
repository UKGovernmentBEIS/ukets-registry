import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { TaskDetailsActions } from '@task-details/actions';
import { Observable, of } from 'rxjs';
import { catchError, filter, switchMap, take, tap } from 'rxjs/operators';

import { TaskDetails } from '@task-management/model';
import { Store } from '@ngrx/store';
import { selectTask } from '@task-details/reducers/task-details.selector';

@Injectable()
export class TaskHeaderGuard {
  constructor(private router: Router, private store: Store) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    const { requestId } = route.params;
    return this.getTask(requestId).pipe(
      switchMap(() => of(true)),
      catchError(() => of(false))
    );
  }

  private getTask(requestId: string) {
    return this.store.select(selectTask).pipe(
      tap((data) => this.prefetch(requestId, data)),
      filter(
        (data) => data.requestId && data.requestId.toString() === requestId
      ),
      take(1)
    );
  }

  private prefetch(requestId: string, data: TaskDetails) {
    if (!data.requestId) {
      this.store.dispatch(
        TaskDetailsActions.prepareNavigationToTask({ taskId: requestId })
      );
    }
  }
}
