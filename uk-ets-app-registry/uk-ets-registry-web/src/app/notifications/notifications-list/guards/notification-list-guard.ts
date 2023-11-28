import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { Injectable } from '@angular/core';
import { searchNotifications } from '@notifications/notifications-list/actions/notifications-list.actions';

@Injectable({
  providedIn: 'root',
})
export class NotificationListGuard {
  constructor(private router: Router, private store: Store) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    this.store.dispatch(
      searchNotifications({
        criteria: { type: undefined },
        pageParameters: { page: 0, pageSize: 10 },
        sortParameters: {
          sortField: 'scheduledDate',
          sortDirection: 'DESC',
        },
        potentialErrors: null,
      })
    );
    return true;
  }
}
