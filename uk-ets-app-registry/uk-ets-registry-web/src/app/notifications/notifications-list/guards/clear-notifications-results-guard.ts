import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { Injectable } from '@angular/core';
import { clearNotificationsListRequest } from '@notifications/notifications-list/actions/notifications-list.actions';

@Injectable({
  providedIn: 'root',
})
export class ClearNotificationsResultsGuard {
  constructor(private router: Router, private store: Store) {}

  canDeactivate(
    component: any,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot
  ): boolean {
    this.store.dispatch(clearNotificationsListRequest());
    return true;
  }
}
