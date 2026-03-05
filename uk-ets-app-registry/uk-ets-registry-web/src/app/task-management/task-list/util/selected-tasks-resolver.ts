import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { first } from 'rxjs/operators';
import { Store } from '@ngrx/store';
import { selectedTasks } from '../store/task-list.selector';
import { Task } from '@shared/task-and-regulator-notice-management/model';

@Injectable()
export class SelectedTasksResolver {
  constructor(private store: Store) {}

  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<Task[]> {
    return this.store.select(selectedTasks).pipe(first());
  }
}
