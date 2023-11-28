import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { select, Store } from '@ngrx/store';
import { selectedTasks } from '../task-list.selector';
import { first } from 'rxjs/operators';
import { Task } from '@task-management/model';

@Injectable()
export class SelectedTasksResolver {
  constructor(private store: Store) {}

  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<Task[]> {
    return this.store.pipe(select(selectedTasks), first());
  }
}
